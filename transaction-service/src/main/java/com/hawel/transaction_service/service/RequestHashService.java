package com.hawel.transaction_service.service;

import com.hawel.transaction_service.dto.request.TransferTransactionRequest;

public interface RequestHashService {
    String generateHash(TransferTransactionRequest request);
}
