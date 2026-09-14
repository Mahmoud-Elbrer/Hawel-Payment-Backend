package com.hawel.wallet_service.mapper;

import com.hawel.wallet_service.dto.response.*;
import com.hawel.wallet_service.entity.Wallet;
import com.hawel.wallet_service.entity.WalletLimit;
import com.hawel.wallet_service.entity.WalletSettings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface WalletMapper {


    @Mapping(source = "wallet.id", target = "id")
    @Mapping(source = "wallet.walletNumber", target = "walletNumber")
    @Mapping(source = "wallet.ownerId", target = "ownerId")
    @Mapping(source = "wallet.currencyCode", target = "currencyCode")
    @Mapping(source = "wallet.walletType", target = "walletType")
    @Mapping(source = "wallet.status", target = "status")
    @Mapping(source = "wallet.createdAt", target = "createdAt")
    @Mapping(source = "limits", target = "limits")
    @Mapping(source = "settings", target = "settings")
    @Mapping(source = "balance", target = "balance")
    WalletResponse toResponse(
            Wallet wallet,
            WalletLimit limits,
            WalletSettings settings ,
            WalletBalanceResponse balance
    );


    WalletLimitResponse toLimitResponse(
            WalletLimit walletLimit
    );


    WalletSettingsResponse toSettingsResponse(
            WalletSettings walletSettings
    );

}