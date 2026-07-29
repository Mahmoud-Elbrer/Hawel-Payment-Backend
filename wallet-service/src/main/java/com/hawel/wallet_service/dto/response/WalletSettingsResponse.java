package com.hawel.wallet_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletSettingsResponse {

    private boolean allowTransfer;

    private boolean allowCashIn;

    private boolean allowCashOut;

    private boolean allowNfc;

    private boolean allowQr;
}