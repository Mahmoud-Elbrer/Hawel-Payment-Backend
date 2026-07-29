package com.hawel.wallet_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wallet_sequence")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletSequence {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(
            nullable = false,
            unique = true
    )
    private String sequenceName;


    @Column(nullable = false)
    private Long currentValue;

}