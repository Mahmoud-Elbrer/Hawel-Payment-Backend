package com.hawel.payment_service.client;

import com.hawel.payment_service.client.dto.TransactionClientResponse;
import com.hawel.payment_service.client.dto.TransferRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

//@FeignClient(name = "transaction-service", path = "/v1/transactions")
 @FeignClient(name = "transaction-service", url = "${services.transaction.url}")
public interface TransactionClient {

    @PostMapping("/api/v1/transactions/transfer")
    TransactionClientResponse transfer(@RequestHeader("Idempotency-Key") String idempotencyKey, @RequestBody TransferRequest request);
}