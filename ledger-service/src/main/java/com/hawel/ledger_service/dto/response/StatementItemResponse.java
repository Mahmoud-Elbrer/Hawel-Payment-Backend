package com.hawel.ledger_service.dto.response;

import com.hawel.common_service.enums.CurrencyCode;
import com.hawel.ledger_service.enums.EntryType;
import com.hawel.ledger_service.enums.JournalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatementItemResponse {

    private String journalNumber;

    private UUID transactionId;

    private String reference;

    private JournalType journalType;

    private EntryType entryType;

    private BigDecimal amount;

    private CurrencyCode currencyCode;

    private Instant createdAt;
}