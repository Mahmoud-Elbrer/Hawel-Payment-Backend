package com.hawel.payment_service.dto.response;

import com.hawel.common_service.enums.CurrencyCode;
import com.hawel.payment_service.enums.PaymentMethod;
import com.hawel.payment_service.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class PaymentResponse {

    private UUID id;

    private String paymentReference;

    private UUID payerWalletId;

    private UUID receiverWalletId;

    private BigDecimal amount;

    private CurrencyCode currencyCode ;

    private PaymentMethod paymentMethod;

    private PaymentStatus status;

    private UUID transactionId;

    private String description;

    private Instant expiresAt;

    private Instant createdAt;

    private Instant updatedAt;
}