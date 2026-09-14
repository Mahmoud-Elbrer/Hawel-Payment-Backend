package com.hawel.ledger_service.controller;

import com.hawel.common_service.dto.ledger.JournalResponse;
import com.hawel.ledger_service.service.JournalEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;



@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/ledger")
public class JournalEntryServiceController {

    private final JournalEntryService journalEntryService ;

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<JournalResponse> getByTransactionId(@PathVariable UUID transactionId) {

        return ResponseEntity.ok(journalEntryService.findByTransactionId(transactionId));
    }
}
