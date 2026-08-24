package com.hawel.ledger_service.controller;


import com.hawel.common_service.dto.ledger.JournalResponse;
import com.hawel.ledger_service.dto.request.DepositJournalRequest;
import com.hawel.ledger_service.service.LedgerService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/ledger")
@RequiredArgsConstructor
@Slf4j
public class DepositLedgerController {


    private final LedgerService ledgerService;


    @PostMapping("/deposit")
    public ResponseEntity<JournalResponse> deposit(@Valid @RequestBody DepositJournalRequest request) {

        log.info("Received deposit request: {}", request);

        return ResponseEntity.ok(ledgerService.deposit(request));

    }

}