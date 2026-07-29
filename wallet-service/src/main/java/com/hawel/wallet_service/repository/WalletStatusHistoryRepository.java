package com.hawel.wallet_service.repository;

import com.hawel.wallet_service.entity.WalletStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WalletStatusHistoryRepository extends JpaRepository<WalletStatusHistory, UUID> {
    List<WalletStatusHistory> findByWalletIdOrderByChangedAtDesc(UUID walletId);

}