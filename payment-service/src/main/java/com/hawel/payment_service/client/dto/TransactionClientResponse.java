package com.hawel.payment_service.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionClientResponse {

    private UUID id;

    private String referenceNumber;

    private String status;
}