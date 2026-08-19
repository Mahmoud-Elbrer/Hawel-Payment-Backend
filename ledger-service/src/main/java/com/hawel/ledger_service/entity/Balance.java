package com.hawel.ledger_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "balances")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Balance {

    @Id
    private UUID accountId;

    @OneToOne(fetch = FetchType.LAZY)
    // mapsId indicates that the primary key of this entity is also a foreign key to the Account entity
    // accounts.id = balances.account_id
    @MapsId
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "available_balance", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal availableBalance = BigDecimal.ZERO;

    @Column(name = "blocked_balance", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal blockedBalance = BigDecimal.ZERO;

    @Version
    private Long version;


    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        updatedAt = Instant.now();
    }
}