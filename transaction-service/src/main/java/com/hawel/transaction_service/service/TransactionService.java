package com.hawel.transaction_service.service;

import com.hawel.transaction_service.dto.request.TransferTransactionRequest;
import com.hawel.transaction_service.dto.response.TransactionResponse;

public interface TransactionService {

    TransactionResponse transfer(TransferTransactionRequest request, String idempotencyKey);
}
