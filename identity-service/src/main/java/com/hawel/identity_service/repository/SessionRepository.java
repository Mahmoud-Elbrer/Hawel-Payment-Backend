package com.hawel.identity_service.repository;

import com.hawel.identity_service.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {


    Optional<Session> findByUserIdAndDeviceIdAndActiveTrue(UUID userId, UUID deviceId);

    List<Session> findAllByUserIdAndActiveTrue(UUID userId);

    /**
     * Find active session by id.
     */
    Optional<Session> findByIdAndActiveTrue(UUID sessionId);



    /**
     * Check if user has active session on device.
     */
    boolean existsByUserIdAndDeviceIdAndActiveTrue(UUID userId, UUID deviceId);


    Optional<Session> findByUser_IdAndDevice_IdAndActiveTrue(
            UUID userId,
            UUID deviceId
    );


}
