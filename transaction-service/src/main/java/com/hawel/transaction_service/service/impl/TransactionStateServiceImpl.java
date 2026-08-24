package com.hawel.transaction_service.service.impl;

import com.hawel.transaction_service.entity.Transaction;
import com.hawel.transaction_service.entity.TransactionStatusHistory;
import com.hawel.transaction_service.enums.TransactionStatus;
import com.hawel.transaction_service.repository.TransactionRepository;
import com.hawel.transaction_service.repository.TransactionStatusHistoryRepository;
import com.hawel.transaction_service.service.TransactionStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TransactionStateServiceImpl implements TransactionStateService {

    private final TransactionRepository transactionRepository;
    private final TransactionStatusHistoryRepository statusHistoryRepository;

    @Override
    @Transactional
    public Transaction createPendingTransaction(Transaction transaction) {

        transaction.setStatus(TransactionStatus.PENDING);

        Transaction savedTransaction = transactionRepository.save(transaction);

        saveHistory(savedTransaction, null, TransactionStatus.PENDING, "Transaction created");

        return savedTransaction;
    }

    @Override
    @Transactional
    public Transaction changeStatus(Transaction transaction, TransactionStatus newStatus, String reason) {

        TransactionStatus oldStatus = transaction.getStatus();

        transaction.setStatus(newStatus);

        if (newStatus == TransactionStatus.SUCCESS || newStatus == TransactionStatus.REFUNDED || newStatus == TransactionStatus.REVERSED) {

            transaction.setCompletedAt(Instant.now());
        }

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        saveHistory(savedTransaction, oldStatus, newStatus, reason);

        return savedTransaction;
    }

    private void saveHistory(Transaction transaction, TransactionStatus oldStatus, TransactionStatus newStatus, String reason) {

        TransactionStatusHistory history = TransactionStatusHistory.builder()
                .transactionId(transaction.getId())
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedAt(Instant.now())
                .reason(reason)
                .build();

        statusHistoryRepository.save(history);
    }
}