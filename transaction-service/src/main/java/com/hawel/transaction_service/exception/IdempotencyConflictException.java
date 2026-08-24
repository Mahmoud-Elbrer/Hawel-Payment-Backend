package com.hawel.transaction_service.exception;

public class IdempotencyConflictException extends TransactionException {

    public IdempotencyConflictException(String message) {
        super(message);
    }
}