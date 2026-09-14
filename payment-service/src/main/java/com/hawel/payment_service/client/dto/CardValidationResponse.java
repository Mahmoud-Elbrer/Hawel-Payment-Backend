package com.hawel.payment_service.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardValidationResponse {

    private boolean valid;

    private UUID cardId;

    private UUID walletId;

    private String cardNumber;

    private String status;
}