package com.hawel.transaction_service.client.wallet;

import com.hawel.common_service.dto.ledger.JournalResponse;
import com.hawel.common_service.dto.ledger.ResolveAccountsRequest;
import com.hawel.common_service.dto.ledger.ResolveAccountsResponse;
import com.hawel.common_service.dto.ledger.TransferJournalRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//@FeignClient(name = "ledger-service")
@FeignClient(name = "ledger-service", url = "${services.ledger.url}")
public interface LedgerClient {

    @PostMapping("/api/v1/ledger/transfer")
    JournalResponse transfer(@Valid @RequestBody TransferJournalRequest request);


    // this call to get account id for sender and receiver
    @PostMapping("/api/internal/ledger/accounts/resolve")
    ResolveAccountsResponse resolveAccounts(@Valid @RequestBody ResolveAccountsRequest request);

}