package com.hawel.wallet_service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWalletLimitRequest {

        @DecimalMin(
                value = "0.0",
                message = "Daily limit must be greater than or equal to zero"
        )
        private BigDecimal dailyLimit;


        @DecimalMin(
                value = "0.0",
                message = "Monthly limit must be greater than or equal to zero"
        )
        private BigDecimal monthlyLimit;


        @DecimalMin(
                value = "0.0",
                message = "Maximum balance must be greater than or equal to zero"
        )
        private BigDecimal maximumBalance;


        @DecimalMin(
                value = "0.0",
                message = "Maximum transaction must be greater than or equal to zero"
        )
        private BigDecimal maximumTransaction;
}