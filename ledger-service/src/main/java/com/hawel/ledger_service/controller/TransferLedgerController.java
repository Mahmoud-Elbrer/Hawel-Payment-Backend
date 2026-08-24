package com.hawel.ledger_service.controller;

import com.hawel.common_service.dto.ledger.JournalResponse;
import com.hawel.common_service.dto.ledger.TransferJournalRequest;
import com.hawel.ledger_service.service.LedgerService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/ledger")
@RequiredArgsConstructor
public class TransferLedgerController {


    private final LedgerService ledgerService;

    @PostMapping("/transfer")
    public ResponseEntity<JournalResponse> transfer(@Valid @RequestBody TransferJournalRequest request) {

        return ResponseEntity.ok(ledgerService.transfer(request));

    }

}