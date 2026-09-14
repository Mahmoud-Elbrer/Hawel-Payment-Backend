package com.hawel.wallet_service.controller;

import com.hawel.wallet_service.dto.request.*;
import com.hawel.wallet_service.dto.response.*;
import com.hawel.wallet_service.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
@Slf4j
public class WalletController {


    private final WalletService walletService;


    /**
     * Create new wallet
     */
    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody CreateWalletRequest request) {

        log.info("Received request to create wallet for ownerId={}", request.getOwnerId());

        return ResponseEntity.status(HttpStatus.CREATED).body(walletService.createWallet(request));
    }


    /**
     * Get wallet by id
     */
    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponse> getWalletById(@PathVariable UUID walletId) {

        log.info("Received request to get wallet by id={}", walletId);

        return ResponseEntity.ok(walletService.getWalletById(walletId));
    }


    /**
     * Get wallet by wallet number
     */
    @GetMapping("/number/{walletNumber}")
    public ResponseEntity<WalletResponse> getWalletByNumber(@PathVariable String walletNumber) {

        log.info("Received request to get wallet by number={}", walletNumber);

        return ResponseEntity.ok(walletService.getWalletByNumber(walletNumber));
    }


    /**
     * Get all wallets for customer
     */
    @GetMapping("/customer/{ownerId}")
    public ResponseEntity<List<WalletResponse>> getOwnerWallets(@PathVariable UUID ownerId) {

        return ResponseEntity.ok(walletService.getOwnerWallets(ownerId));
    }


    /**
     * Activate wallet
     */
    @PutMapping("/{walletId}/activate")
    public ResponseEntity<WalletResponse> activateWallet(@PathVariable UUID walletId) {

        return ResponseEntity.ok(walletService.activateWallet(walletId));
    }


    /**
     * Freeze wallet
     */
    @PutMapping("/{walletId}/freeze")
    public ResponseEntity<WalletResponse> freezeWallet(@PathVariable UUID walletId, @Valid @RequestBody FreezeWalletRequest request) {

        return ResponseEntity.ok(walletService.freezeWallet(walletId, request));
    }


    /**
     * Unfreeze wallet
     */
    @PutMapping("/{walletId}/unfreeze")
    public ResponseEntity<WalletResponse> unfreezeWallet(@PathVariable UUID walletId) {

        return ResponseEntity.ok(walletService.unfreezeWallet(walletId));
    }


    /**
     * Suspend wallet
     */
    @PutMapping("/{walletId}/suspend")
    public ResponseEntity<WalletResponse> suspendWallet(@PathVariable UUID walletId) {

        return ResponseEntity.ok(walletService.suspendWallet(walletId));
    }


    /**
     * Close wallet
     */
    @PutMapping("/{walletId}/close")
    public ResponseEntity<WalletResponse> closeWallet(@PathVariable UUID walletId) {

        return ResponseEntity.ok(walletService.closeWallet(walletId));
    }


    /**
     * Update wallet limits
     */
    @PutMapping("/{walletId}/limits")
    public ResponseEntity<WalletLimitResponse> updateLimits(@PathVariable UUID walletId, @Valid @RequestBody UpdateWalletLimitRequest request) {

        return ResponseEntity.ok(walletService.updateLimits(walletId, request));
    }


    /**
     * Update wallet settings
     */
    @PutMapping("/{walletId}/settings")
    public ResponseEntity<WalletSettingsResponse> updateSettings(@PathVariable UUID walletId, @RequestBody UpdateWalletSettingsRequest request) {

        return ResponseEntity.ok(walletService.updateSettings(walletId, request));
    }

}