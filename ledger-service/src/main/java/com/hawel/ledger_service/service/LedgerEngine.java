package com.hawel.ledger_service.service;


import com.hawel.common_service.enums.CurrencyCode;
import com.hawel.common_service.event.JournalCompletedEvent;
import com.hawel.ledger_service.domain.LedgerPosting;
import com.hawel.ledger_service.entity.*;
import com.hawel.ledger_service.enums.AccountStatus;
import com.hawel.ledger_service.enums.EntryType;
import com.hawel.ledger_service.enums.JournalStatus;
import com.hawel.ledger_service.enums.JournalType;
import com.hawel.ledger_service.exception.*;
import com.hawel.ledger_service.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Slf4j
public class LedgerEngine {


    private final AccountRepository accountRepository;

    private final BalanceRepository balanceRepository;

    private final JournalEntryRepository journalRepository;

    private final LedgerEntryRepository ledgerEntryRepository;

    private final JournalNumberGenerator journalNumberGenerator;

    private final OutboxService outboxService;


    @Transactional
    public JournalEntry postJournal(JournalType type, UUID transactionId, String reference, String description, List<LedgerPosting> postings) {

        log.info("Starting journal posting: type={}, transactionId={}, reference={}", type, transactionId, reference);

        // 1- Check for duplicate reference
        checkDuplicate(reference);

        // 2- Validate postings
        validatePostings(postings);

        // 3- Load accounts
        Map<UUID, Account> accounts = loadAccounts(postings);

        // 4- Validate that all accounts are active and exist
        validateAccounts(postings, accounts);

        // 5- Journal
        JournalEntry journal = createJournal(type, transactionId, reference, description);

        // 6- save ledger entries for the postings
        saveLedgerEntries(journal, postings, accounts);

        // 7- Lock balances for the accounts involved in the postings to prevent concurrent modifications
        Map<UUID, Balance> balances = lockBalances(accounts.values());

        // 8- update balances for the accounts involved in the postings
        updateBalances(postings, balances);

        // 9- the journal completed after all postings are saved and balances are updated
        completeJournal(journal);

        // 10- create an outbox event for the completed journal to be published to other services
        createJournalCompletedOutboxEvent(journal, postings, accounts, balances);

        log.info("Journal posted successfully: journalId={}, journalNumber={}, reference={}", journal.getId(), journal.getJournalNumber(), journal.getReference());

        return journal;
    }


    private void validatePostings(List<LedgerPosting> postings) {

        log.info("Validating ledger postings. postingsCount={}", postings == null ? 0 : postings.size());

        if (postings == null || postings.isEmpty()) {

            log.error("Ledger postings are empty");

            throw new InvalidJournalException("Ledger postings are required.");
        }

        // Here will validate that all postings have a valid amount (greater than zero)
        validateAmounts(postings);

        // here will ask to validate that all postings have the same currency
        validateCurrencies(postings);

        // Here will ask to validate that the journal is balanced (total debits == total credits)
        validateBalancedJournal(postings);

        log.info("Ledger postings validation passed successfully");

    }

    private JournalEntry createJournal(JournalType type, UUID transactionId, String reference, String description) {

        log.info("Creating journal. type={}, transactionId={}, reference={}, description={}", type, transactionId, reference, description);

        String journalNumber = journalNumberGenerator.generate();

        log.info("Journal number generated. journalNumber={}, transactionId={}", journalNumber, transactionId);

        JournalEntry journal = JournalEntry.builder()
                .journalNumber(journalNumber)
                .transactionId(transactionId)
                .reference(reference)
                .type(type)
                .status(JournalStatus.PENDING)
                .description(description)
                .build();

        log.info("Journal entity created. journalNumber={}, status={}, type={}", journal.getJournalNumber(), journal.getStatus(), journal.getType());

        JournalEntry savedJournal = journalRepository.save(journal);

        log.info("Journal saved successfully. journalId={}, journalNumber={}, transactionId={}", savedJournal.getId(), savedJournal.getJournalNumber(), savedJournal.getTransactionId());

        return savedJournal;

    }


    // Idempotency check: Ensure that the journal reference is unique to prevent duplicate postings for the same transaction.
    private void checkDuplicate(String reference) {

        log.info("Checking duplicate journal reference. reference={}", reference);

        if (journalRepository.existsByReference(reference)) {

            log.warn("Duplicate journal reference detected. reference={}", reference);

            throw new DuplicateJournalException(reference);
        }

        log.info("Journal reference is unique. reference={}", reference);

    }


    private void validateAmounts(List<LedgerPosting> postings) {

        log.info("Validating posting amounts. count={}", postings.size());

        for (LedgerPosting posting : postings) {

            log.info("Validating posting amount. accountId={}, entryType={}, amount={}, currency={}", posting.getAccountId(), posting.getEntryType(), posting.getAmount(), posting.getCurrency());

            if (posting.getAmount() == null) {

                log.error("Posting amount is null. accountId={}", posting.getAccountId());

                throw new LedgerException("INVALID_AMOUNT", "Amount is required for ledger posting.");
            }

            if (posting.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

                log.error("Invalid posting amount. accountId={}, amount={}", posting.getAccountId(), posting.getAmount());

                throw new LedgerException("INVALID_AMOUNT", "Amount must be greater than zero for ledger posting.");
            }

        }

        log.info("Posting amounts validation passed");

    }

    private void validateCurrencies(List<LedgerPosting> postings) {

        CurrencyCode currency = postings.get(0).getCurrency();

        log.info("Validating posting currencies. expectedCurrency={}", currency);

        for (LedgerPosting posting : postings) {

            log.info("Checking posting currency. accountId={}, currency={}", posting.getAccountId(), posting.getCurrency());

            if (posting.getCurrency() != currency) {

                log.error("Currency mismatch. expected={}, actual={}, accountId={}", currency, posting.getCurrency(), posting.getAccountId());

                throw new CurrencyMismatchException(currency, posting.getCurrency());
            }
        }

        log.info("Posting currencies validation passed. currency={}", currency);

    }

    private void validateBalancedJournal(List<LedgerPosting> postings) {

        BigDecimal debit = postings.stream()
                .filter(p -> p.getEntryType() == EntryType.DEBIT)
                .map(LedgerPosting::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal credit = postings.stream()
                .filter(p -> p.getEntryType() == EntryType.CREDIT)
                .map(LedgerPosting::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Validating journal balance. debit={}, credit={}, difference={}", debit, credit, debit.subtract(credit));

        if (debit.compareTo(credit) != 0) {

            log.error("Journal is not balanced. debit={}, credit={}", debit, credit);

            throw new JournalNotBalancedException(debit, credit);

        }

        log.info("Journal balance validation passed. debit={}, credit={}", debit, credit);

    }


    private Map<UUID, Account> loadAccounts(List<LedgerPosting> postings) {

        log.info("Loading accounts for ledger postings. postingsCount={}", postings.size());

        Set<UUID> accountIds = postings.stream().map(LedgerPosting::getAccountId).collect(Collectors.toSet());

        log.info("Account IDs required for ledger postings. accountIds={}, count={}", accountIds, accountIds.size());

        List<Account> accounts = accountRepository.findAllById(accountIds);

        log.info("Accounts loaded from database. requestedCount={}, foundCount={}", accountIds.size(), accounts.size());


        if (accounts.size() != accountIds.size()) {

            Set<UUID> found = accounts.stream().map(Account::getId).collect(Collectors.toSet());

            accountIds.removeAll(found);

            log.error("Some accounts were not found. missingAccountIds={}", accountIds);

            throw new AccountNotFoundException(accountIds.iterator().next());

        }


        Map<UUID, Account> accountMap = accounts.stream().collect(Collectors.toMap(Account::getId, Function.identity()));

        log.info("Accounts loaded successfully and mapped. accountMapSize={}", accountMap.size());

        return accountMap;

    }


    // Lock balances for the given accounts to prevent concurrent modifications
    // يعني لو عمليتان تحاولان تعديل نفس الحساب في نفس اللحظة، واحدة تنتظر الثانية بدل أن يحصل Race Condition.
    private Map<UUID, Balance> lockBalances(Collection<Account> accounts) {

        log.info("Starting balance locking. accountsCount={}", accounts.size());

        // Sort accounts by ID to avoid deadlocks when locking balances
        List<Account> sortedAccounts = accounts.stream().sorted(Comparator.comparing(Account::getId)).collect(Collectors.toList());

        log.info("Accounts sorted for balance locking. accountIds={}", sortedAccounts.stream().map(Account::getId).collect(Collectors.toList()));

        Map<UUID, Balance> balances = new HashMap<>();

        for (Account account : sortedAccounts) {

            log.info("Locking balance for account. accountId={}, accountNumber={}, currency={}", account.getId(), account.getAccountNumber(), account.getCurrency());

            Balance balance = balanceRepository.findByAccountIdForUpdate(account.getId()).orElseThrow(() -> {

                log.error("Balance not found for account. accountId={}, currency={}", account.getId(), account.getCurrency());

                return new BalanceNotFoundException(account.getId(), account.getCurrency().name());
            });

            log.info("Balance locked successfully for account. accountId={}, availableBalance={}, blockedBalance={}", account.getId(), balance.getAvailableBalance(), balance.getBlockedBalance());

            balances.put(account.getId(), balance);

        }

        log.info("All balances locked successfully. balancesCount={}", balances.size());

        return balances;
    }

    private void saveLedgerEntries(JournalEntry journal, List<LedgerPosting> postings, Map<UUID, Account> accounts) {


        log.info("Saving ledger entries. journalId={}, journalNumber={}, postingsCount={}", journal.getId(), journal.getJournalNumber(), postings.size());


        List<LedgerEntry> entries = postings.stream()
                .map(posting -> {

                    Account account = accounts.get(posting.getAccountId());

                    log.info(
                            "Creating ledger entry. journalNumber={}, accountId={}, accountNumber={}, entryType={}, amount={}, currency={}",
                            journal.getJournalNumber(),
                            posting.getAccountId(),
                            account != null ? account.getAccountNumber() : null,
                            posting.getEntryType(),
                            posting.getAmount(),
                            posting.getCurrency()
                    );

                    return LedgerEntry.builder()
                            .journal(journal)
                            .account(account)
                            .entryType(posting.getEntryType())
                            .amount(posting.getAmount())
                            .currency(posting.getCurrency())
                            .build();
                }).collect(Collectors.toList());

        log.info("Ledger entries created. journalNumber={}, entriesCount={}", journal.getJournalNumber(), entries.size());

        ledgerEntryRepository.saveAll(entries);

        log.info("Ledger entries saved successfully. journalId={}, journalNumber={}, entriesCount={}", journal.getId(), journal.getJournalNumber(), entries.size());

    }


    private void updateBalances(List<LedgerPosting> postings, Map<UUID, Balance> balances) {

        log.info("Updating balances for ledger postings. postingsCount={}, balancesCount={}", postings.size(), balances.size());

        for (LedgerPosting posting : postings) {

            Balance balance = balances.get(posting.getAccountId());

            if (posting.getEntryType() == EntryType.DEBIT) {

                debit(balance, posting.getAmount());

            } else {

                credit(balance, posting.getAmount());

            }

            log.info("Balance updated. accountId={}, newAvailableBalance={}, newBlockedBalance={}", balance.getAccount().getId(), balance.getAvailableBalance(), balance.getBlockedBalance());

        }

        balanceRepository.saveAll(balances.values());

        log.info("All balances updated and saved successfully. balancesCount={}", balances.size());

    }

    private void credit(Balance balance, BigDecimal amount) {

        log.info("Attempting to credit balance. accountId={}, creditAmount={}, currentAvailableBalance={}", balance.getAccount().getId(), amount, balance.getAvailableBalance());

        balance.setAvailableBalance(balance.getAvailableBalance().add(amount));

        log.info("Balance credited. accountId={}, creditedAmount={}, newAvailableBalance={}", balance.getAccount().getId(), amount, balance.getAvailableBalance());

    }

    private void debit(Balance balance, BigDecimal amount) {

        log.info("Attempting to debit balance. accountId={}, debitAmount={}, currentAvailableBalance={}", balance.getAccount().getId(), amount, balance.getAvailableBalance());

        if (balance.getAvailableBalance().compareTo(amount) < 0) {

            throw new InsufficientFundsException(balance.getAccount().getId(), balance.getAvailableBalance(), amount);

        }

        balance.setAvailableBalance(balance.getAvailableBalance().subtract(amount));

        log.info("Balance debited. accountId={}, debitedAmount={}, newAvailableBalance={}", balance.getAccount().getId(), amount, balance.getAvailableBalance());

    }

    private void completeJournal(JournalEntry journal) {

        log.info("Completing journal. journalId={}, journalNumber={}", journal.getId(), journal.getJournalNumber());

        journal.setStatus(JournalStatus.COMPLETED);

        log.info("Journal status updated to COMPLETED. journalId={}, journalNumber={}", journal.getId(), journal.getJournalNumber());

    }

    private void validateAccounts(List<LedgerPosting> postings, Map<UUID, Account> accounts) {

        log.info("Validating ledger accounts. postingsCount={}, accountsLoaded={}", postings.size(), accounts.size());

        for (LedgerPosting posting : postings) {

            // get : without make new query to database, because we already load all accounts in loadAccounts method
            Account account = accounts.get(posting.getAccountId());

            log.info("Validating account. accountId={}, entryType={}, amount={}, currency={}", posting.getAccountId(), posting.getEntryType(), posting.getAmount(), posting.getCurrency());

            if (account == null) {

                log.error("Account not found in loaded accounts. accountId={}", posting.getAccountId());

                throw new AccountNotFoundException(posting.getAccountId());
            }

            log.info("Account found. accountId={}, accountNumber={}, accountType={}, status={}, currency={}", account.getId(), account.getAccountNumber(), account.getAccountType(), account.getStatus(), account.getCurrency());

            // Check if account is active, if not throw exception
            validateAccountStatus(account);

            log.info("Account status validation passed. accountId={}, status={}", account.getId(), account.getStatus());

        }

        log.info("All ledger accounts validated successfully");

    }

    private void validateAccountStatus(Account account) {

        // in future if want add FREE or SUSPENDED status, we can add them to the check below

        if (account.getStatus() != AccountStatus.ACTIVE) {

            throw new AccountNotActiveException(account.getId(), account.getStatus());

        }

    }

    private void createJournalCompletedOutboxEvent(JournalEntry journal, List<LedgerPosting> postings, Map<UUID, Account> accounts, Map<UUID, Balance> balances) {

        log.info("Creating JOURNAL_COMPLETED outbox event: journalId={}, journalNumber={}", journal.getId(), journal.getJournalNumber());

        BigDecimal amount = postings.stream()
                .filter(p -> p.getEntryType() == EntryType.DEBIT)
                .map(LedgerPosting::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CurrencyCode currency = postings.get(0).getCurrency();

        // to get wallet id
        UUID walletId = resolveWalletId(postings, accounts);

        // to get wallet balance
        UUID walletAccountId = resolveWalletAccountId(postings, accounts, walletId);

        Balance walletBalance = balances.get(walletAccountId);

        if (walletBalance == null) {
            throw new LedgerException(
                    "BalanceNotFoundException",
                    "Balance Not Found  for accountId = {}" + walletAccountId
            );
        }

        JournalCompletedEvent event = new JournalCompletedEvent(
                journal.getTransactionId(),
                journal.getId(),
                walletId,
                journal.getJournalNumber(),
                journal.getReference(),
                journal.getType().name(),
                currency,
                amount,
                walletBalance.getAvailableBalance(),
                walletBalance.getBlockedBalance(),
                journal.getStatus().name()
        );

        outboxService.saveJournalCompletedEvent("JOURNAL", journal.getId(), "JOURNAL_COMPLETED", event);

        log.info("JOURNAL_COMPLETED outbox event created: journalId={}, amount={}, currency={}", journal.getId(), amount, currency);

    }

    private UUID resolveWalletId(List<LedgerPosting> postings, Map<UUID, Account> accounts) {

        for (LedgerPosting posting : postings) {

            Account account = accounts.get(posting.getAccountId());

            if (account == null) {
                continue;
            }

            if ("WALLET".equals(account.getOwnerType().name())) {

                return account.getOwnerId();
            }
        }

        return null;
    }


    private UUID resolveWalletAccountId(List<LedgerPosting> postings, Map<UUID, Account> accounts, UUID walletId) {
        return postings.stream()
                .map(LedgerPosting::getAccountId)
                .filter(accountId -> {
                    Account account = accounts.get(accountId);

                    return account != null
                            && "WALLET".equals(account.getOwnerType().name())
                            && walletId.equals(account.getOwnerId());
                })
                .findFirst()
                .orElseThrow(() -> new LedgerException(
                        "WALLET_ACCOUNT_NOT_FOUND",
                        "Unable to find wallet account for wallet: " + walletId
                ));
    }


}