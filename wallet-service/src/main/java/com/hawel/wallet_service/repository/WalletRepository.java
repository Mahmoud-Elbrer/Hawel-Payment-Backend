package com.hawel.wallet_service.repository;

import com.hawel.common_service.enums.WalletType;
import com.hawel.wallet_service.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    Optional<Wallet> findByWalletNumber(String walletNumber);

    List<Wallet> findByOwnerId(UUID ownerId);

    boolean existsByOwnerIdAndWalletType(UUID ownerId, WalletType walletType);

}
