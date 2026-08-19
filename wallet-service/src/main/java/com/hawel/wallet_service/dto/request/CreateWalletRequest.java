package com.hawel.wallet_service.dto.request;

import com.hawel.common_service.enums.CurrencyCode;
import com.hawel.common_service.enums.WalletType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWalletRequest {

        @NotNull(message = "Customer ID is required")
        private UUID customerId;

        @NotNull(message = "CurrencyCode is required")
        private CurrencyCode currencyCode;

        @NotNull(message = "Wallet type is required")
        private WalletType walletType;
}