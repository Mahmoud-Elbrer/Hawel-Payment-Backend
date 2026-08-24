package com.hawel.ledger_service.service;

import com.hawel.ledger_service.entity.OutboxEvent;
import com.hawel.ledger_service.enums.OutboxStatus;
import com.hawel.ledger_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
// TODO : Warring : when make more instance of this Publisher
public class OutboxPublisher {

    private static final String LEDGER_EVENTS_TOPIC = "ledger.events";

    private final OutboxEventRepository outboxEventRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 30000)
    public void publishPendingEvents() {

        log.debug("Checking for pending outbox events...");

        List<OutboxEvent> events = outboxEventRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);


        log.debug("Found {} pending outbox events", events.size());

        if (events.isEmpty()) {

            log.debug("No pending outbox events found.");

            return;
        }

        log.info("Found {} pending outbox events", events.size());

        for (OutboxEvent event : events) {

            publish(event);
        }
    }

    private void publish(OutboxEvent event) {

        log.info("Publishing outbox event: eventId={}, eventType={}, aggregateId={}", event.getId(), event.getEventType(), event.getAggregateId());

        try {

            kafkaTemplate.send(LEDGER_EVENTS_TOPIC, event.getAggregateId().toString(), event.getPayload()).get();

            event.setStatus(OutboxStatus.PUBLISHED);

            event.setPublishedAt(Instant.now());

            event.setLastError(null);

            outboxEventRepository.save(event);

            log.info("Outbox event published successfully: eventId={}, eventType={}", event.getId(), event.getEventType());

        } catch (Exception ex) {

            int retryCount = event.getRetryCount() + 1;

            event.setRetryCount(retryCount);

            event.setLastError(ex.getMessage());

            outboxEventRepository.save(event);

            log.error("Failed to publish outbox event: eventId={}, retryCount={}", event.getId(), retryCount, ex);
        }
    }
}