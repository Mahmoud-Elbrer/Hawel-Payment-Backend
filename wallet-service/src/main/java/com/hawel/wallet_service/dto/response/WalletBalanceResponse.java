package com.hawel.wallet_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletBalanceResponse {

    private UUID walletId;

    private BigDecimal availableBalance;

    private BigDecimal blockedBalance;

    private LocalDateTime lastUpdated;
}