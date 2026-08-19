package com.hawel.wallet_service.event.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hawel.common_service.event.JournalCompletedEvent;
import com.hawel.common_service.event.WalletBalanceUpdate;
import com.hawel.wallet_service.entity.ProcessedEvent;
import com.hawel.wallet_service.repository.ProcessedEventRepository;
import com.hawel.wallet_service.service.WalletBalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JournalCompletedListener {

    private final ProcessedEventRepository processedEventRepository;

    private final WalletBalanceService walletBalanceService;

    private final ObjectMapper objectMapper;


    @KafkaListener(topics = "ledger.events", groupId = "wallet-service")
    @Transactional
    public void consume(String payload) {

        try {
            JournalCompletedEvent event = objectMapper.readValue(payload, JournalCompletedEvent.class);

            log.info("Received JournalCompletedEvent: eventId={}, transactionId={}, journalId={} ", event.getEventId(), event.getTransactionId(), event.getJournalId());

            // handle Idempotency event
            if (processedEventRepository.existsByEventId(event.getEventId())) {

                log.warn("Duplicate event ignored: eventId={}", event.getEventId());

                return;
            }

            // Validate event
            if (!"COMPLETED".equals(event.getStatus())) {

                log.warn("Ignoring event because journal is not completed: journalId={}, status={}", event.getJournalId(), event.getStatus());

                return;
            }

            // Update all wallet balances affected by this journal
            for (WalletBalanceUpdate walletBalance : event.getWalletBalances()) {

                log.info("Syncing wallet balance: walletId={}, availableBalance={}, blockedBalance={}", walletBalance.getWalletId(), walletBalance.getAvailableBalance(), walletBalance.getBlockedBalance());

                // why for loop? because a journal can have multiple postings affecting different wallets like transfer , so we need to update the balance for each affected wallet
                walletBalanceService.syncBalanceFromLedger(walletBalance.getWalletId(), walletBalance.getAvailableBalance(), walletBalance.getBlockedBalance());

                log.info("JournalCompletedEvent processed successfully: eventId={}, walletId={}", event.getEventId(), walletBalance.getWalletId());

            }

            // save or Mark event as processed
            processedEventRepository.save(ProcessedEvent.builder().eventId(event.getEventId()).eventType("JOURNAL_COMPLETED").build());


            log.info("JournalCompletedEvent processed successfully: eventId={}", event.getEventId());


        } catch (Exception ex) {

            log.error("Failed to process JournalCompletedEvent payload={}", payload, ex);

            throw new RuntimeException(ex);
        }
    }
}