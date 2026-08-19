package com.hawel.wallet_service.event;

import com.hawel.common_service.enums.CurrencyCode;
import lombok.Getter;

import java.util.UUID;


import com.hawel.wallet_service.enums.WalletType;

@Getter
public class WalletCreatedEvent extends BaseEvent {

    private final UUID walletId;
    private final UUID customerId;
    private final String walletNumber;
    private final CurrencyCode currencyCode;
    private final WalletType walletType;

    public WalletCreatedEvent(
            UUID walletId,
            UUID customerId,
            String walletNumber,
            CurrencyCode currencyCode,
            WalletType walletType
    ) {
        this.walletId = walletId;
        this.customerId = customerId;
        this.walletNumber = walletNumber;
        this.currencyCode = currencyCode;
        this.walletType = walletType;
    }
}