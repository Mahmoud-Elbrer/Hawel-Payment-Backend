package com.hawel.wallet_service.mapper;

import com.hawel.wallet_service.dto.response.WalletBalanceResponse;
import com.hawel.wallet_service.entity.WalletBalance;
import org.springframework.stereotype.Component;

@Component
public class WalletBalanceMapper {

    public WalletBalanceResponse toResponse(WalletBalance balance) {

        return new WalletBalanceResponse(

                balance.getWalletId(),

                balance.getAvailableBalance(),

                balance.getBlockedBalance(),

                balance.getLastUpdated()

        );

    }

}