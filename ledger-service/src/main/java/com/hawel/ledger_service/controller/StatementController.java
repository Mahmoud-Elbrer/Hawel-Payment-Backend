package com.hawel.ledger_service.controller;

import com.hawel.ledger_service.dto.*;
import com.hawel.ledger_service.dto.response.StatementResponse;
import com.hawel.ledger_service.enums.EntryType;
import com.hawel.ledger_service.enums.JournalType;
import com.hawel.ledger_service.service.StatementQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ledger/accounts")
public class StatementController {

    private final StatementQueryService statementQueryService;

    @GetMapping("/{accountId}/statement")
    public StatementResponse getStatement(

            @PathVariable UUID accountId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant to,

            @RequestParam(required = false)
            EntryType entryType,

            @RequestParam(required = false)
            JournalType journalType,

            @RequestParam(required = false)
            String reference,

            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable

    ) {

        StatementFilter filter = new StatementFilter(accountId, from, to, entryType, journalType, reference);

        return statementQueryService.getStatement(filter, pageable);
    }
}