package com.hawel.wallet_service.service.impl;

import com.hawel.wallet_service.config.WalletDefaultConfig;
import com.hawel.wallet_service.dto.request.CreateWalletRequest;
import com.hawel.wallet_service.dto.request.FreezeWalletRequest;
import com.hawel.wallet_service.dto.request.UpdateWalletLimitRequest;
import com.hawel.wallet_service.dto.request.UpdateWalletSettingsRequest;
import com.hawel.wallet_service.dto.response.WalletBalanceResponse;
import com.hawel.wallet_service.dto.response.WalletLimitResponse;
import com.hawel.wallet_service.dto.response.WalletResponse;
import com.hawel.wallet_service.dto.response.WalletSettingsResponse;
import com.hawel.wallet_service.entity.Wallet;
import com.hawel.wallet_service.entity.WalletLimit;
import com.hawel.wallet_service.entity.WalletSettings;
import com.hawel.wallet_service.entity.WalletStatusHistory;
import com.hawel.wallet_service.enums.WalletStatus;
import com.hawel.wallet_service.event.*;
import com.hawel.wallet_service.exception.ResourceNotFoundException;
import com.hawel.wallet_service.exception.WalletApiException;
import com.hawel.wallet_service.generator.WalletNumberGenerator;
import com.hawel.wallet_service.mapper.WalletMapper;
import com.hawel.wallet_service.repository.WalletLimitRepository;
import com.hawel.wallet_service.repository.WalletRepository;
import com.hawel.wallet_service.repository.WalletSettingsRepository;
import com.hawel.wallet_service.repository.WalletStatusHistoryRepository;
import com.hawel.wallet_service.service.WalletBalanceService;
import com.hawel.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


// TODO: Replace direct Kafka publishing with Outbox Pattern

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WalletServiceImpl implements WalletService {


    private final WalletRepository walletRepository;
    private final WalletLimitRepository walletLimitRepository;
    private final WalletSettingsRepository walletSettingsRepository;
    private final WalletStatusHistoryRepository walletStatusHistoryRepository;

    private final WalletMapper walletMapper;

    private final WalletEventPublisher eventPublisher;

    private final WalletNumberGenerator walletNumberGenerator;

    private final WalletBalanceService walletBalanceService;


    @Override
    public WalletResponse createWallet(CreateWalletRequest request) {

        log.info("Creating wallet for customerId={}, walletType={} ", request.getCustomerId(), request.getWalletType());

        // 1. Check if customer already has wallet
        boolean exists = walletRepository.existsByCustomerIdAndWalletType(request.getCustomerId(), request.getWalletType());

        // TODO : Enable this when we want to restrict customers to have only one wallet of each type
        //        if (exists) {
        //            throw new WalletApiException(HttpStatus.BAD_REQUEST, "Customer already has a wallet of this type");
        //        }

        // 3. Generate wallet number
        String walletNumber = walletNumberGenerator.generate();

        // 4. Create Wallet entity
        Wallet wallet = Wallet.builder()
                .walletNumber(walletNumber)
                .customerId(request.getCustomerId())
                .currency(request.getCurrency())
                .walletType(request.getWalletType())
                .status(WalletStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();


        // 5. Save wallet
        Wallet savedWallet = walletRepository.save(wallet);

        log.info("Wallet created successfully walletId={}, walletNumber={}", savedWallet.getId(), savedWallet.getWalletNumber());


        // 6. Create default limits
        WalletLimit limits = WalletLimit.builder()
                .walletId(savedWallet.getId())
                .dailyLimit(WalletDefaultConfig.DEFAULT_DAILY_LIMIT)
                .monthlyLimit(WalletDefaultConfig.DEFAULT_MONTHLY_LIMIT)
                .maximumBalance(WalletDefaultConfig.DEFAULT_MAXIMUM_BALANCE)
                .maximumTransaction(WalletDefaultConfig.DEFAULT_MAXIMUM_TRANSACTION)
                .build();


        walletLimitRepository.save(limits);

        // 7. Create default settings
        WalletSettings settings = WalletSettings.builder()
                .walletId(savedWallet.getId())
                .allowTransfer(true)
                .allowCashIn(true)
                .allowCashOut(true)
                .allowNfc(false)
                .allowQr(true)
                .build();

        walletSettingsRepository.save(settings);

        log.debug("Default wallet configuration created walletId={}", savedWallet.getId());

        // 8. Create initial wallet balance
       WalletBalanceResponse balance =  walletBalanceService.createInitialWalletBalance(savedWallet.getId());

        log.debug("Initial wallet balance created walletId={}", savedWallet.getId());

        // TODO : 8. Publish WalletCreated event
        eventPublisher.publish(new WalletCreatedEvent(
                        savedWallet.getId(),
                        savedWallet.getCustomerId(),
                        savedWallet.getWalletNumber(),
                        savedWallet.getCurrency(),
                        savedWallet.getWalletType()
                )
        );

        log.info("WalletCreatedEvent published walletId={}", savedWallet.getId());

        // 9. Return response
        return walletMapper.toResponse(savedWallet, limits, settings , balance);


    }

    @Override
    public WalletResponse getWalletById(UUID walletId) {

        log.debug("Fetching wallet walletId={}", walletId);

        Wallet wallet = getWallet(walletId);

        log.debug("Wallet found walletId={}, status={}", wallet.getId(), wallet.getStatus());

        return buildResponse(wallet);
    }

    @Override
    @Transactional(readOnly = true)
    public WalletResponse getWalletByNumber(String walletNumber) {

        log.debug("Fetching wallet by walletNumber={}", walletNumber);

        Wallet wallet = walletRepository.findByWalletNumber(walletNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Wallet",
                                "walletNumber",
                                UUID.randomUUID()
                        )
                );


        return buildResponse(wallet);

    }

    @Override
    public List<WalletResponse> getCustomerWallets(UUID customerId) {
        List<Wallet> wallets = walletRepository.findByCustomerId(customerId);

        log.info("Fetching wallets for customerId={}, totalWallets={}", customerId, wallets.size());

        return wallets.stream()
                .map(wallet -> {

                    WalletLimit walletLimit = walletLimitRepository.findByWalletId(wallet.getId()).orElse(null);

                    WalletSettings walletSettings = walletSettingsRepository.findByWalletId(wallet.getId()).orElse(null);

                    WalletBalanceResponse balance = walletBalanceService.getBalance(wallet.getId());

                    return walletMapper.toResponse(wallet, walletLimit, walletSettings , balance);

                })
                .collect(Collectors.toList());
    }

    @Override
    public WalletResponse activateWallet(UUID walletId) {

        log.info("Activating wallet walletId={}", walletId);

        // 1. Find wallet
        Wallet wallet = getWallet(walletId);

        // 2. Validate current state
        WalletStatus currentStatus = wallet.getStatus();


        if (currentStatus == WalletStatus.CLOSED) {

            log.warn("Cannot activate closed wallet walletId={}", walletId);

            throw new WalletApiException(HttpStatus.BAD_REQUEST, "Cannot activate closed wallet");
        }


        if (currentStatus == WalletStatus.ACTIVE) {

            log.warn("Wallet already active walletId={}", walletId);

            return buildResponse(wallet);
        }


        // 3. Change status
        wallet.setStatus(WalletStatus.ACTIVE);


        Wallet savedWallet = walletRepository.save(wallet);


        // 4. Save status history
        saveStatusHistory(savedWallet, currentStatus, WalletStatus.ACTIVE, "Wallet activated", null);

        // 5. Publish event
        eventPublisher.publish(new WalletActivatedEvent(savedWallet.getId()));

        log.info("Wallet activated successfully walletId={}", walletId);

        // 6. Return response
        return buildResponse(savedWallet);
    }

    @Override
    public WalletResponse freezeWallet(UUID walletId, FreezeWalletRequest request) {

        log.info("Freezing wallet walletId={}, reason={}", walletId, request.getReason());

        Wallet wallet = getWallet(walletId);


        WalletStatus currentStatus = wallet.getStatus();


        if (currentStatus == WalletStatus.CLOSED) {
            log.warn("Cannot freeze closed wallet walletId={}", walletId);

            throw new WalletApiException(HttpStatus.BAD_REQUEST, "Cannot freeze closed wallet");
        }


        if (currentStatus == WalletStatus.FROZEN) {

            log.warn("Wallet already frozen walletId={}", walletId);

            return buildResponse(wallet);
        }



        wallet.setStatus(WalletStatus.FROZEN);

        Wallet savedWallet = walletRepository.save(wallet);

        saveStatusHistory(savedWallet, currentStatus, WalletStatus.FROZEN, request.getReason(), null);

        eventPublisher.publish(new WalletFrozenEvent(savedWallet.getId(), request.getReason()));

        log.info("Wallet frozen successfully walletId={}", walletId);

        return buildResponse(wallet);

    }

    @Override
    public WalletResponse unfreezeWallet(UUID walletId) {

        log.info("Unfreezing wallet walletId={}", walletId);

        // 1. Find wallet
        Wallet wallet = getWallet(walletId);


        // 2. Validate current state
        WalletStatus currentStatus = wallet.getStatus();


        if (currentStatus != WalletStatus.FROZEN) {

            log.warn(
                    "Cannot unfreeze wallet because status is not FROZEN. walletId={}, currentStatus={}",
                    walletId,
                    currentStatus
            );


            throw new WalletApiException(HttpStatus.BAD_REQUEST, "Cannot unfreeze wallet because status is not FROZEN");

        }


        // 3. Change status
        wallet.setStatus(WalletStatus.ACTIVE);

        Wallet savedWallet = walletRepository.save(wallet);


        // 4. Save status history
        saveStatusHistory(savedWallet, currentStatus, WalletStatus.ACTIVE, "Wallet unfrozen", null);


        // 5. Publish event
        eventPublisher.publish(new WalletUnfrozenEvent(savedWallet.getId()));


        log.info("Wallet unfrozen successfully walletId={}", walletId);

        // 6. Return response
        return buildResponse(savedWallet);
    }

    @Override
    public WalletResponse suspendWallet(UUID walletId) {

        log.info("Suspending wallet walletId={}", walletId);


        // 1. Find wallet
        Wallet wallet = getWallet(walletId);


        // 2. Validate current state
        WalletStatus currentStatus = wallet.getStatus();


        if (currentStatus == WalletStatus.CLOSED) {

            log.warn("Cannot suspend closed wallet walletId={}", walletId);

            throw new WalletApiException(HttpStatus.BAD_REQUEST, "Cannot suspend closed wallet");

        }


        if (currentStatus == WalletStatus.SUSPENDED) {

            log.warn("Wallet already suspended walletId={}", walletId);

            return buildResponse(wallet);

        }


        // 3. Change status
        wallet.setStatus(WalletStatus.SUSPENDED);


        Wallet savedWallet = walletRepository.save(wallet);


        // 4. Save history
        // TODO : null should be the userId of the user who suspended the wallet, either admin or customer
        saveStatusHistory(savedWallet, currentStatus, WalletStatus.SUSPENDED, "Wallet suspended", null);

        log.info("Wallet suspended successfully walletId={}", walletId);

        // 5. Publish event
        // TODO : make "reason" dynamic based on the actual reason for suspension, currently hardcoded
        eventPublisher.publish(new WalletSuspendedEvent(savedWallet.getId() , "reason") );

        return buildResponse(savedWallet);
    }

    @Override
    public WalletResponse closeWallet(UUID walletId) {

        log.info("Closing wallet walletId={}", walletId);


        // 1. Find wallet
        Wallet wallet = getWallet(walletId);


        // 2. Validate current state
        WalletStatus currentStatus = wallet.getStatus();


        if (currentStatus == WalletStatus.CLOSED) {

            log.warn("Wallet already closed walletId={}", walletId);

            return buildResponse(wallet);
        }


        // 3. Change status
        wallet.setStatus(WalletStatus.CLOSED);


        Wallet savedWallet = walletRepository.save(wallet);


        // 4. Save status history
        saveStatusHistory(savedWallet, currentStatus, WalletStatus.CLOSED, "Wallet closed", null);


        // 5. Publish event
        eventPublisher.publish(new WalletClosedEvent(savedWallet.getId()));

        log.info("Wallet closed successfully walletId={}", walletId);


        // 6. Return response
        return buildResponse(savedWallet);
    }

    @Override
    public WalletLimitResponse updateLimits(UUID walletId, UpdateWalletLimitRequest request) {

        log.info("Updating wallet limits walletId={}", walletId);

        // 1. Check wallet exists
        Wallet wallet = getWallet(walletId);

        // 2. Validate wallet status
        if (wallet.getStatus() != WalletStatus.ACTIVE) {

            log.warn("Cannot update wallet limits because wallet is not active. walletId={}, status={}", walletId, wallet.getStatus());

            throw new WalletApiException(HttpStatus.BAD_REQUEST, "Wallet must be ACTIVE to update limits");
        }


        // 3. Load limits
        WalletLimit limits = walletLimitRepository.findByWalletId(walletId)
                .orElseThrow(() ->
                        new WalletApiException(
                                HttpStatus.NOT_FOUND,
                                "Wallet limits not found for walletId=" + walletId
                        )
                );


        // 4. Update only provided values

        if (request.getDailyLimit() != null) {
            limits.setDailyLimit(request.getDailyLimit());
        }


        if (request.getMonthlyLimit() != null) {
            limits.setMonthlyLimit(request.getMonthlyLimit());
        }


        if (request.getMaximumBalance() != null) {
            limits.setMaximumBalance(request.getMaximumBalance());
        }


        if (request.getMaximumTransaction() != null) {
            limits.setMaximumTransaction(request.getMaximumTransaction());
        }


        // 5. Save
        WalletLimit saved = walletLimitRepository.save(limits);


        log.debug("Wallet limits saved walletId={}, dailyLimit={}, monthlyLimit={}", walletId, saved.getDailyLimit(), saved.getMonthlyLimit());


        // 6. Publish Event
        eventPublisher.publish(new WalletLimitChangedEvent(walletId, saved.getDailyLimit(), saved.getMonthlyLimit(), saved.getMaximumBalance(), saved.getMaximumTransaction()));

        log.info("Wallet limits updated successfully walletId={}", walletId);


        // 7. Return Response
        return walletMapper.toLimitResponse(saved);
    }

    @Override
    public WalletSettingsResponse updateSettings(UUID walletId, UpdateWalletSettingsRequest request) {

        log.info("Updating wallet settings walletId={}", walletId);


        // 1. Validate wallet exists
        Wallet wallet = getWallet(walletId);

        // 2. Validate wallet status
        if (wallet.getStatus() != WalletStatus.ACTIVE) {

            log.warn("Cannot update wallet limits because wallet is not active. walletId={}, status={}", walletId, wallet.getStatus());

            throw new WalletApiException(HttpStatus.BAD_REQUEST, "Wallet must be ACTIVE to update limits");
        }

        // 3. Load settings
        WalletSettings settings = walletSettingsRepository.findByWalletId(walletId)
                .orElseThrow(() -> {

                    log.warn("Wallet settings not found walletId={}", walletId);


                    return new ResourceNotFoundException("WalletSettings", "walletId", walletId);

                });


        // 4. Update settings
        if (request.getAllowTransfer() != null) {

            settings.setAllowTransfer(request.getAllowTransfer());

        }


        if (request.getAllowCashIn() != null) {

            settings.setAllowCashIn(request.getAllowCashIn());

        }


        if (request.getAllowCashOut() != null) {

            settings.setAllowCashOut(request.getAllowCashOut());

        }


        if (request.getAllowNfc() != null) {

            settings.setAllowNfc(request.getAllowNfc());

        }


        if (request.getAllowQr() != null) {

            settings.setAllowQr(request.getAllowQr());

        }


        // 5. Save
        WalletSettings savedSettings = walletSettingsRepository.save(settings);


        log.info("Wallet settings updated successfully walletId={}", walletId);


        // 6. Publish Event
        eventPublisher.publish(new WalletSettingsChangedEvent(walletId, savedSettings.isAllowTransfer(), savedSettings.isAllowCashIn(), savedSettings.isAllowCashOut(), savedSettings.isAllowNfc(), savedSettings.isAllowQr()));


        // 7. Return response
        return walletMapper.toSettingsResponse(savedSettings);
    }


    // Get Wallet by ID and throw exception if not found
    private Wallet getWallet(UUID walletId) {

        return walletRepository.findById(walletId).orElseThrow(() -> {

            log.warn("Wallet not found walletId={}", walletId);

            return new ResourceNotFoundException("Wallet", "walletId", walletId);

        });

    }

    private WalletResponse buildResponse(Wallet wallet) {

        // Load Wallet Limit and Settings
        WalletLimit walletLimit = walletLimitRepository.findByWalletId(wallet.getId()).orElse(null);

        // Load Wallet Settings
        WalletSettings walletSettings = walletSettingsRepository.findByWalletId(wallet.getId()).orElse(null);

        // Load Wallet Balance
        WalletBalanceResponse balance = walletBalanceService.getBalance(wallet.getId());

        // Build Response
        return walletMapper.toResponse(wallet, walletLimit, walletSettings , balance);

    }


    // Save Wallet Status History
    private void saveStatusHistory(Wallet wallet, WalletStatus oldStatus, WalletStatus newStatus, String reason, UUID changedBy) {

        WalletStatusHistory history = WalletStatusHistory.builder()
                .walletId(wallet.getId())
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(changedBy)
                .reason(reason)
                .changedAt(LocalDateTime.now())
                .build();

        walletStatusHistoryRepository.save(history);
    }


    private Wallet changeWalletStatus(UUID walletId, WalletStatus newStatus, String reason) {


        Wallet wallet = getWallet(walletId);

        WalletStatus oldStatus = wallet.getStatus();

        log.info("Changing wallet status walletId={}, oldStatus={}, newStatus={}", walletId, oldStatus, newStatus);

        wallet.setStatus(newStatus);

        Wallet updatedWallet = walletRepository.save(wallet);

        // TODO : change null to the userId of the user who changed the status ,by admin or customer
        saveStatusHistory(updatedWallet, oldStatus, newStatus, reason, null);

        log.info("Wallet status changed successfully walletId={}", walletId);

        return updatedWallet;

    }


}