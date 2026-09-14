package com.hawel.wallet_service.service;


import com.hawel.common_service.dto.wallet.WalletTransferValidationRequest;
import com.hawel.common_service.dto.wallet.WalletTransferValidationResponse;
import com.hawel.wallet_service.dto.request.CreateWalletRequest;
import com.hawel.wallet_service.dto.request.FreezeWalletRequest;
import com.hawel.wallet_service.dto.request.UpdateWalletLimitRequest;
import com.hawel.wallet_service.dto.request.UpdateWalletSettingsRequest;
import com.hawel.wallet_service.dto.response.WalletLimitResponse;
import com.hawel.wallet_service.dto.response.WalletResponse;
import com.hawel.wallet_service.dto.response.WalletSettingsResponse;

import java.util.List;
import java.util.UUID;

public interface WalletService {
    WalletResponse createWallet(CreateWalletRequest request);

    WalletResponse getWalletById(UUID walletId);

    WalletResponse getWalletByNumber(String walletNumber);

    List<WalletResponse> getOwnerWallets(UUID ownerId);

    WalletResponse activateWallet(UUID walletId);

    WalletResponse freezeWallet(UUID walletId, FreezeWalletRequest request);

    WalletResponse unfreezeWallet(UUID walletId);

    WalletResponse suspendWallet(UUID walletId);

    WalletResponse closeWallet(UUID walletId);

    WalletLimitResponse updateLimits(UUID walletId, UpdateWalletLimitRequest request);

    WalletSettingsResponse updateSettings(UUID walletId, UpdateWalletSettingsRequest request);


    // Internal method for validating wallet transfer
    // it used by transaction service to validate transfer before processing it
    WalletTransferValidationResponse validateTransfer(WalletTransferValidationRequest request);
}