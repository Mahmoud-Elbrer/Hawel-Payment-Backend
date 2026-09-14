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
        // OwnerType.WALLET can be for Customer or Tajer, so ledger account don't need to know the owner type, it just needs to know the wallet id and wallet type.
        return accountRepository.findByOwnerIdAndOwnerType(event.getWalletId(), OwnerType.WALLET).orElseGet(() -> createAccount(event));
    }

    private Account createAccount(WalletCreatedEvent event) {

        // Resolve the AccountType based on the WalletType from the event.
        AccountType accountType = resolveAccountType(event.getWalletType().name());


        /*
         * Difference between OwnerType and AccountType here .ownerType(OwnerType.WALLET) and .accountType(accountType) :
         *
         * OwnerType identifies the entity that this account belongs to within
         * the Ledger system. In this case, the account is linked to a Wallet,
         * so we use OwnerType.WALLET regardless of whether the wallet belongs
         * to a Customer or a Tajer.
         *
         * AccountType identifies the financial or business type of the account.
         * It is determined based on the WalletType received from the Wallet Service.
         *
         * Example:
         * - Customer Wallet → OwnerType = WALLET, AccountType = CUSTOMER
         * - Tajer Wallet    → OwnerType = WALLET, AccountType = TAJER
         *
         * This approach keeps the Ledger Service independent from Customer or Tajer
         * domain details, treating the Wallet as the direct owner of the ledger account.
         */

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .ownerId(event.getWalletId())
                // OwnerType = the entity this account belongs to (Wallet).
                .ownerType(OwnerType.WALLET) // OwnerType.WALLET can be for Customer or Tajer, so ledger account don't need to know the owner type, it just needs to know the wallet id and wallet type.
                // AccountType = the financial type of the account (Customer or Tajer).
                .accountType(accountType)
                .currencyCode(event.getCurrencyCode())
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

            case "PERSONAL":
                return AccountType.CUSTOMER;

            case "BUSINESS":
                return AccountType.TAJER;

//            case "MERCHANT":
//                return AccountType.MERCHANT;

            // TODO : Add more wallet types here as needed. For example, if you have a MERCHANT wallet type, you can add it like this:

            default:
                throw new IllegalArgumentException("Unsupported wallet type: " + walletType);
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