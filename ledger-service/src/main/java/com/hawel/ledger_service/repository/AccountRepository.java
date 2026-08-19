package com.hawel.ledger_service.repository;

import com.hawel.common_service.enums.CurrencyCode;
import com.hawel.ledger_service.entity.Account;
import com.hawel.ledger_service.enums.AccountType;
import com.hawel.ledger_service.enums.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {


    Optional<Account> findByAccountNumber(String accountNumber);


    Optional<Account> findByOwnerIdAndOwnerType(UUID ownerId, OwnerType ownerType);

    @Query("SELECT a FROM Account a " +
            "WHERE a.accountType = :type " +
            "AND a.systemCode = :code " +
            "AND a.currency = :currency")
    Optional<Account> findSystemAccount(AccountType type, String code, CurrencyCode currency);


    boolean existsByOwnerIdAndOwnerType(UUID ownerId, OwnerType ownerType);


}