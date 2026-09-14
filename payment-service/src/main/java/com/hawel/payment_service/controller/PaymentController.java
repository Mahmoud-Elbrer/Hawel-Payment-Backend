package com.hawel.payment_service.controller;

import com.hawel.payment_service.dto.request.ConfirmPaymentRequest;
import com.hawel.payment_service.dto.request.CreateNfcPaymentRequest;
import com.hawel.payment_service.dto.request.CreatePaymentLinkRequest;
import com.hawel.payment_service.dto.request.CreateQrPaymentRequest;
import com.hawel.payment_service.dto.response.NfcPaymentResponse;
import com.hawel.payment_service.dto.response.PaymentLinkResponse;
import com.hawel.payment_service.dto.response.PaymentResponse;
import com.hawel.payment_service.dto.response.QrPaymentResponse;
import com.hawel.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @PostMapping("/qr")
    public ResponseEntity<QrPaymentResponse> createQrPayment(@Valid @RequestBody CreateQrPaymentRequest request) {

        log.info("Create QR payment request received: receiverWalletId={}, amount={}", request.getReceiverWalletId(), request.getAmount());

        QrPaymentResponse response = paymentService.createQrPayment(request);

        log.info("Create QR payment request completed: paymentId={}, paymentReference={}", response.getPayment().getId(), response.getPayment().getPaymentReference());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable UUID paymentId) {

        log.debug("Get payment request received: paymentId={}", paymentId);

        return ResponseEntity.ok(paymentService.getPayment(paymentId));
    }

    @GetMapping("/reference/{paymentReference}")
    public ResponseEntity<PaymentResponse> getPaymentByReference(@PathVariable String paymentReference) {

        log.debug("Get payment by reference request received: paymentReference={}", paymentReference);

        return ResponseEntity.ok(paymentService.getPaymentByReference(paymentReference));
    }

    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable UUID paymentId) {

        log.info("Cancel payment request received: paymentId={}", paymentId);

        PaymentResponse response = paymentService.cancelPayment(paymentId);

        log.info("Cancel payment request completed: paymentId={}, status={}", response.getId(), response.getStatus());

        return ResponseEntity.ok(response);
    }


    // This When the payer scans the QR code or clicks the payment link,
    // the payer can confirm the payment by providing their wallet ID and an idempotency key.
    // The system will then process the payment and return the payment status.
    @PostMapping("/{paymentId}/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(@PathVariable UUID paymentId, @Valid @RequestBody ConfirmPaymentRequest request) {

        log.info("Confirm payment request received: paymentId={}, payerWalletId={}", paymentId, request.getPayerWalletId());

        PaymentResponse response = paymentService.confirmPayment(paymentId, request);

        log.info("Confirm payment request completed: paymentId={}, status={}", response.getId(), response.getStatus());

        return ResponseEntity.ok(response);
    }


    @PostMapping("/nfc")
    public ResponseEntity<NfcPaymentResponse> createNfcPayment(@RequestHeader("Idempotency-Key") String idempotencyKey, @Valid @RequestBody CreateNfcPaymentRequest request) {

        log.info("Create NFC payment request received: nfcUid={}, receiverWalletId={}, amount={}", request.getNfcUid(), request.getReceiverWalletId(), request.getAmount());

        NfcPaymentResponse response = paymentService.createNfcPayment(request, idempotencyKey);

        log.info("Create NFC payment request completed: paymentId={}, status={}", response.getPayment().getId(), response.getPayment().getStatus());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/link")
    public ResponseEntity<PaymentLinkResponse> createPaymentLink(@Valid @RequestBody CreatePaymentLinkRequest request) {

        log.info("Create payment link request received: receiverWalletId={}, amount={}", request.getReceiverWalletId(), request.getAmount());

        PaymentLinkResponse response = paymentService.createPaymentLink(request);

        log.info("Payment link created: paymentId={}, paymentReference={}", response.getPayment().getId(), response.getPayment().getPaymentReference());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    // when Pay Payment Link is called, it confirms the payment link and processes the payment.
    @PostMapping("/{paymentId}/pay")
    public ResponseEntity<PaymentResponse> payPaymentLink(@PathVariable UUID paymentId, @Valid @RequestBody ConfirmPaymentRequest request) {

        log.info("Pay payment link request received: paymentId={}, payerWalletId={}", paymentId, request.getPayerWalletId());

        PaymentResponse response = paymentService.confirmPayment(paymentId, request);

        log.info("Pay payment link completed: paymentId={}, status={}", paymentId, response.getStatus());

        return ResponseEntity.ok(response);
    }



}