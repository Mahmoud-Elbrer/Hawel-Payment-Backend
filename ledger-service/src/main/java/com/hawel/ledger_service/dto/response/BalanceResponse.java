package com.hawel.ledger_service.dto.response;

import com.hawel.common_service.enums.CurrencyCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceResponse {

    private UUID accountId;

    private BigDecimal availableBalance;

    private BigDecimal blockedBalance;

    private CurrencyCode currencyCode;

    private Instant updatedAt;
}