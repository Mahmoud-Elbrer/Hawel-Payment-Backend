package com.hawel.wallet_service.service;

import com.hawel.wallet_service.dto.response.WalletBalanceResponse;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletBalanceService {

    // This method is used to retrieve the wallet balance for a given wallet ID.
    WalletBalanceResponse getBalance(UUID walletId);

    // This method is used to create an initial wallet balance for a new wallet.
    WalletBalanceResponse createInitialWalletBalance(UUID walletId);

    // This method is used to synchronize the wallet balance from the ledger service.
    void syncBalanceFromLedger(UUID walletId, BigDecimal availableBalance, BigDecimal blockedBalance);

}
