package com.hawel.common_service.dto.ledger;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResolveAccountsRequest {

    @NotEmpty(message = "Wallet IDs are required")
    private List<@NotNull(message = "Wallet ID cannot be null") UUID> walletIds;
}