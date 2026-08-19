package com.hawel.common_service.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hawel.common_service.enums.CurrencyCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class JournalCompletedEvent extends BaseEvent {

    private final UUID transactionId;
    private final UUID journalId;
    private final UUID walletId;
    private final String journalNumber;
    private final String reference;
    private final String journalType;
    private final CurrencyCode currency;
    private final BigDecimal amount;
    private final BigDecimal availableBalance;
    private final BigDecimal blockedBalance;
    private final String status;

    // Used by Ledger Service when creating a new event
    public JournalCompletedEvent(
            UUID transactionId,
            UUID journalId,
            UUID walletId,
            String journalNumber,
            String reference,
            String journalType,
            CurrencyCode currency,
            BigDecimal amount,
            BigDecimal availableBalance,
            BigDecimal blockedBalance,
            String status
    ) {
        super();

        this.transactionId = transactionId;
        this.journalId = journalId;
        this.walletId = walletId;
        this.journalNumber = journalNumber;
        this.reference = reference;
        this.journalType = journalType;
        this.currency = currency;
        this.amount = amount;
        this.availableBalance = availableBalance;
        this.blockedBalance = blockedBalance;
        this.status = status;
    }

    // Used by Jackson when consuming from Kafka
    @JsonCreator
    public JournalCompletedEvent(
            @JsonProperty("eventId") UUID eventId,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("transactionId") UUID transactionId,
            @JsonProperty("journalId") UUID journalId,
            @JsonProperty("walletId") UUID walletId,
            @JsonProperty("journalNumber") String journalNumber,
            @JsonProperty("reference") String reference,
            @JsonProperty("journalType") String journalType,
            @JsonProperty("currency") CurrencyCode currency,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("availableBalance") BigDecimal availableBalance,
            @JsonProperty("blockedBalance") BigDecimal blockedBalance,
            @JsonProperty("status") String status
    ) {
        super(eventId, createdAt);

        this.transactionId = transactionId;
        this.journalId = journalId;
        this.walletId = walletId;
        this.journalNumber = journalNumber;
        this.reference = reference;
        this.journalType = journalType;
        this.currency = currency;
        this.amount = amount;
        this.availableBalance = availableBalance;
        this.blockedBalance = blockedBalance;
        this.status = status;
    }
}