package com.hawel.payment_service.service;

import com.hawel.payment_service.dto.request.ConfirmPaymentRequest;
import com.hawel.payment_service.dto.request.CreateNfcPaymentRequest;
import com.hawel.payment_service.dto.request.CreatePaymentLinkRequest;
import com.hawel.payment_service.dto.request.CreateQrPaymentRequest;
import com.hawel.payment_service.dto.response.NfcPaymentResponse;
import com.hawel.payment_service.dto.response.PaymentLinkResponse;
import com.hawel.payment_service.dto.response.PaymentResponse;
import com.hawel.payment_service.dto.response.QrPaymentResponse;

import java.util.UUID;

public interface PaymentService {

    // 1 -method for creating QR payments
    QrPaymentResponse createQrPayment(CreateQrPaymentRequest request);

    // 1- method for creating payment links
    PaymentLinkResponse createPaymentLink(CreatePaymentLinkRequest request);

    // 3 - method for confirming payments
    PaymentResponse confirmPayment(UUID paymentId ,  ConfirmPaymentRequest request);

    // method for creating NFC payments using Card with Confirmation
    NfcPaymentResponse createNfcPayment(CreateNfcPaymentRequest request , String idempotencyKey );

    PaymentResponse getPayment(UUID paymentId);

    // 2- method for getting payment by reference
    PaymentResponse getPaymentByReference(String paymentReference);

    PaymentResponse cancelPayment(UUID paymentId);




}
