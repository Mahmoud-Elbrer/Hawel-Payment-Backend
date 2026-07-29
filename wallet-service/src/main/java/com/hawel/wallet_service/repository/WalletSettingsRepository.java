package com.hawel.wallet_service.repository;


import com.hawel.wallet_service.entity.WalletSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface WalletSettingsRepository extends JpaRepository<WalletSettings, UUID> {


    Optional<WalletSettings> findByWalletId(UUID walletId);

}