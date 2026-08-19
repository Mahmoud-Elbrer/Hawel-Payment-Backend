package com.hawel.ledger_service.repository;

import com.hawel.ledger_service.entity.Balance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;


public interface BalanceRepository extends JpaRepository<Balance, UUID> {


    // This method is used to find a balance by account id and lock the row for update to prevent concurrent updates
    // we use PESSIMISTIC_WRITE lock mode to lock the row for update
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Balance b WHERE b.account.id = :accountId")
    Optional<Balance> findByAccountIdForUpdate(UUID accountId);


    Optional<Balance> findByAccountId(UUID accountId);

}