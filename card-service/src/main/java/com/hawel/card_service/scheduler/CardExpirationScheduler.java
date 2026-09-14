package com.hawel.card_service.scheduler;

import com.hawel.card_service.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardExpirationScheduler {

    private final CardRepository cardRepository;

    @Scheduled(cron = "0 0 1 * * *") // every day at 1 AM
    @Transactional
    public void expireCards() {

        log.info("Starting card expiration job");

        int expiredCards = cardRepository.expireCards(LocalDate.now());

        log.info("Card expiration job completed. expiredCards={}", expiredCards);
    }
}