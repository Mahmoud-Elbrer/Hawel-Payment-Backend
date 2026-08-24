package com.hawel.transaction_service.scheduler;

import com.hawel.transaction_service.repository.IdempotencyKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotencyCleanupScheduler {

    private final IdempotencyKeyRepository idempotencyKeyRepository;

    // TODO : make this time in configuration admin panel
    @Scheduled(cron = "0 0 2 * * *", zone = "Africa/Khartoum") // it run every day at 2 AM.
    @Transactional
    public void cleanupExpiredIdempotencyKeys() {

        Instant now = Instant.now();

        log.info("Starting idempotency keys cleanup at {}", now);

        long deletedCount = idempotencyKeyRepository.deleteByExpireAtBefore(now);

        log.info("Idempotency keys cleanup completed at {}. Deleted {} expired keys.", Instant.now(), deletedCount);

    }
}