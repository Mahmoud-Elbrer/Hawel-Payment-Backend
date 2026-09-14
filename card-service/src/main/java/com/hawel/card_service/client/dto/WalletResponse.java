package com.hawel.card_service.client.dto;

import com.hawel.common_service.enums.CurrencyCode;
import com.hawel.common_service.enums.WalletStatus;
import com.hawel.common_service.enums.WalletType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletResponse {

    private UUID id;

    private UUID ownerId;

    private String walletNumber;

    private CurrencyCode currencyCode;

    private WalletType walletType;

    private WalletStatus status;
}