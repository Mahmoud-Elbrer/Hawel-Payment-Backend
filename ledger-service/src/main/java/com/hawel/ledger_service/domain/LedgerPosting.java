package com.hawel.ledger_service.domain;

import com.hawel.common_service.enums.CurrencyCode;
import com.hawel.ledger_service.enums.EntryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class LedgerPosting {

    private UUID accountId;

    private EntryType entryType;

    private BigDecimal amount;

    private CurrencyCode currency;
}