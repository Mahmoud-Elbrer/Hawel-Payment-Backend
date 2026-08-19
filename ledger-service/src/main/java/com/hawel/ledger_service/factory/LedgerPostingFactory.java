package com.hawel.ledger_service.factory;

import com.hawel.common_service.enums.CurrencyCode;
import com.hawel.ledger_service.domain.LedgerPosting;
import com.hawel.ledger_service.enums.EntryType;

import java.math.BigDecimal;
import java.util.UUID;

public final class LedgerPostingFactory {

    private LedgerPostingFactory() {}

    public static LedgerPosting debit(UUID accountId, BigDecimal amount, CurrencyCode currency) {

        return new LedgerPosting(
                accountId,
                EntryType.DEBIT,
                amount,
                currency
        );
    }

    public static LedgerPosting credit(UUID accountId, BigDecimal amount, CurrencyCode currency) {

        return new LedgerPosting(
                accountId,
                EntryType.CREDIT,
                amount,
                currency
        );
    }

}
