package com.hawel.ledger_service.service;

import com.hawel.common_service.dto.ledger.JournalResponse;

import java.util.Optional;
import java.util.UUID;

public interface JournalEntryService {
    JournalResponse findByTransactionId(UUID transactionId);

}
