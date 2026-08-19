package com.hawel.ledger_service.exception;

public class DuplicateJournalException extends LedgerException {

    public DuplicateJournalException(String journalId) {

        super(
                "LEDGER_004",
                "Duplicate journal entry: " + journalId
        );

    }
}
