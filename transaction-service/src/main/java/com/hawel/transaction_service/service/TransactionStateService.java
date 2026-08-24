package com.hawel.transaction_service.service;

import com.hawel.transaction_service.entity.Transaction;
import com.hawel.transaction_service.enums.TransactionStatus;

public interface TransactionStateService {

    Transaction createPendingTransaction(Transaction transaction);

    Transaction changeStatus(Transaction transaction, TransactionStatus newStatus, String reason);
}
