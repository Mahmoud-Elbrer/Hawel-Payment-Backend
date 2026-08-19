package com.hawel.ledger_service.repository;

import com.hawel.ledger_service.entity.JournalSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface JournalSequenceRepository extends JpaRepository<JournalSequence, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<JournalSequence> findById(Long id);

}