package com.hawel.ledger_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hawel.common_service.event.JournalCompletedEvent;
import com.hawel.ledger_service.entity.OutboxEvent;
import com.hawel.ledger_service.enums.OutboxStatus;
import com.hawel.ledger_service.exception.LedgerException;
import com.hawel.ledger_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;


    // don't add @Transactional here, because this method is called from a transactional context and we want the outbox event to be saved only if the transaction is successful
    public void saveJournalCompletedEvent(String aggregateType, UUID aggregateId, String eventType, JournalCompletedEvent event) {

        log.info("Creating outbox event: aggregateType={}, aggregateId={}, eventType={}", aggregateType, aggregateId, eventType);

        try {

            String payload = objectMapper.writeValueAsString(event);

            log.debug("Outbox event payload created: aggregateId={}, payload={}", aggregateId, payload);

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .eventId(event.getEventId())
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .payload(payload)
                    .status(OutboxStatus.PENDING)
                    .createdAt(Instant.now())
                    .retryCount(0)
                    .build();

            OutboxEvent savedEvent = outboxEventRepository.save(outboxEvent);

            log.info(
                    "Outbox event saved successfully: eventId={}, aggregateId={}, eventType={}, status={}",
                    savedEvent.getId(),
                    savedEvent.getAggregateId(),
                    savedEvent.getEventType(),
                    savedEvent.getStatus()
            );

        } catch (JsonProcessingException ex) {

            log.error("Failed to serialize outbox event. aggregateId={}, eventType={}", aggregateId, eventType, ex);

            throw new LedgerException("Failed to serialize outbox event. aggregateId=" + aggregateId + ", eventType=" + eventType ,ex.getMessage());
        }
    }
}