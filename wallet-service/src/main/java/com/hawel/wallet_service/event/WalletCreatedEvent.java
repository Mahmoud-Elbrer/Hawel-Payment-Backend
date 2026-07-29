package com.hawel.wallet_service.event;

import lombok.Getter;

import java.util.UUID;


import com.hawel.wallet_service.enums.Currency;
import com.hawel.wallet_service.enums.WalletType;

@Getter
public class WalletCreatedEvent extends BaseEvent {

    private final UUID walletId;
    private final UUID customerId;
    private final String walletNumber;
    private final Currency currency;
    private final WalletType walletType;

    public WalletCreatedEvent(
            UUID walletId,
            UUID customerId,
            String walletNumber,
            Currency currency,
            WalletType walletType
    ) {
        this.walletId = walletId;
        this.customerId = customerId;
        this.walletNumber = walletNumber;
        this.currency = currency;
        this.walletType = walletType;
    }
}