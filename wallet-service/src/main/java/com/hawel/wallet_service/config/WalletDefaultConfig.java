package com.hawel.wallet_service.config;

import java.math.BigDecimal;

public final class WalletDefaultConfig {


    private WalletDefaultConfig() {
    }


    public static final BigDecimal DEFAULT_DAILY_LIMIT = new BigDecimal("50000");


    public static final BigDecimal DEFAULT_MONTHLY_LIMIT = new BigDecimal("500000");


    public static final BigDecimal DEFAULT_MAXIMUM_BALANCE = new BigDecimal("1000000");


    public static final BigDecimal DEFAULT_MAXIMUM_TRANSACTION = new BigDecimal("25000");

}