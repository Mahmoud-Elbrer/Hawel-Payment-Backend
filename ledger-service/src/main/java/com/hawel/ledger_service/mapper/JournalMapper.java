package com.hawel.ledger_service.mapper;

import com.hawel.ledger_service.dto.response.JournalResponse;
import com.hawel.ledger_service.entity.JournalEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JournalMapper {

    @Mapping(source = "id", target = "journalId")
    JournalResponse toResponse(JournalEntry journal);

}