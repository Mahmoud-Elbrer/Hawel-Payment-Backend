package com.hawel.ledger_service.event;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public abstract class BaseEvent {

    private final UUID eventId;

    private final LocalDateTime createdAt;


    protected BaseEvent() {

        this.eventId = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();

    }
}