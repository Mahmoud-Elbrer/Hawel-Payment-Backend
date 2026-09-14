package com.hawel.payment_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class NfcPaymentResponse {

    private PaymentResponse payment;

    private UUID cardId;

    private String cardNumber;
}