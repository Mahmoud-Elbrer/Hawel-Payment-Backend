package com.hawel.wallet_service.event;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class WalletLimitChangedEvent extends BaseEvent {

    private final UUID walletId;
    private final BigDecimal dailyLimit;
    private final BigDecimal monthlyLimit;
    private final BigDecimal maximumBalance;
    private final BigDecimal maximumTransaction;

    public WalletLimitChangedEvent(
            UUID walletId,
            BigDecimal dailyLimit,
            BigDecimal monthlyLimit,
            BigDecimal maximumBalance,
            BigDecimal maximumTransaction
    ) {
        this.walletId = walletId;
        this.dailyLimit = dailyLimit;
        this.monthlyLimit = monthlyLimit;
        this.maximumBalance = maximumBalance;
        this.maximumTransaction = maximumTransaction;
    }
}