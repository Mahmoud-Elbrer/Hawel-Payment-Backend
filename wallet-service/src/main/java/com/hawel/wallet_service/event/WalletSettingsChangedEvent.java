package com.hawel.wallet_service.event;

import com.hawel.common_service.event.BaseEvent;
import lombok.Getter;


import java.util.UUID;

@Getter
public class WalletSettingsChangedEvent extends BaseEvent {

    private final UUID walletId;

    private final Boolean allowTransfer;

    private final Boolean allowCashIn;

    private final Boolean allowCashOut;

    private final Boolean allowNfc;

    private final Boolean allowQr;


    public WalletSettingsChangedEvent(
            UUID walletId,
            Boolean allowTransfer,
            Boolean allowCashIn,
            Boolean allowCashOut,
            Boolean allowNfc,
            Boolean allowQr
    ) {
        this.walletId = walletId;
        this.allowTransfer = allowTransfer;
        this.allowCashIn = allowCashIn;
        this.allowCashOut = allowCashOut;
        this.allowNfc = allowNfc;
        this.allowQr = allowQr;
    }
}