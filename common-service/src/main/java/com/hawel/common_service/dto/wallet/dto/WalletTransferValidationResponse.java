package com.hawel.transaction_service.client.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransferValidationResponse {

    private boolean valid;

    private String reason;
}