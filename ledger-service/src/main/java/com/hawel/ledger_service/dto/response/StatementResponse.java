package com.hawel.ledger_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatementResponse {

    private UUID accountId;

    private List<StatementItemResponse> items;

    private long totalElements;

    private int totalPages;

    private int page;

    private int size;
}