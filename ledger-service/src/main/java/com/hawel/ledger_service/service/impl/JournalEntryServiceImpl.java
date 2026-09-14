package com.hawel.ledger_service.service.impl;

import com.hawel.common_service.dto.ledger.JournalResponse;
import com.hawel.ledger_service.entity.JournalEntry;
import com.hawel.ledger_service.mapper.JournalMapper;
import com.hawel.ledger_service.repository.JournalEntryRepository;
import com.hawel.ledger_service.service.JournalEntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Slf4j
@Service
public class JournalEntryServiceImpl implements JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;

    private final JournalMapper journalMapper;


    @Override
    public JournalResponse findByTransactionId(UUID transactionId) {

        log.info("Searching ledger journal by transactionId={}", transactionId);

        JournalEntry journal =
                journalEntryRepository.findByTransactionId(transactionId)
                        .orElseThrow(() -> {

                            log.warn("Ledger journal not found for transactionId={}", transactionId);

                            return new ResourceNotFoundException("Journal not found for transaction: " + transactionId);
                        });

        log.info(
                "Ledger journal found: transactionId={}, journalId={}, journalNumber={}, status={}",
                transactionId,
                journal.getId(),
                journal.getJournalNumber(),
                journal.getStatus()
        );


        return journalMapper.toResponse(journal);
    }
}
