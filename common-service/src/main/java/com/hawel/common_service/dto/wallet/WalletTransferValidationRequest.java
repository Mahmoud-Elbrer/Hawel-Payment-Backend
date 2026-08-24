package com.hawel.common_service.dto.wallet.dto;

import com.hawel.common_service.enums.CurrencyCode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransferValidationRequest {

    @NotNull(message = "Sender wallet ID is required")
    private UUID senderWalletId;

    @NotNull(message = "Receiver wallet ID is required")
    private UUID receiverWalletId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Currency is required")
    private CurrencyCode currencyCode;
}