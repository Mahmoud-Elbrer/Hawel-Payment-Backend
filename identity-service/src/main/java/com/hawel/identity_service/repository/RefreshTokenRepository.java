package com.hawel.identity_service.repository;

import com.hawel.identity_service.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    List<RefreshToken> findAllByUserId(UUID userId);

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    // Find all active refresh tokens for a specific user and device
    List<RefreshToken> findAllByUser_IdAndDevice_IdAndRevokedFalse(UUID userId, UUID deviceId);


    List<RefreshToken> findAllByUser_Id(UUID userId);
}
