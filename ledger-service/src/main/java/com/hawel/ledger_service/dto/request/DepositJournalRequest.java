package com.hawel.ledger_service.dto.request;

import com.hawel.common_service.enums.CurrencyCode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositJournalRequest {

    @NotNull
    private UUID transactionId;

    @NotBlank
    private String reference;

    @NotNull
    private UUID accountId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    private CurrencyCode currencyCode;

    private String description;
}