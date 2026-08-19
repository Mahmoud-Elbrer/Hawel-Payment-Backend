package com.hawel.ledger_service.controller;


import com.hawel.ledger_service.dto.request.WithdrawalJournalRequest;
import com.hawel.ledger_service.dto.response.JournalResponse;
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
public class WithdrawalLedgerController {


    private final LedgerService ledgerService;


    @PostMapping("/withdraw")
    public ResponseEntity<JournalResponse> withdraw(@Valid @RequestBody WithdrawalJournalRequest request) {

        return ResponseEntity.ok(ledgerService.withdraw(request));

    }

}