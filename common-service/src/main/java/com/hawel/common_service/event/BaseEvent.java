package com.hawel.common_service.event;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public abstract class BaseEvent {

    private final UUID eventId;
    private final LocalDateTime createdAt;

    protected BaseEvent() {
        this(UUID.randomUUID(), LocalDateTime.now());
    }

    protected BaseEvent(UUID eventId, LocalDateTime createdAt) {
        this.eventId = eventId;
        this.createdAt = createdAt;
    }
}