package com.hawel.wallet_service.event;


import lombok.Getter;

import java.util.UUID;


@Getter
public class WalletFrozenEvent extends BaseEvent {

    private final UUID walletId;
    private final String reason;

    public WalletFrozenEvent(
            UUID walletId,
            String reason
    ) {
        this.walletId = walletId;
        this.reason = reason;
    }
}