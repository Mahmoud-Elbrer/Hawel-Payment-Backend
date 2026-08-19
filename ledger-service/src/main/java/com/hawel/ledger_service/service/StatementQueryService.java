package com.hawel.ledger_service.service;

import com.hawel.ledger_service.dto.StatementFilter;
import com.hawel.ledger_service.dto.response.StatementResponse;

import org.springframework.data.domain.Pageable;

public interface StatementQueryService {
    StatementResponse getStatement(StatementFilter filter, Pageable pageable);
}
