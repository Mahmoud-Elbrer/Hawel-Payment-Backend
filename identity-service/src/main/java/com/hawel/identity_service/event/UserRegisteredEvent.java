package com.hawel.identity_service.event;

import lombok.Getter;

import java.util.UUID;


@Getter
public class UserRegisteredEvent extends BaseEvent {

    private final UUID userId;

    private final String phoneNumber;


    public UserRegisteredEvent(
            UUID userId,
            String phoneNumber
    ) {

        super();

        this.userId = userId;
        this.phoneNumber = phoneNumber;

    }
}