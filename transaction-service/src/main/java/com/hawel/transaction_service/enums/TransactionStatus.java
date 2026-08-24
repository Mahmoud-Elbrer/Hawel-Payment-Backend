package com.hawel.transaction_service.enums;

public enum TransactionStatus {

    PENDING,

    VALIDATING,

    PROCESSING,

    SUCCESS,

    FAILED,

    TIMEOUT,

    REVERSED,

    REFUNDED,

    CANCELLED
}
