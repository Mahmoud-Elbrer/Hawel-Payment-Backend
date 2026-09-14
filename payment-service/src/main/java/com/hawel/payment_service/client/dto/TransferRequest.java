package com.hawel.payment_service.client.dto;

import com.hawel.common_service.enums.CurrencyCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    private UUID senderWalletId;

    private UUID receiverWalletId;

    private BigDecimal amount;

    private CurrencyCode currencyCode;
}