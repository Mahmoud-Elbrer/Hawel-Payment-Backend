package com.hawel.payment_service.dto.request;

import com.hawel.common_service.enums.CurrencyCode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNfcPaymentRequest {

    @NotBlank
    private String nfcUid;

    @NotNull
    private UUID receiverWalletId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @NotNull
    private CurrencyCode currencyCode;

    @Size(max = 500)
    private String description;
}