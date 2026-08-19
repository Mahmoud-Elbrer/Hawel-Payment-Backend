package com.hawel.ledger_service.repository;


import com.hawel.ledger_service.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> , JpaSpecificationExecutor<LedgerEntry> {


    List<LedgerEntry> findByJournalId(UUID journalId);


    // This method is used to find all ledger entries for a given account id and order them by created at in descending order
    List<LedgerEntry> findByAccountIdOrderByCreatedAtDesc(UUID accountId);


}