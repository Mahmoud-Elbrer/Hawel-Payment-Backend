package com.hawel.transaction_service.exception;


import lombok.Getter;


@Getter
public class TransactionException extends RuntimeException {

    private final String code;


    public TransactionException(String code, String message) {

        super(message);

        this.code = code;
    }

}