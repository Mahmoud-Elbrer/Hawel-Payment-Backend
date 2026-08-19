package com.hawel.ledger_service.event;

import com.hawel.common_service.enums.CurrencyCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalCompletedEvent {

    private UUID eventId;

    private UUID transactionId;

    private UUID journalId;

    private String journalNumber;

    private String reference;

    private String journalType;

    private CurrencyCode currency;

    private BigDecimal amount;

    private String status;
}