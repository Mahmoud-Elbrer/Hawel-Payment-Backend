package com.hawel.transaction_service.scheduler;

import com.hawel.common_service.dto.ledger.JournalResponse;
import com.hawel.common_service.enums.JournalStatus;
import com.hawel.transaction_service.client.ledger.LedgerClient;
import com.hawel.transaction_service.entity.Transaction;
import com.hawel.transaction_service.enums.TransactionStatus;
import com.hawel.transaction_service.repository.TransactionRepository;
import com.hawel.transaction_service.service.TransactionStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReconciliationScheduler {

    private final TransactionRepository transactionRepository;

    private final LedgerClient ledgerClient;

    private final TransactionStateService transactionStateService;

    @Value("${transaction.reconciliation.timeout-minutes:10}")
    private long timeoutMinutes;


    @Scheduled(fixedDelay = 60_000)
    public void reconcileProcessingTransactions() {

        log.info("Starting transaction reconciliation...");

        List<Transaction> transactions = transactionRepository.findByStatus(TransactionStatus.PROCESSING);

        if (transactions.isEmpty()) {

            log.info("No PROCESSING transactions found.");

            return;
        }

        log.info("Found {} PROCESSING transactions", transactions.size());

        for (Transaction transaction : transactions) {

            reconcile(transaction);
        }
    }


    private void reconcile(Transaction transaction) {

        log.info("Reconciling transactionId={}", transaction.getId());

        try {

            JournalResponse journalResponse = ledgerClient.getByTransactionId(transaction.getId());

            if (journalResponse != null) {

                handleLedgerResponse(transaction, journalResponse);

                return;
            }

        } catch (Exception ex) {

            log.warn("Could not reconcile transactionId={}. " + "Ledger response is not definitive.", transaction.getId(), ex);
        }

        /*
         * Ledger did not give us a definitive result.
         *
         * Check timeout.
         */
        checkTimeout(transaction);
    }


    private void handleLedgerResponse(Transaction transaction, JournalResponse journalResponse) {

        if (journalResponse.getStatus() == JournalStatus.COMPLETED) {

            transactionStateService.changeStatus(transaction, TransactionStatus.SUCCESS, "Ledger completed transaction");

            return;
        }

        if (journalResponse.getStatus() == JournalStatus.FAILED) {

            transactionStateService.changeStatus(transaction, TransactionStatus.FAILED, "Ledger failed transaction");

            return;
        }

        /*
         * Unknown/intermediate Ledger status.
         */
        checkTimeout(transaction);
    }


    private void checkTimeout(Transaction transaction) {

        if (transaction.getProcessingAt() == null) {

            log.warn("Transaction {} has no processingAt. " + "Skipping timeout check.", transaction.getId());

            return;
        }

        Instant timeoutAt = transaction.getProcessingAt().plus(timeoutMinutes, ChronoUnit.MINUTES);

        if (Instant.now().isAfter(timeoutAt)) {

            log.warn(
                    "Transaction timeout: transactionId={}, processingAt={}, timeoutAt={}",
                    transaction.getId(),
                    transaction.getProcessingAt(),
                    timeoutAt
            );

            transactionStateService.changeStatus(
                    transaction,
                    TransactionStatus.TIMEOUT,
                    "Transaction reconciliation timeout"
            );
        }
    }
}