package com.hawel.ledger_service.exception;

public class InvalidJournalException extends LedgerException {
    public InvalidJournalException(String message) {
        super("LEDGER_003", message);
    }
}
