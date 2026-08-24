package com.hawel.ledger_service.controller;

import com.hawel.common_service.dto.ledger.ResolveAccountsRequest;
import com.hawel.common_service.dto.ledger.ResolveAccountsResponse;
import com.hawel.ledger_service.service.LedgerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/ledger/accounts")
@RequiredArgsConstructor
public class LedgerAccountInternalController {

    private final LedgerService ledgerService;

    @PostMapping("/resolve")
    public ResponseEntity<ResolveAccountsResponse> resolveAccounts(@Valid @RequestBody ResolveAccountsRequest request) {

        return ResponseEntity.ok(ledgerService.resolveAccounts(request));
    }
}