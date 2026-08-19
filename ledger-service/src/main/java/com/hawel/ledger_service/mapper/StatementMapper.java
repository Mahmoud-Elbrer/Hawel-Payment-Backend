package com.hawel.ledger_service.mapper;


import com.hawel.ledger_service.dto.response.StatementItemResponse;
import com.hawel.ledger_service.entity.LedgerEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StatementMapper {

    @Mapping(target = "journalNumber", source = "journal.journalNumber")
    @Mapping(target = "transactionId", source = "journal.transactionId")
    @Mapping(target = "reference", source = "journal.reference")
    @Mapping(target = "journalType", source = "journal.type")
    StatementItemResponse toResponse(LedgerEntry ledgerEntry);
}