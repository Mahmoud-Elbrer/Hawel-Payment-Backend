package com.hawel.ledger_service.dto.response;

import com.hawel.ledger_service.enums.EntryType;
import com.hawel.ledger_service.enums.JournalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatementFilter {

    private UUID accountId;

    private Instant from;

    private Instant to;

    private EntryType entryType;

    private JournalType journalType;

    private String reference;
}