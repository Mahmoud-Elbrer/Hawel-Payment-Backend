package com.hawel.ledger_service.kafka.consumer;

import com.hawel.ledger_service.kafka.KafkaTopics;
import com.hawel.ledger_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.hawel.common_service.event.WalletCreatedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WalletEventConsumer {

    private final AccountService accountService;

    @KafkaListener(topics = KafkaTopics.WALLET_CREATED, groupId = "ledger-service")
    public void handleWalletCreated(WalletCreatedEvent event) {

        log.info("Received WalletCreatedEvent walletId={}, ownerId={}", event.getWalletId(), event.getOwnerId());

        accountService.createWalletAccount(event);
    }
}