package com.hawel.wallet_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wallet_balances")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletBalance {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(nullable = false, unique = true)
    private UUID walletId;


    @Column(nullable = false)
    private BigDecimal availableBalance;


    @Column(nullable = false)
    private BigDecimal blockedBalance;


    private LocalDateTime lastUpdated;

}