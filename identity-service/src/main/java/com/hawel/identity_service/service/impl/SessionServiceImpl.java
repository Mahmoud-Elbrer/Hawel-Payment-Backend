package com.hawel.identity_service.service.impl;

import com.hawel.identity_service.entity.Device;
import com.hawel.identity_service.entity.Session;
import com.hawel.identity_service.entity.User;
import com.hawel.identity_service.exception.ResourceNotFoundException;
import com.hawel.identity_service.repository.SessionRepository;
import com.hawel.identity_service.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {


    private final SessionRepository sessionRepository;

    @Override
    @Transactional
    public Session create(User user, Device device, String ipAddress, String userAgent) {
        log.info("Creating session userId={}, deviceId={}", user.getId(), device.getId());


        Session session = sessionRepository
                .findByUserIdAndDeviceIdAndActiveTrue(user.getId(), device.getId())
                .map(existingSession -> {

                    log.info("Active session already exists. Updating activity.");

                    existingSession.setIpAddress(ipAddress);
                    existingSession.setUserAgent(userAgent);
                    existingSession.setLastActivityAt(LocalDateTime.now());
                    return existingSession;

                })
                .orElseGet(() -> {

                    log.info("Creating new session.");

                    return Session.builder()
                            .user(user)
                            .device(device)
                            .ipAddress(ipAddress)
                            .userAgent(userAgent)
                            .active(true)
                            .loginAt(LocalDateTime.now())
                            .lastActivityAt(LocalDateTime.now())
                            .build();

                });


        Session saved = sessionRepository.save(session);


        log.info("Session created successfully sessionId={}", saved.getId());

        return saved;
    }

    @Override
    public Session getActiveSession(UUID userId, UUID deviceId) {

        return sessionRepository
                .findByUserIdAndDeviceIdAndActiveTrue(
                        userId,
                        deviceId
                )
                .orElseThrow(() -> new ResourceNotFoundException("Active session not found for userId: " + userId + " and deviceId: " + deviceId, "userId/deviceId", userId));
    }

    @Override
    public void updateLastActivity(UUID userId, UUID deviceId) {
        Session session = sessionRepository.findByUser_IdAndDevice_IdAndActiveTrue(userId, deviceId).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Active session not found for userId: " + userId + " and deviceId: " + deviceId,
                        "userId/deviceId",
                        userId
                )
        );


        session.setLastActivityAt(LocalDateTime.now());

        // todo :
        // may be is not necessary to save the session here,
        // because the session is managed by JPA and will be automatically saved at the end of the transaction.
        // But we can keep it for clarity.
        sessionRepository.save(session);

        log.debug("Session last activity updated successfully sessionId={}", session.getId());
    }

    @Override
    public void logout( UUID userId, UUID deviceId) {
        Session session = sessionRepository
                .findByUser_IdAndDevice_IdAndActiveTrue(userId , deviceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active session not found for userId: " + userId + " and deviceId: " + deviceId,
                                "userId/deviceId",
                                userId
                        )
                );


        session.setActive(false);

        session.setLogoutAt(LocalDateTime.now());

        sessionRepository.save(session);

        log.info("Session logged out successfully sessionId={}", session.getId());
    }

    @Override
    public void logoutAll(UUID userId) {
        List<Session> sessions = sessionRepository.findAllByUserIdAndActiveTrue(userId);


        sessions.forEach(session -> {
            session.setActive(false);
            session.setLogoutAt(LocalDateTime.now());
        });

        sessionRepository.saveAll(sessions);

        log.info("All sessions logged out userId={}", userId);
    }

    @Override
    public List<Session> getUserSessions(UUID userId) {
        return sessionRepository.findAllByUserIdAndActiveTrue(userId);
    }
}