package com.hawel.payment_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentLinkResponse {

    private PaymentResponse payment;

    private String paymentUrl;
}