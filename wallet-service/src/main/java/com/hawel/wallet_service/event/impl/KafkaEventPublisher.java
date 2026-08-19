package com.hawel.wallet_service.event.impl;


import com.hawel.common_service.event.WalletCreatedEvent;
import com.hawel.wallet_service.event.*;
import com.hawel.wallet_service.kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisher implements WalletEventPublisher {


    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Override
    public void publish(Object event) {


        String topic = resolveTopic(event);


        kafkaTemplate.send(topic, event);


        log.info("Kafka event published. topic={}, event={}", topic, event.getClass().getSimpleName());

    }


    private String resolveTopic(Object event) {


        if (event instanceof WalletCreatedEvent)
            return KafkaTopics.WALLET_CREATED;


        if (event instanceof WalletActivatedEvent)
            return KafkaTopics.WALLET_ACTIVATED;


        if (event instanceof WalletFrozenEvent)
            return KafkaTopics.WALLET_FROZEN;


        if (event instanceof WalletUnfrozenEvent)
            return KafkaTopics.WALLET_UNFROZEN;

        if (event instanceof WalletClosedEvent)
            return KafkaTopics.WALLET_CLOSED;

        if (event instanceof WalletLimitChangedEvent)
            return KafkaTopics.WALLET_LIMIT_CHANGED;


        if (event instanceof WalletSettingsChangedEvent)
            return KafkaTopics.WALLET_SETTINGS_CHANGED;

        if (event instanceof WalletSuspendedEvent)
            return KafkaTopics.WALLET_SUSPENDED;


        throw new IllegalArgumentException(
                "Unknown event type"
        );
    }

}