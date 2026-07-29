package com.hawel.identity_service.event;


import lombok.Getter;

import java.util.UUID;


@Getter
public class UserLoggedInEvent extends BaseEvent {

    private final UUID userId;

    private final UUID deviceId;


    public UserLoggedInEvent(UUID userId, UUID deviceId) {
        super();
        this.userId = userId;
        this.deviceId = deviceId;
    }

}