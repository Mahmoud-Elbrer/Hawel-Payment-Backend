package com.hawel.wallet_service.service.impl;

import com.hawel.wallet_service.dto.response.WalletBalanceResponse;
import com.hawel.wallet_service.entity.WalletBalance;
import com.hawel.wallet_service.exception.WalletApiException;
import com.hawel.wallet_service.mapper.WalletBalanceMapper;
import com.hawel.wallet_service.repository.WalletBalanceRepository;
import com.hawel.wallet_service.service.WalletBalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WalletBalanceServiceImpl implements WalletBalanceService {


    private final WalletBalanceRepository repository;

    private final WalletBalanceMapper walletBalanceMapper;


    @Override
    @Transactional(readOnly = true)
    public WalletBalanceResponse getBalance(UUID walletId) {


        log.debug("Fetching wallet balance walletId={}", walletId);


        WalletBalance balance = repository.findByWalletId(walletId)
                .orElseThrow(() ->
                        new WalletApiException(
                                HttpStatus.NOT_FOUND,
                                "Wallet balance not found"
                        )
                );

        log.debug("Wallet balance fetched walletId={}", walletId);

        return walletBalanceMapper.toResponse(balance);

    }


    @Override
    public WalletBalanceResponse createInitialWalletBalance(UUID walletId) {


        log.info("Creating wallet balance walletId={}", walletId);


        WalletBalance balance = WalletBalance.builder()
                .walletId(walletId)
                .availableBalance(BigDecimal.ZERO)
                .blockedBalance(BigDecimal.ZERO)
                .lastUpdated(LocalDateTime.now())
                .build();


        WalletBalance saved = repository.save(balance);

        log.info("Wallet balance created walletId={}", walletId);

        return walletBalanceMapper.toResponse(saved);

    }


    @Override
    public void syncBalanceFromLedger(UUID walletId, BigDecimal availableBalance, BigDecimal blockedBalance) {

        log.info("Updating wallet balance walletId={}", walletId);

        WalletBalance balance = repository.findByWalletId(walletId)
                .orElseThrow(() ->
                        new WalletApiException(
                                HttpStatus.NOT_FOUND,
                                "Wallet balance not found"
                        )
                );


        balance.setAvailableBalance(availableBalance);

        balance.setBlockedBalance(blockedBalance);

        balance.setLastUpdated(LocalDateTime.now());

        repository.save(balance);

        log.info("Wallet balance updated walletId={}", walletId);

    }


}
