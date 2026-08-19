package com.hawel.ledger_service.exception;


import java.math.BigDecimal;
import java.util.UUID;


public class InsufficientBalanceException extends LedgerException {


    public InsufficientBalanceException(UUID accountId, BigDecimal amount) {

        super(
                "LEDGER_002",

                "Insufficient balance for account "
                        + accountId
                        + " required "
                        + amount
        );

    }

}