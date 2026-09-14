package com.hawel.ledger_service.repository;

import com.hawel.ledger_service.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, UUID> {

    Optional<JournalEntry> findByReference(String reference);


    // use to find a journal entry by its reference
    // and avoid duplicate journal entries for the same transaction it Idempotency
    boolean existsByReference(String reference);


    /**
     * Find journal entry by Transaction ID.
     *
     * Used by Transaction Service during reconciliation
     * when a transaction is stuck in PROCESSING.
     */
    Optional<JournalEntry> findByTransactionId(UUID transactionId);



}