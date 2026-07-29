package com.hawel.identity_service.service;

import com.hawel.identity_service.entity.Device;
import com.hawel.identity_service.entity.Session;
import com.hawel.identity_service.entity.User;

import java.util.List;
import java.util.UUID;

public interface SessionService {

    Session create(
            User user,
            Device device,
            String ipAddress,
            String userAgent
    );

    Session getActiveSession(
            UUID userId,
            UUID deviceId
    );

    void updateLastActivity(
            UUID userId,
            UUID deviceId
    );

    void logout(
            UUID userId,
            UUID deviceId
    );

    void logoutAll(
            UUID userId
    );

    /**
     * Get active sessions for user.
     */
    List<Session> getUserSessions(
            UUID userId
    );

}
