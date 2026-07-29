package com.hawel.wallet_service.event;

import lombok.Getter;

import java.util.UUID;


@Getter
public class WalletClosedEvent extends BaseEvent {


    private final UUID walletId;

    public WalletClosedEvent(UUID walletId) {
        this.walletId = walletId;
    }
}