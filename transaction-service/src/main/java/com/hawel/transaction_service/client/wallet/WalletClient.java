package com.hawel.transaction_service.client.wallet;

import com.hawel.transaction_service.client.wallet.dto.WalletTransferValidationRequest;
import com.hawel.transaction_service.client.wallet.dto.WalletTransferValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "wallet-service"
)
public interface WalletClient {

    @PostMapping("/internal/wallets/transfer-validation")
    WalletTransferValidationResponse validateTransfer(
            @RequestBody WalletTransferValidationRequest request
    );
}