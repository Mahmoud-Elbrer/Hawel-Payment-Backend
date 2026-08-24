package com.hawel.transaction_service.service.impl;

import com.hawel.transaction_service.entity.IdempotencyKey;
import com.hawel.transaction_service.entity.Transaction;
import com.hawel.transaction_service.entity.TransactionStatusHistory;
import com.hawel.transaction_service.enums.TransactionStatus;
import com.hawel.transaction_service.repository.IdempotencyKeyRepository;
import com.hawel.transaction_service.repository.TransactionRepository;
import com.hawel.transaction_service.repository.TransactionStatusHistoryRepository;
import com.hawel.transaction_service.service.TransactionCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TransactionCreationServiceImpl implements TransactionCreationService {

    private final TransactionRepository transactionRepository;
    private final TransactionStatusHistoryRepository statusHistoryRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    @Override
    @Transactional
    public Transaction create(Transaction transaction, String idempotencyKey, String requestHash) {

        /*
         * 1. Set initial status
         */
        transaction.setStatus(TransactionStatus.PENDING);

        /*
         * 2. Save transaction
         */
        Transaction savedTransaction = transactionRepository.save(transaction);

        /*
         * 3. Save initial status history
         */
        TransactionStatusHistory history = TransactionStatusHistory.builder()
                        .transactionId(savedTransaction.getId())
                        .oldStatus(null)
                        .newStatus(TransactionStatus.PENDING)
                        .changedAt(Instant.now())
                        .reason("Transaction created")
                        .build();

        statusHistoryRepository.save(history);

        /*
         * 4. Save idempotency key
         */
        Instant now = Instant.now();

        IdempotencyKey idempotency = IdempotencyKey.builder()
                        .idempotencyKey(idempotencyKey)
                        .requestHash(requestHash)
                        .transactionId(savedTransaction.getId())
                        .createdAt(now)
                        .expireAt(now.plusSeconds(24 * 60 * 60))
                        .build();

        idempotencyKeyRepository.save(idempotency);

        /*
         * 5. Return saved transaction
         */
        return savedTransaction;
    }
}