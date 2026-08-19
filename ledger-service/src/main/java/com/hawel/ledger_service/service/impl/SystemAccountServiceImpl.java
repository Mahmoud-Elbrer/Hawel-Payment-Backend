package com.hawel.ledger_service.service.impl;


import com.hawel.common_service.enums.CurrencyCode;
import com.hawel.ledger_service.entity.Account;
import com.hawel.ledger_service.enums.AccountType;
import com.hawel.ledger_service.exception.LedgerException;
import com.hawel.ledger_service.repository.AccountRepository;
import com.hawel.ledger_service.service.SystemAccountService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class SystemAccountServiceImpl implements SystemAccountService {


    private final AccountRepository accountRepository;


    @Override
    public UUID getCashAccountId(CurrencyCode currency) {

        // Find the system account for cash based on the currency and return its ID
        // can have CASH, CASH_USD, CASH_EUR, etc. depending on the currency
        return findSystemAccount(AccountType.SYSTEM, "CASH", currency).getId();

    }


    @Override
    public UUID getFeeAccountId(CurrencyCode currency) {

        return findSystemAccount(AccountType.FEE, "FEE", currency).getId();

    }


    @Override
    public UUID getCommissionAccountId(CurrencyCode currency) {

        return findSystemAccount(AccountType.COMMISSION, "COMMISSION", currency).getId();

    }


    @Override
    public UUID getTaxAccountId(CurrencyCode currency) {

        return findSystemAccount(AccountType.TAX, "TAX", currency).getId();

    }


    @Override
    public UUID getSettlementAccountId(CurrencyCode currency) {

        return findSystemAccount(AccountType.SYSTEM, "SETTLEMENT", currency).getId();

    }


    private Account findSystemAccount(AccountType type, String code, CurrencyCode currency) {

        log.info("Finding system account: type={}, code={}, currency={}", type, code, currency);

        return accountRepository.findSystemAccount(type, code, currency).orElseThrow(() -> new LedgerException("LEDGER_002", "System account not found: type=" + type + ", code=" + code + ", currency=" + currency));
    }

}