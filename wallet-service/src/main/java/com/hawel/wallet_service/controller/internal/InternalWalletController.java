package com.hawel.wallet_service.controller.internal;

import com.hawel.common_service.dto.wallet.WalletTransferValidationRequest;
import com.hawel.common_service.dto.wallet.WalletTransferValidationResponse;
import com.hawel.wallet_service.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/wallets")
@RequiredArgsConstructor
public class InternalWalletController {

    private final WalletService walletService;

    @PostMapping("/transfer-validation")
    public ResponseEntity<WalletTransferValidationResponse> validateTransfer(@Valid @RequestBody WalletTransferValidationRequest request) {

        WalletTransferValidationResponse response = walletService.validateTransfer(request);

        return ResponseEntity.ok(response);
    }
}