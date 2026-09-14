package com.hawel.payment_service.dto.request;

import com.hawel.common_service.enums.CurrencyCode;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CreateQrPaymentRequest {

    @NotNull
    private UUID receiverWalletId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code", nullable = false, length = 10)
    private CurrencyCode currencyCode = CurrencyCode.SDG;

    @Size(max = 500)
    private String description;
}