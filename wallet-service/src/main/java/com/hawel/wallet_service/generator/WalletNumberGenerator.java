package com.hawel.wallet_service.generator;


import com.hawel.wallet_service.entity.WalletSequence;
import com.hawel.wallet_service.repository.WalletSequenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class WalletNumberGenerator {

    private static final String SEQUENCE_NAME = "WALLET";


    private final WalletSequenceRepository repository;



    @Transactional
    public String generate() {

        log.info("Generating new wallet number");

        WalletSequence sequence = repository.findForUpdate(SEQUENCE_NAME);

        log.info("Current wallet sequence value: {}", sequence != null ? sequence.getCurrentValue() : "null");

        if(sequence == null){

            log.error("Wallet sequence not initialized");

            throw new IllegalStateException("Wallet sequence not initialized");
        }


        Long nextNumber = sequence.getCurrentValue() + 1;

        log.info("Next wallet sequence value: {}", nextNumber);

        sequence.setCurrentValue(nextNumber);

        repository.save(sequence);

        log.info("Wallet sequence updated to: {}", nextNumber);

        return String.valueOf(nextNumber);

    }
}
