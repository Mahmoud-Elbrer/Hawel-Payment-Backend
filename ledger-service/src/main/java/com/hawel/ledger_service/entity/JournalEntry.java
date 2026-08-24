package com.hawel.ledger_service.entity;

import com.hawel.common_service.enums.JournalStatus;
import com.hawel.ledger_service.enums.JournalType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "journal_entries",
        indexes = {
                @Index(
                        name = "idx_reference",
                        columnList = "reference",
                        unique = true
                ),
                @Index(
                        name = "idx_transaction_id",
                        columnList = "transaction_id"
                )

        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "journal_number", nullable = false, unique = true)
    private String journalNumber;

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Column(nullable = false, unique = true)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JournalType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JournalStatus status;

    @Column(length = 300)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {

        createdAt = LocalDateTime.now();

        if (status == null) {
            status = JournalStatus.PENDING;
        }
    }
}