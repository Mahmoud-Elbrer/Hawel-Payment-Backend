package com.hawel.transaction_service.controller;

import com.hawel.transaction_service.dto.request.TransferTransactionRequest;
import com.hawel.transaction_service.dto.response.TransactionResponse;
import com.hawel.transaction_service.service.TransactionService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@RequestHeader("Idempotency-Key") String idempotencyKey, @Valid @RequestBody TransferTransactionRequest request) {

        log.info("Received transfer request: {} with idempotency key: {}", request, idempotencyKey);

        TransactionResponse response = transactionService.transfer(request , idempotencyKey);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable UUID id) {

        return ResponseEntity.ok(transactionService.getTransaction(id));
    }

}