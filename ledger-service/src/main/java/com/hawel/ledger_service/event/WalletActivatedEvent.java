package com.hawel.wallet_service.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class WalletActivatedEvent extends BaseEvent {

    private final UUID walletId;

    public WalletActivatedEvent(UUID walletId) {
        this.walletId = walletId;
    }
}
