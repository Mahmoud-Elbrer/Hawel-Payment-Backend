package com.hawel.transaction_service.repository;

import com.hawel.transaction_service.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, UUID> {

    Optional<IdempotencyKey> findByIdempotencyKey(String idempotencyKey);

    boolean existsByIdempotencyKey(String idempotencyKey);


    // Remove IdempotencyKey with time shoulder
    long deleteByExpireAtBefore(Instant now);
}
