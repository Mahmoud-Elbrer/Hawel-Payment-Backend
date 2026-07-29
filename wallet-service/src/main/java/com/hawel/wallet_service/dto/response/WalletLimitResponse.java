package com.hawel.wallet_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletLimitResponse {

    private BigDecimal dailyLimit;

    private BigDecimal monthlyLimit;

    private BigDecimal maximumBalance;

    private BigDecimal maximumTransaction;
}