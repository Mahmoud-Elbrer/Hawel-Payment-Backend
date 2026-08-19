package com.hawel.ledger_service.exception;


import java.math.BigDecimal;


public class JournalNotBalancedException extends LedgerException {


    public JournalNotBalancedException(BigDecimal debit, BigDecimal credit) {

        super(
                "LEDGER_003",
                "Journal not balanced. Debit="
                        + debit
                        + " Credit="
                        + credit

        );

    }

}