package com.hawel.ledger_service.service;

import com.hawel.ledger_service.dto.response.BalanceResponse;

import java.util.UUID;

public interface BalanceQueryService {

    BalanceResponse getBalance(UUID accountId);

}