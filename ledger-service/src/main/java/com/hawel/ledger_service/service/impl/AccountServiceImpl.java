package com.hawel.ledger_service.service.impl;

import com.hawel.common_service.event.WalletCreatedEvent;
import com.hawel.ledger_service.entity.Account;
import com.hawel.ledger_service.entity.Balance;
import com.hawel.ledger_service.enums.AccountStatus;
import com.hawel.ledger_service.enums.AccountType;
import com.hawel.ledger_service.enums.OwnerType;
import com.hawel.ledger_service.repository.AccountRepository;
import com.hawel.ledger_service.repository.BalanceRepository;
import com.hawel.ledger_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;

    @Override
    @Transactional
    public Account createWalletAccount(WalletCreatedEvent event) {

        // Idempotency
        return accountRepository.findByOwnerIdAndOwnerType(event.getWalletId(), OwnerType.WALLET).orElseGet(() -> createAccount(event));
    }

    private Account createAccount(WalletCreatedEvent event) {

        AccountType accountType = resolveAccountType(event.getWalletType().name());

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .ownerId(event.getWalletId())
                .ownerType(OwnerType.WALLET)
                .accountType(accountType)
                .currency(event.getCurrencyCode())
                .status(AccountStatus.ACTIVE)
                .build();

        Account savedAccount = accountRepository.save(account);

        Balance balance = Balance.builder()
                .account(savedAccount)
                .availableBalance(BigDecimal.ZERO)
                .blockedBalance(BigDecimal.ZERO)
                .build();

        balanceRepository.save(balance);

        return savedAccount;
    }

    private AccountType resolveAccountType(String walletType) {

        switch (walletType) {

            case "PERSONAL": return AccountType.CUSTOMER_WALLET;

            case "TAJER": return AccountType.TAJER;

            case "MERCHANT": return AccountType.MERCHANT;

            default: throw new IllegalArgumentException("Unsupported wallet type: " + walletType);
        }
    }


    // TODO : Implement a proper account number generation strategy. This could involve using a sequence generator, UUIDs, or any other method that ensures uniqueness and meets business requirements.
    private String generateAccountNumber() {

        // Temporary implementation.
        // We will replace this with AccountNumberGenerator later.

        // TODO  :MAKE  AccountSequence table in DB and use it to generate account numbers. This will ensure that account numbers are unique and sequential, and will allow for better control over the account number generation process.

        return "ACC" + System.currentTimeMillis();
    }
}