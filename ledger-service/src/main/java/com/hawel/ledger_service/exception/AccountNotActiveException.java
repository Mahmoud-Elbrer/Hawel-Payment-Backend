package com.hawel.ledger_service.exception;

import com.hawel.ledger_service.enums.AccountStatus;

import java.util.UUID;

public class AccountNotActiveException extends LedgerException {
    public AccountNotActiveException(UUID accountId, AccountStatus status) {
        super(
                "LEDGER_001",
                "Account with ID: "
                        + accountId
                        + " is not active. Current status: "
                        + status
        );
    }
}
