package com.hawel.common_service.dto.ledger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResolveAccountsResponse {

    private List<AccountReferenceResponse> accounts;
}