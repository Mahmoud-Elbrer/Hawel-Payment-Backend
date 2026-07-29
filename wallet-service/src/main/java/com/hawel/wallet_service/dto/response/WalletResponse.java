package com.hawel.wallet_service.dto.response;

import com.hawel.wallet_service.enums.Currency;
import com.hawel.wallet_service.enums.WalletStatus;
import com.hawel.wallet_service.enums.WalletType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {

    private UUID id;

    private String walletNumber;

    private UUID customerId;

    private Currency currency;

    private WalletType walletType;

    private WalletStatus status;

    private LocalDateTime createdAt;

    private WalletLimitResponse limits;

    private WalletBalanceResponse balance;

    private WalletSettingsResponse settings;
}