package com.hawel.wallet_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(name = "wallet_limits")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletLimit {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;


    @Column(
            name = "daily_limit",
            precision = 19,
            scale = 2
    )
    private BigDecimal dailyLimit;


    @Column(
            name = "monthly_limit",
            precision = 19,
            scale = 2
    )
    private BigDecimal monthlyLimit;


    @Column(
            name = "maximum_balance",
            precision = 19,
            scale = 2
    )
    private BigDecimal maximumBalance;


    @Column(
            name = "maximum_transaction",
            precision = 19,
            scale = 2
    )
    private BigDecimal maximumTransaction;

}