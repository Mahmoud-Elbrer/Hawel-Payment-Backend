package com.hawel.card_service.client;

import com.hawel.card_service.client.dto.WalletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

////@FeignClient(name = "transaction-service", path = "/v1/transactions")
@FeignClient(name = "wallet-service", url = "${services.wallet.url}")
public interface WalletClient {

    @GetMapping("/api/wallets/{walletId}")
    WalletResponse getWallet(@PathVariable UUID walletId);
}