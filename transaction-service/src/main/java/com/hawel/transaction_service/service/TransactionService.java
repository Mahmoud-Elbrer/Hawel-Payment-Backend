package com.hawel.transaction_service.service;

import com.hawel.transaction_service.dto.request.TransferTransactionRequest;
import com.hawel.transaction_service.dto.response.TransactionResponse;

import java.util.UUID;

public interface TransactionService {

    TransactionResponse transfer(TransferTransactionRequest request, String idempotencyKey);
    TransactionResponse getTransaction(UUID transactionId);
}
