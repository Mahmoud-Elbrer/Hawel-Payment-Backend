package com.hawel.wallet_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWalletSettingsRequest {

    private Boolean allowTransfer;

    private Boolean allowCashIn;

    private Boolean allowCashOut;

    private Boolean allowNfc;

    private Boolean allowQr;
}