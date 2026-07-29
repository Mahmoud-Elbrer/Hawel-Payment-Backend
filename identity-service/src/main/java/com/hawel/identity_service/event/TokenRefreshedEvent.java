package com.hawel.identity_service.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class TokenRefreshedEvent extends BaseEvent {

    private final UUID userId;

    private final UUID deviceId;


    public TokenRefreshedEvent(UUID userId, UUID deviceId) {

        super();

        this.userId = userId;
        this.deviceId = deviceId;
    }
}
