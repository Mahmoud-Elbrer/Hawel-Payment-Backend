package com.hawel.transaction_service.service;

import com.hawel.transaction_service.entity.Transaction;

public interface TransactionCreationService {
    Transaction create(Transaction transaction, String idempotencyKey, String requestHash);
}
