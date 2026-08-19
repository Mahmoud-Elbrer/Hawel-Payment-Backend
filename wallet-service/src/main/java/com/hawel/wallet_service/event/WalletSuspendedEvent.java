package com.hawel.wallet_service.event;

import com.hawel.common_service.event.BaseEvent;

import lombok.Getter;

import java.util.UUID;

@Getter
public class WalletSuspendedEvent extends BaseEvent {

    private final UUID walletId;
    private final String reason;

    public WalletSuspendedEvent(
            UUID walletId,
            String reason
    ) {
        this.walletId = walletId;
        this.reason = reason;
    }
}
