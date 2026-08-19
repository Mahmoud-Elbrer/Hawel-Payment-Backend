package com.hawel.ledger_service.controller;


import com.hawel.ledger_service.dto.request.WithdrawalJournalRequest;
import com.hawel.ledger_service.dto.response.JournalResponse;
import com.hawel.ledger_service.service.LedgerService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/ledger")
@RequiredArgsConstructor
public class WithdrawalLedgerController {


    private final LedgerService ledgerService;


    @PostMapping("/withdraw")
    public ResponseEntity<JournalResponse> withdraw(@Valid @RequestBody WithdrawalJournalRequest request) {

        return ResponseEntity.ok(ledgerService.withdraw(request));

    }

}