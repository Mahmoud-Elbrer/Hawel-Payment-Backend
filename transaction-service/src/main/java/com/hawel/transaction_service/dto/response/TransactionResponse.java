package com.hawel.transaction_service.dto.response;

import com.hawel.common_service.enums.CurrencyCode;
import com.hawel.transaction_service.enums.TransactionStatus;
import com.hawel.transaction_service.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private UUID id;

    private String referenceNumber;

    private TransactionType transactionType;

    private BigDecimal amount;

    private CurrencyCode currencyCode;

    private TransactionStatus status;

    private UUID senderWalletId;

    private UUID receiverWalletId;

    private Instant createdAt;

    private Instant completedAt;
}