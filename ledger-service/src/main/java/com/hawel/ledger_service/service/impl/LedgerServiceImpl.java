package com.hawel.ledger_service.service.impl;

import com.hawel.common_service.dto.ledger.*;
import com.hawel.ledger_service.domain.LedgerPosting;
import com.hawel.ledger_service.dto.request.*;
import com.hawel.ledger_service.entity.Account;
import com.hawel.ledger_service.entity.JournalEntry;
import com.hawel.ledger_service.enums.JournalType;
import com.hawel.ledger_service.enums.OwnerType;
import com.hawel.ledger_service.factory.LedgerPostingFactory;
import com.hawel.ledger_service.mapper.JournalMapper;
import com.hawel.ledger_service.repository.AccountRepository;
import com.hawel.ledger_service.service.LedgerEngine;
import com.hawel.ledger_service.service.LedgerService;

import com.hawel.ledger_service.service.SystemAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LedgerServiceImpl implements LedgerService {

    private final LedgerEngine ledgerEngine;

    private final JournalMapper journalMapper;

    private final SystemAccountService systemAccountService;

    private final AccountRepository accountRepository;


    @Override
    @Transactional
    public JournalResponse transfer(TransferJournalRequest request) {

        log.info(
                "Starting transfer: transactionId={}, reference={}, fromAccountId={}, toAccountId={}, amount={}, currency={}",
                request.getTransactionId(),
                request.getReference(),
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount(),
                request.getCurrencyCode()
        );

        List<LedgerPosting> postings = Arrays.asList(

                // Create a debit posting for the fromAccountId
                LedgerPostingFactory.debit(
                        request.getFromAccountId(),
                        request.getAmount(),
                        request.getCurrencyCode()
                ),

                // Create a credit posting for the toAccountId
                LedgerPostingFactory.credit(
                        request.getToAccountId(),
                        request.getAmount(),
                        request.getCurrencyCode()
                )
        );

        log.debug(
                "Created transfer postings: debitAccount={}, creditAccount={}, amount={}, currency={}",
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount(),
                request.getCurrencyCode()
        );

        JournalEntry journal = ledgerEngine.postJournal(
                JournalType.TRANSFER,
                request.getTransactionId(),
                request.getReference(),
                request.getDescription(),
                postings
        );

        log.info(
                "Transfer journal posted successfully: journalId={}, transactionId={}, reference={}, fromAccountId={}, toAccountId={}, amount={}, currency={}",
                journal.getId(),
                request.getTransactionId(),
                request.getReference(),
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount(),
                request.getCurrencyCode()
        );

        return journalMapper.toResponse(journal);
    }

    @Override
    @Transactional
    public JournalResponse deposit(DepositJournalRequest request) {


        log.info(
                "Starting deposit: transactionId={}, reference={}, accountId={}, amount={}, currency={}",
                request.getTransactionId(),
                request.getReference(),
                request.getAccountId(),
                request.getAmount(),
                request.getCurrencyCode()
        );

        UUID cashAccount = systemAccountService.getCashAccountId(request.getCurrencyCode());

        log.debug("Resolved system cash account: currency={}, cashAccountId={}", request.getCurrencyCode(), cashAccount);

        List<LedgerPosting> postings = Arrays.asList(

                LedgerPostingFactory.debit(
                        cashAccount,
                        request.getAmount(),
                        request.getCurrencyCode()
                ),

                LedgerPostingFactory.credit(
                        request.getAccountId(),
                        request.getAmount(),
                        request.getCurrencyCode()
                )
        );

        log.debug("Created deposit postings: debitAccount={}, creditAccount={}, amount={}, currency={}", cashAccount, request.getAccountId(), request.getAmount(), request.getCurrencyCode());


        JournalEntry journal = ledgerEngine.postJournal(
                JournalType.DEPOSIT,
                request.getTransactionId(),
                request.getReference(),
                request.getDescription(),
                postings
        );

        log.info(
                "Deposit journal posted successfully: journalId={}, transactionId={}, reference={}, accountId={}, amount={}, currency={}",
                journal.getId(),
                request.getTransactionId(),
                request.getReference(),
                request.getAccountId(),
                request.getAmount(),
                request.getCurrencyCode()
        );

        return journalMapper.toResponse(journal);
    }

    @Override
    @Transactional
    public JournalResponse withdraw(WithdrawalJournalRequest request) {

        log.info("Starting withdrawal: transactionId={}, reference={}, accountId={}, amount={}, currency={}", request.getTransactionId(), request.getReference(), request.getAccountId(), request.getAmount(), request.getCurrencyCode());

        UUID cashAccount = systemAccountService.getCashAccountId(request.getCurrencyCode());

        log.debug("Resolved system cash account: currency={}, cashAccountId={}", request.getCurrencyCode(), cashAccount);


        List<LedgerPosting> postings = Arrays.asList(

                LedgerPostingFactory.debit(
                        request.getAccountId(),
                        request.getAmount(),
                        request.getCurrencyCode()
                ),

                LedgerPostingFactory.credit(
                        cashAccount,
                        request.getAmount(),
                        request.getCurrencyCode()
                )
        );

        log.debug(
                "Created withdrawal postings: debitAccount={}, creditAccount={}, amount={}, currency={}",
                request.getAccountId(),
                cashAccount,
                request.getAmount(),
                request.getCurrencyCode()
        );

        JournalEntry journal = ledgerEngine.postJournal(
                JournalType.WITHDRAWAL,
                request.getTransactionId(),
                request.getReference(),
                request.getDescription(),
                postings
        );

        log.info(
                "Withdrawal journal posted successfully: journalId={}, transactionId={}, reference={}, accountId={}, amount={}, currency={}",
                journal.getId(),
                request.getTransactionId(),
                request.getReference(),
                request.getAccountId(),
                request.getAmount(),
                request.getCurrencyCode()
        );

        return journalMapper.toResponse(journal);
    }

    @Override
    public JournalResponse payment(PaymentJournalRequest request) {
        return null;
    }

    @Override
    public JournalResponse refund(RefundJournalRequest request) {
        return null;
    }


    @Override
    public JournalResponse reverse(UUID journalId) {
        return null;
    }


    // This Internal method is used to find all accounts to Transaction Service to check if the owner has any accounts before creating a transaction
    @Override
    @Transactional(readOnly = true)
    public ResolveAccountsResponse resolveAccounts(ResolveAccountsRequest request) {

        log.info("Starting account resolution | walletCount={}", request.getWalletIds().size());

        log.debug("Resolving ledger accounts for wallets | walletIds={}", request.getWalletIds());


        // OwnerType.WALLET can be for Customer or Tajer, so ledger account don't need to know the owner type, it just needs to know the wallet id and wallet type.
        List<Account> accounts = accountRepository.findAllByOwnerIdInAndOwnerType(request.getWalletIds(), OwnerType.WALLET);

        log.info(
                "Ledger accounts retrieved | requestedWalletCount={} | foundAccountCount={}",
                request.getWalletIds().size(),
                accounts.size()
        );


        Map<UUID, Account> accountMap = accounts.stream().collect(Collectors.toMap(Account::getOwnerId, Function.identity()));

        log.debug(
                "Ledger account map created | accountCount={}",
                accountMap.size()
        );

        List<AccountReferenceResponse> references =
                request.getWalletIds()
                        .stream()
                        .map(walletId -> {

                            Account account = accountMap.get(walletId);

                            if (account == null) {

                                // OwnerType.WALLET can be for Customer or Tajer, so ledger account don't need to know the owner type, it just needs to know the wallet id and wallet type.
                                log.error("Ledger account not found | walletId={} | ownerType={}", walletId, OwnerType.WALLET);

                                log.debug("Ledger account resolved | walletId={} | accountId={}", walletId, account.getId());

                                throw new ResourceNotFoundException("Ledger account not found for wallet: " + walletId);
                            }

                            return new AccountReferenceResponse(walletId, account.getId());
                        }).collect(Collectors.toList());

        log.info(
                "Account resolution completed successfully | referenceCount={}",
                references.size()
        );

        return new ResolveAccountsResponse(references);
    }
}