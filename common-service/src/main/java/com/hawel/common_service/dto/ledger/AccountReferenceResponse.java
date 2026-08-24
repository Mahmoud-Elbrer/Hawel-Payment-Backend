package com.hawel.common_service.dto.ledger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountReferenceResponse {

    private UUID walletId;

    private UUID accountId;
}