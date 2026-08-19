package com.hawel.ledger_service.entity;

import com.hawel.ledger_service.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "outbox_events",
        indexes = {
                @Index(
                        name = "idx_outbox_status_created",
                        columnList = "status, created_at"
                ),
                @Index(
                        name = "idx_outbox_aggregate",
                        columnList = "aggregate_type, aggregate_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;

    /**
     * The type of entity that generated the event.
     * <p>
     * Example:
     * JOURNAL
     */
    @Column(name = "aggregate_type", nullable = false, length = 100)
    private String aggregateType;

    /**
     * ID of the entity that generated the event.
     * <p>
     * Example:
     * journal.id
     */
    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    /**
     * Event name.
     * <p>
     * Example:
     * JOURNAL_COMPLETED
     */
    @Column(name = "event_type", nullable = false, length = 150)
    private String eventType;

    /**
     * JSON representation of the event.
     */
    @Lob
    @Column(name = "payload", nullable = false , columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "last_error", length = 1000)
    private String lastError;

    @PrePersist
    void onCreate() {

        createdAt = Instant.now();

        if (status == null) {
            status = OutboxStatus.PENDING;
        }

        if (retryCount < 0) {
            retryCount = 0;
        }
    }
}