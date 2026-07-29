package com.hawel.wallet_service.controller;

import com.hawel.wallet_service.dto.response.WalletBalanceResponse;
import com.hawel.wallet_service.service.WalletBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/wallet-balances")
@RequiredArgsConstructor
public class WalletBalanceController {


    private final WalletBalanceService service;


    @GetMapping("/{walletId}")
    public ResponseEntity<WalletBalanceResponse> getBalance(@PathVariable UUID walletId) {

        return ResponseEntity.ok(service.getBalance(walletId));

    }

}