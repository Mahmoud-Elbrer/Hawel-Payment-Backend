package com.hawel.ledger_service.mapper;

import com.hawel.ledger_service.dto.response.BalanceResponse;
import com.hawel.ledger_service.entity.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BalanceMapper {

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "currency", source = "account.currency")
    BalanceResponse toResponse(Balance balance);

}