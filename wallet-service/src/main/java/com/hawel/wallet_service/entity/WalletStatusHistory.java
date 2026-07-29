package com.hawel.wallet_service.entity;

import com.hawel.wallet_service.enums.WalletStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wallet_status_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID walletId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletStatus newStatus;

    /**
     * User/Admin/System who changed the status.
     * Can be null when changed automatically by the system.
     */
    private UUID changedBy;

    @Column(length = 500)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime changedAt;

}