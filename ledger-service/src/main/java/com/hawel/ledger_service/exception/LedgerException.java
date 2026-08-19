package com.hawel.ledger_service.exception;


import lombok.Getter;


@Getter
public class LedgerException extends RuntimeException {


    private final String code;


    public LedgerException(String code, String message) {

        super(message);

        this.code = code;
    }

}