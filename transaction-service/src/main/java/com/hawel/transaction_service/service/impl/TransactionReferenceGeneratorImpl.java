package com.hawel.transaction_service.service.impl;

import com.hawel.transaction_service.service.TransactionReferenceGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class TransactionReferenceGeneratorImpl implements TransactionReferenceGenerator {

    @Override
    public String generate() {

        String date = LocalDate.now().toString().replace("-", "");

        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        return "TXN-" + date + "-" + random;
    }
}
