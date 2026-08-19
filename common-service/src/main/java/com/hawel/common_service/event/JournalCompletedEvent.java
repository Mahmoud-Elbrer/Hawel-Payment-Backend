package com.hawel.common_service.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hawel.common_service.enums.CurrencyCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class JournalCompletedEvent extends BaseEvent {

    private final UUID transactionId;
    private final UUID journalId;
    private final String journalNumber;
    private final String reference;
    private final String journalType;
    private final CurrencyCode currency;
    private final BigDecimal amount;
    private List<WalletBalanceUpdate> walletBalances;
    private final String status;

    // Used by Ledger Service when creating a new event
    public JournalCompletedEvent(
            UUID transactionId,
            UUID journalId,
            String journalNumber,
            String reference,
            String journalType,
            CurrencyCode currency,
            BigDecimal amount,
            List<WalletBalanceUpdate> walletBalances ,
            String status
    ) {
        super();

        this.transactionId = transactionId;
        this.journalId = journalId;
        this.journalNumber = journalNumber;
        this.reference = reference;
        this.journalType = journalType;
        this.currency = currency;
        this.amount = amount;
        this.walletBalances = walletBalances;
        this.status = status;
    }

    // Used by Jackson when consuming from Kafka
    @JsonCreator
    public JournalCompletedEvent(
            @JsonProperty("eventId") UUID eventId,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("transactionId") UUID transactionId,
            @JsonProperty("journalId") UUID journalId,
            @JsonProperty("journalNumber") String journalNumber,
            @JsonProperty("reference") String reference,
            @JsonProperty("journalType") String journalType,
            @JsonProperty("currency") CurrencyCode currency,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("walletBalances") List<WalletBalanceUpdate>  walletBalances,
            @JsonProperty("status") String status
    ) {
        super(eventId, createdAt);

        this.transactionId = transactionId;
        this.journalId = journalId;
        this.journalNumber = journalNumber;
        this.reference = reference;
        this.journalType = journalType;
        this.currency = currency;
        this.amount = amount;
        this.walletBalances = walletBalances;
        this.status = status;
    }
}