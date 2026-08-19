package com.hawel.ledger_service.exception;

import java.util.UUID;

public class BalanceNotFoundException extends LedgerException {

    public BalanceNotFoundException(UUID accountId, String currency) {
        super(
                "LEDGER_004",
                "Balance not found for accountId: "
                        + accountId
                        + " and currency: "
                        + currency
        );
    }

}
