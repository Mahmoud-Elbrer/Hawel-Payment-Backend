package com.hawel.ledger_service.service.impl;

import com.hawel.ledger_service.dto.StatementFilter;
import com.hawel.ledger_service.dto.response.StatementResponse;
import com.hawel.ledger_service.entity.LedgerEntry;
import com.hawel.ledger_service.mapper.StatementMapper;
import com.hawel.ledger_service.repository.LedgerEntryRepository;
import com.hawel.ledger_service.service.StatementQueryService;
import com.hawel.ledger_service.specification.LedgerEntrySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatementQueryServiceImpl implements StatementQueryService {

    private final LedgerEntryRepository ledgerEntryRepository;

    private final StatementMapper statementMapper;

    @Override
    public StatementResponse getStatement(StatementFilter filter, Pageable pageable) {

        Page<LedgerEntry> page = ledgerEntryRepository.findAll(LedgerEntrySpecification.byFilter(filter), pageable);

        return new StatementResponse(
                filter.getAccountId(),
                page.getContent()
                        .stream()
                        .map(statementMapper::toResponse)
                        .collect(Collectors.toList()),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }


}