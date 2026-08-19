package com.hawel.ledger_service.exception;


import com.hawel.common_service.enums.CurrencyCode;

public class CurrencyMismatchException extends LedgerException {


    public CurrencyMismatchException(CurrencyCode first, CurrencyCode second) {

        super(
                "LEDGER_005",
                "Currency mismatch "
                        + first
                        + " != "
                        + second

        );

    }

}