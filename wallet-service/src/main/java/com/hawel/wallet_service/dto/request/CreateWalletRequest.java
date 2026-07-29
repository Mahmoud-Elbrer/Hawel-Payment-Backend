package com.hawel.wallet_service.dto.request;

import com.hawel.wallet_service.enums.Currency;
import com.hawel.wallet_service.enums.WalletType;
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

        @NotNull(message = "Currency is required")
        private Currency currency;

        @NotNull(message = "Wallet type is required")
        private WalletType walletType;
}