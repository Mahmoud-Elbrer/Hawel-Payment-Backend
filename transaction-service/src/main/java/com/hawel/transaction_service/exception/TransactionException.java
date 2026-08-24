package com.hawel.transaction_service.exception;


import lombok.Getter;


@Getter
public class TransactionException extends RuntimeException {

    public TransactionException(String message) {

        super(message);
    }

}