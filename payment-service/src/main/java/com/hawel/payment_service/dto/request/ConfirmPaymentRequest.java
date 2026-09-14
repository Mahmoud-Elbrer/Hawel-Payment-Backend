package com.hawel.payment_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ConfirmPaymentRequest {

    @NotNull
    private UUID payerWalletId;

    @NotNull
    private String idempotencyKey;
}