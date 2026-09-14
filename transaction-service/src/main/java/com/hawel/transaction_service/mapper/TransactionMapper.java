package com.hawel.transaction_service.mapper;

import com.hawel.transaction_service.dto.response.TransactionResponse;
import com.hawel.transaction_service.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionResponse toResponse(Transaction transaction);
}