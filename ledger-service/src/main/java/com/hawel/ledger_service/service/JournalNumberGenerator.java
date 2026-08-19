package com.hawel.ledger_service.service;

import com.hawel.ledger_service.entity.JournalSequence;
import com.hawel.ledger_service.exception.LedgerException;
import com.hawel.ledger_service.repository.JournalSequenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JournalNumberGenerator {

    private static final Long SEQUENCE_ID = 1L;

    private final JournalSequenceRepository journalSequenceRepository;

    @Transactional
    // TODO : generate list of numbers in cache and use them to avoid DB hit for every journal number generation
    //  by using a cache mechanism like Redis or an in-memory cache. This will improve performance and reduce database load.
    // TODO : OR Use PostgreSQL sequences to generate journal numbers. This will allow the database to handle the sequence generation and ensure that the numbers are unique and sequential.
    public String generate() {

        log.info("Starting journal number generation. sequenceId={}", SEQUENCE_ID);

        log.info("Looking for journal sequence. sequenceId={}", SEQUENCE_ID);


        JournalSequence sequence = journalSequenceRepository.findById(SEQUENCE_ID).orElseThrow(() -> {

            log.error("Journal sequence not initialized. sequenceId={}", SEQUENCE_ID);

            return new LedgerException("LEDGER_004", "Journal sequence not initialized");
        });

        log.info("Journal sequence found. sequenceId={}, currentNextValue={}", SEQUENCE_ID, sequence.getNextValue());

        long value = sequence.getNextValue();

        long nextValue = value + 1;

        sequence.setNextValue(nextValue);

        log.info("Updating journal sequence. sequenceId={}, oldValue={}, newValue={}", SEQUENCE_ID, value, nextValue);

        journalSequenceRepository.save(sequence);

        String journalNumber = String.format("JRN%010d", value);

        log.info("Journal number generated successfully. journalNumber={}, sequenceValue={}", journalNumber, value);

        return journalNumber;
    }

}