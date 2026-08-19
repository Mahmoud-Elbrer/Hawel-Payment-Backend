package com.hawel.ledger_service.controller;

import com.hawel.ledger_service.dto.response.BalanceResponse;
import com.hawel.ledger_service.service.BalanceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ledger/accounts")
public class BalanceController {

    private final BalanceQueryService balanceQueryService;

    @GetMapping("/{accountId}/balance")
    public BalanceResponse getBalance(@PathVariable UUID accountId) {

        return balanceQueryService.getBalance(accountId);

    }

}
