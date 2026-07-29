package com.hawel.wallet_service.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Entity
@Table(name = "wallet_settings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletSettings {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;


    @Column(nullable = false)
    private boolean allowTransfer;


    @Column(nullable = false)
    private boolean allowCashIn;


    @Column(nullable = false)
    private boolean allowCashOut;


    @Column(nullable = false)
    private boolean allowNfc;


    @Column(nullable = false)
    private boolean allowQr;

}