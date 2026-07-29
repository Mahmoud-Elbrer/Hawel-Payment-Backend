package com.hawel.identity_service.event;

import lombok.Getter;

import java.util.UUID;


@Getter
public class PasswordChangedEvent extends BaseEvent {

    private final UUID userId;


    public PasswordChangedEvent(UUID userId) {
        super();
        this.userId = userId;
    }

}
