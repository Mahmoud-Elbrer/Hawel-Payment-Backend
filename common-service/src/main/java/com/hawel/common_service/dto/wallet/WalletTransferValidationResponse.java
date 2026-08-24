package com.hawel.common_service.dto.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransferValidationResponse {

    private boolean valid;

    private String reason;

    private UUID senderAccountId;

    private UUID receiverAccountId;
}