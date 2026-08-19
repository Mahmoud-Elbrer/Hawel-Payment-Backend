package com.hawel.ledger_service.service;

import com.hawel.common_service.enums.CurrencyCode;
import com.hawel.ledger_service.entity.Account;
import com.hawel.ledger_service.enums.AccountType;

import java.util.UUID;

public interface SystemAccountService {

    UUID getCashAccountId(CurrencyCode currency);

    UUID getFeeAccountId(CurrencyCode currency);

    UUID getCommissionAccountId(CurrencyCode currency);

    UUID getSettlementAccountId(CurrencyCode currency);

    UUID getTaxAccountId(CurrencyCode currency);


    // TODO : add methode to deposit and withdraw from system accounts
}
