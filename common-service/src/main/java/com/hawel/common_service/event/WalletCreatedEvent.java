package com.hawel.common_service.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hawel.common_service.enums.CurrencyCode;
import com.hawel.common_service.enums.WalletType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class WalletCreatedEvent extends BaseEvent {

    private final UUID walletId;
    private final UUID customerId;
    private final String walletNumber;
    private final CurrencyCode currencyCode;
    private final WalletType walletType;

    // Used by Wallet Service when creating a new event
    public WalletCreatedEvent(
            UUID walletId,
            UUID customerId,
            String walletNumber,
            CurrencyCode currencyCode,
            WalletType walletType
    ) {
        super();

        this.walletId = walletId;
        this.customerId = customerId;
        this.walletNumber = walletNumber;
        this.currencyCode = currencyCode;
        this.walletType = walletType;
    }

    // Used by Jackson when consuming from Kafka
    @JsonCreator
    public WalletCreatedEvent(
            @JsonProperty("eventId") UUID eventId,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("walletId") UUID walletId,
            @JsonProperty("customerId") UUID customerId,
            @JsonProperty("walletNumber") String walletNumber,
            @JsonProperty("currencyCode") CurrencyCode currencyCode,
            @JsonProperty("walletType") WalletType walletType
    ) {
        super(eventId, createdAt);

        this.walletId = walletId;
        this.customerId = customerId;
        this.walletNumber = walletNumber;
        this.currencyCode = currencyCode;
        this.walletType = walletType;
    }
}