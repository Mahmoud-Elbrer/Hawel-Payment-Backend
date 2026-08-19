package com.hawel.ledger_service.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class InsufficientFundsException extends LedgerException {
    public InsufficientFundsException(UUID accountId, BigDecimal balance, BigDecimal amount) {
        super(
                "LEDGER_002",
                "Insufficient funds for accountId: "
                        + accountId
                        + ", balance: "
                        + balance
                        + ", amount: "
                        + amount
        );
    }
}
