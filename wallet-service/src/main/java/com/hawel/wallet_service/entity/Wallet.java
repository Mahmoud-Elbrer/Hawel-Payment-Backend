package com.hawel.wallet_service.entity;

import com.hawel.wallet_service.enums.Currency;
import com.hawel.wallet_service.enums.WalletStatus;
import com.hawel.wallet_service.enums.WalletType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="wallets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {


    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;


    @Column(unique = true , nullable = false ,  length = 20)
    private String walletNumber;


    private UUID customerId;


    @Enumerated(EnumType.STRING)
    private Currency currency = Currency.SDG;


    @Enumerated(EnumType.STRING)
    private WalletType walletType;


    @Enumerated(EnumType.STRING)
    private WalletStatus status;


    private LocalDateTime createdAt;

}