package com.hawel.transaction_service.client.wallet;


import com.hawel.common_service.dto.wallet.WalletTransferValidationRequest;
import com.hawel.common_service.dto.wallet.WalletTransferValidationResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//@FeignClient(name = "wallet-service") this with service discovery
@FeignClient(name = "wallet-service", url = "${services.wallet.url}")
public interface WalletClient {

    @PostMapping("/api/internal/wallets/transfer-validation")
    WalletTransferValidationResponse validateTransfer(
      @Valid @RequestBody WalletTransferValidationRequest request
    );
}