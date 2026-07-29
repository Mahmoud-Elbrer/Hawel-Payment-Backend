package com.hawel.wallet_service.event;


import lombok.Getter;

import java.util.UUID;


@Getter
public class WalletUnfrozenEvent extends BaseEvent {

    private final UUID walletId;

    public WalletUnfrozenEvent(UUID walletId) {
        this.walletId = walletId;
    }
}