package com.hawel.wallet_service.repository;


import com.hawel.wallet_service.entity.WalletLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface WalletLimitRepository extends JpaRepository<WalletLimit, UUID> {


    Optional<WalletLimit> findByWalletId(UUID walletId);

}