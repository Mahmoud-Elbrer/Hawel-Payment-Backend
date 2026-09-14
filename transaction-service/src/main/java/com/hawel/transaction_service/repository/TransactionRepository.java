package com.hawel.transaction_service.repository;

import com.hawel.transaction_service.entity.Transaction;
import com.hawel.transaction_service.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Optional<Transaction> findByReferenceNumber(String referenceNumber);

    boolean existsByReferenceNumber(String referenceNumber);

    List<Transaction> findByStatus(TransactionStatus status);

}