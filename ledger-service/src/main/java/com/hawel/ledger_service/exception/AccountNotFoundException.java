package com.hawel.ledger_service.exception;


import java.util.UUID;


public class AccountNotFoundException extends LedgerException {


    public AccountNotFoundException(UUID accountId) {

        super("LEDGER_001", "Account not found: " + accountId);

    }

}