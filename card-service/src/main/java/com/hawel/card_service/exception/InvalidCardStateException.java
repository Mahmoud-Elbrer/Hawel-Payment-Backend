package com.hawel.card_service.exception;

public class InvalidCardStateException extends RuntimeException {

    public InvalidCardStateException(String message) {
        super(message);
    }
}