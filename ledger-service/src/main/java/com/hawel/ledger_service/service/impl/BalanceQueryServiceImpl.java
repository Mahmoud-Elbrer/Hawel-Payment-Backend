package com.hawel.ledger_service.service.impl;

import com.hawel.ledger_service.dto.response.BalanceResponse;
import com.hawel.ledger_service.entity.Balance;
import com.hawel.ledger_service.exception.BalanceNotFoundException;
import com.hawel.ledger_service.mapper.BalanceMapper;
import com.hawel.ledger_service.repository.BalanceRepository;
import com.hawel.ledger_service.service.BalanceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BalanceQueryServiceImpl implements BalanceQueryService {

    private final BalanceRepository balanceRepository;

    private final BalanceMapper balanceMapper;


    @Override
    public BalanceResponse getBalance(UUID accountId) {

        Balance balance = balanceRepository.findByAccountId(accountId).orElseThrow(() -> new BalanceNotFoundException(accountId , "Balance not found for account id: " + accountId));

        return balanceMapper.toResponse(balance);
    }
}
