package com.hawel.payment_service.service.impl;

import com.hawel.payment_service.client.CardClient;
import com.hawel.payment_service.client.TransactionClient;
import com.hawel.payment_service.client.dto.CardValidationResponse;
import com.hawel.payment_service.client.dto.TransactionClientResponse;
import com.hawel.payment_service.client.dto.TransferRequest;
import com.hawel.payment_service.dto.request.ConfirmPaymentRequest;
import com.hawel.payment_service.dto.request.CreateNfcPaymentRequest;
import com.hawel.payment_service.dto.request.CreatePaymentLinkRequest;
import com.hawel.payment_service.dto.request.CreateQrPaymentRequest;
import com.hawel.payment_service.dto.response.NfcPaymentResponse;
import com.hawel.payment_service.dto.response.PaymentLinkResponse;
import com.hawel.payment_service.dto.response.PaymentResponse;
import com.hawel.payment_service.dto.response.QrPaymentResponse;
import com.hawel.payment_service.entity.Payment;
import com.hawel.payment_service.enums.PaymentMethod;
import com.hawel.payment_service.enums.PaymentStatus;
import com.hawel.payment_service.exception.InvalidPaymentStateException;
import com.hawel.payment_service.exception.PaymentExpiredException;
import com.hawel.payment_service.exception.PaymentNotFoundException;
import com.hawel.payment_service.mapper.PaymentMapper;
import com.hawel.payment_service.repository.PaymentRepository;
import com.hawel.payment_service.service.PaymentService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    private final TransactionClient transactionClient;

    private final CardClient cardClient;

    private static final long QR_EXPIRATION_MINUTES = 15;

    @Override
    public QrPaymentResponse createQrPayment(CreateQrPaymentRequest request) {


        log.info(
                "Creating QR payment: receiverWalletId={}, amount={}, currencyCode={}",
                request.getReceiverWalletId(),
                request.getAmount(),
                request.getCurrencyCode()
        );

        String paymentReference = generatePaymentReference();

        Instant expiresAt = Instant.now().plus(QR_EXPIRATION_MINUTES, ChronoUnit.MINUTES);

        Payment payment = Payment.builder()
                .paymentReference(paymentReference)
                .receiverWalletId(request.getReceiverWalletId())
                .amount(request.getAmount())
                .currencyCode(request.getCurrencyCode())
                .paymentMethod(PaymentMethod.QR)
                .status(PaymentStatus.WAITING)
                .expiresAt(expiresAt)
                .description(request.getDescription())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        String qrData = "hawel://payment/" + savedPayment.getPaymentReference();

        log.info(
                "QR payment created successfully: paymentId={}, paymentReference={}, status={}",
                savedPayment.getId(),
                savedPayment.getPaymentReference(),
                savedPayment.getStatus()
        );

        return new QrPaymentResponse(paymentMapper.toResponse(savedPayment), qrData);
    }

    @Override
    public PaymentLinkResponse createPaymentLink(CreatePaymentLinkRequest request) {

        log.info("Creating payment link: receiverWalletId={}, amount={}, currencyCode={}", request.getReceiverWalletId(), request.getAmount(), request.getCurrencyCode());

        String paymentReference = generatePaymentReference();

        // Determine expiration time for the payment link.
        int expirationMinutes = request.getExpirationMinutes() != null
                ? request.getExpirationMinutes()
                : 30; // todo : make this configurable

        Instant expiresAt = Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES);

        Payment payment = Payment.builder()
                .paymentReference(paymentReference)
                .receiverWalletId(request.getReceiverWalletId())
                .amount(request.getAmount())
                .currencyCode(request.getCurrencyCode())
                .paymentMethod(PaymentMethod.PAYMENT_LINK)
                .status(PaymentStatus.WAITING) // must be WAITING for the payer to confirm the payment
                .description(request.getDescription())
                .expiresAt(expiresAt)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // todo: make the base URL configurable
        String paymentUrl = "http://localhost:3000/pay/" + savedPayment.getPaymentReference();

        log.info("Payment link created successfully: paymentId={}, paymentReference={}, expiresAt={}", savedPayment.getId(), savedPayment.getPaymentReference(), savedPayment.getExpiresAt());

        return new PaymentLinkResponse(
                paymentMapper.toResponse(savedPayment),
                paymentUrl
        );
    }

    // This When the payer scans the QR code or clicks the payment link,
    // the payer can confirm the payment by providing their wallet ID and an idempotency key.
    // The system will then process the payment and return the payment status.
    @Override
    public PaymentResponse confirmPayment(UUID paymentId, ConfirmPaymentRequest request) {

        log.info("Confirming payment: paymentId={}, payerWalletId={}", paymentId, request.getPayerWalletId());

        Payment payment = paymentRepository
                .findById(paymentId)
                .orElseThrow(() -> {

                    log.warn("Payment not found during confirmation: paymentId={}", paymentId);

                    return new PaymentNotFoundException("Payment not found: " + paymentId);
                });

        // Check if the payment method is either QR or PAYMENT_LINK. If not, throw an exception.
        if (payment.getPaymentMethod() != PaymentMethod.QR && payment.getPaymentMethod() != PaymentMethod.PAYMENT_LINK) {

            log.warn("Payment cannot be confirmed using this endpoint: paymentId={}, method={}", paymentId, payment.getPaymentMethod());

            throw new InvalidPaymentStateException("Payment cannot be confirmed using this endpoint");
        }

        // Payment must be waiting for confirmation
        if (payment.getStatus() != PaymentStatus.WAITING) {

            log.warn("Payment cannot be confirmed: paymentId={}, status={}", paymentId, payment.getStatus());

            throw new InvalidPaymentStateException("Payment cannot be confirmed in status: " + payment.getStatus());
        }

        // Check expiration
        if (payment.getExpiresAt() != null && payment.getExpiresAt().isBefore(Instant.now())) {

            log.info("Payment expired before confirmation: paymentId={}, reference={}", paymentId, payment.getPaymentReference());

            payment.setStatus(PaymentStatus.EXPIRED);

            paymentRepository.save(payment);

            throw new PaymentExpiredException("Payment has expired: " + payment.getPaymentReference());
        }


        // Prevent self payment
        // This check ensures that the payer's wallet ID is not the same as the receiver's wallet ID, which would indicate an attempt to make a payment to oneself.
        if (request.getPayerWalletId().equals(payment.getReceiverWalletId())) {

            log.warn("Self payment attempt detected: paymentId={}, walletId={}", paymentId, request.getPayerWalletId());

            throw new InvalidPaymentStateException("Payer wallet cannot be the same as receiver wallet");
        }

        // Set payer
        payment.setPayerWalletId(request.getPayerWalletId());

        // Mark as processing before calling Transaction Service
        payment.setStatus(PaymentStatus.PROCESSING);

        paymentRepository.save(payment);

        log.info("Payment marked as PROCESSING: paymentId={}, reference={}", paymentId, payment.getPaymentReference());

        TransferRequest transferRequest = new TransferRequest(
                request.getPayerWalletId(),
                payment.getReceiverWalletId(),
                payment.getAmount(),
                payment.getCurrencyCode()
        );

        log.info(
                "Calling Transaction Service: paymentId={}, senderWalletId={}, receiverWalletId={}, amount={}",
                paymentId,
                transferRequest.getSenderWalletId(),
                transferRequest.getReceiverWalletId(),
                transferRequest.getAmount()
        );

        TransactionClientResponse transactionResponse;

        // If the Transaction Service call fails, or Timeout occurs, we should not mark the payment as FAILED make it PROCESSING.
        try {

            log.info("Calling Transaction Service: paymentId={}, idempotencyKey={}, senderWalletId={}, receiverWalletId={}, amount={}", paymentId, request.getIdempotencyKey(), transferRequest.getSenderWalletId(), transferRequest.getReceiverWalletId(), transferRequest.getAmount());

            transactionResponse = transactionClient.transfer(request.getIdempotencyKey(), transferRequest);

            log.info("Transaction Service call successful: paymentId={}, transactionId={}, status={}", paymentId, transactionResponse.getId(), transactionResponse.getStatus());

        } catch (FeignException ex) {

            log.warn("Transaction Service call failed: paymentId={}, status={}, message={}", paymentId, ex.status(), ex.getMessage());

            // Do NOT mark payment as FAILED.
            // Transaction Service may have processed the transaction
            // but the response may have been lost.

            /*
             This FeignException can occur for various reasons, including:
                400 → Business validation error
                404 → Not found
                500 → Server error
                timeout → Unknown result
             */

            payment.setStatus(PaymentStatus.PROCESSING);

            paymentRepository.save(payment);

            return paymentMapper.toResponse(payment);
        }


        log.info("Transaction Service response received: paymentId={}, transactionId={}, status={}", paymentId, transactionResponse.getId(), transactionResponse.getStatus());

        payment.setTransactionId(transactionResponse.getId());

        if ("SUCCESS".equalsIgnoreCase(transactionResponse.getStatus())) {

            payment.setStatus(PaymentStatus.SUCCESS);

            log.info("Payment completed successfully: paymentId={}, transactionId={}", paymentId, transactionResponse.getId());

        } else if ("FAILED".equalsIgnoreCase(transactionResponse.getStatus())) {

            payment.setStatus(PaymentStatus.FAILED);

            log.warn("Payment failed: paymentId={}, transactionId={}", paymentId, transactionResponse.getId());

        } else {

            payment.setStatus(PaymentStatus.PROCESSING);

            log.info("Payment remains PROCESSING: paymentId={}, transactionId={}, transactionStatus={}", paymentId, transactionResponse.getId(), transactionResponse.getStatus());

        }

        Payment savedPayment = paymentRepository.save(payment);

        return paymentMapper.toResponse(savedPayment);
    }



    // when tap the NFC card, the system will validate the card and create a payment record,
    // then call the Transaction Service to execute the transfer.
    // The response will indicate whether the payment was successful, failed, or is still processing.
    @Override
    public NfcPaymentResponse createNfcPayment(CreateNfcPaymentRequest request, String idempotencyKey) {

        log.info("Creating NFC payment: nfcUid={}, receiverWalletId={}, amount={}", request.getNfcUid(), request.getReceiverWalletId(), request.getAmount());

        // 1. Validate NFC card and resolve wallet
        CardValidationResponse cardResponse = cardClient.validateByUid(request.getNfcUid());

        if (!cardResponse.isValid()) {

            log.warn("NFC card validation failed: nfcUid={}", request.getNfcUid());

            throw new InvalidPaymentStateException("NFC card is not valid");
        }

        if (!"ACTIVE".equalsIgnoreCase(cardResponse.getStatus())) {

            log.warn("NFC card is not active: cardId={}, status={}", cardResponse.getCardId(), cardResponse.getStatus());

            throw new InvalidPaymentStateException("NFC card is not active");
        }

        UUID payerWalletId = cardResponse.getWalletId();

        log.info("NFC card resolved successfully: cardId={}, cardNumber={}, walletId={}", cardResponse.getCardId(), cardResponse.getCardNumber(), payerWalletId);

        // 2. Prevent self payment
        if (payerWalletId.equals(request.getReceiverWalletId())) {

            log.warn("NFC self-payment attempt: walletId={}", payerWalletId);

            throw new InvalidPaymentStateException("Payer wallet cannot be the same as receiver wallet");
        }

        // 3. Create payment record
        String paymentReference = generatePaymentReference();

        Payment payment = Payment.builder()
                .paymentReference(paymentReference)
                .payerWalletId(payerWalletId)
                .receiverWalletId(request.getReceiverWalletId())
                .amount(request.getAmount())
                .currencyCode(request.getCurrencyCode())
                .paymentMethod(PaymentMethod.NFC)
                .status(PaymentStatus.PROCESSING)
                .description(request.getDescription())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        log.info("NFC payment created: paymentId={}, paymentReference={}", savedPayment.getId(), savedPayment.getPaymentReference());

        // 4. Prepare transaction
        TransferRequest transferRequest = new TransferRequest(
                payerWalletId,
                request.getReceiverWalletId(),
                request.getAmount(),
                request.getCurrencyCode()
        );

        log.info("Calling Transaction Service for NFC payment: paymentId={}, senderWalletId={}, receiverWalletId={}, amount={}", savedPayment.getId(), payerWalletId, request.getReceiverWalletId(), request.getAmount());

        // 5. Execute transaction
        TransactionClientResponse transactionResponse;

        try {

            transactionResponse = transactionClient.transfer(idempotencyKey, transferRequest);

        } catch (FeignException ex) {

            log.warn("Transaction Service unavailable during NFC payment: paymentId={}, status={}", savedPayment.getId(), ex.status());

            // We don't know whether Transaction Service executed the transfer
            savedPayment.setStatus(PaymentStatus.PROCESSING);

            Payment updatedPayment = paymentRepository.save(savedPayment);

            return new NfcPaymentResponse(
                    paymentMapper.toResponse(updatedPayment),
                    cardResponse.getCardId(),
                    cardResponse.getCardNumber()
            );
        }

        // 6. Process transaction result
        savedPayment.setTransactionId(transactionResponse.getId());

        if ("SUCCESS".equalsIgnoreCase(transactionResponse.getStatus())) {

            savedPayment.setStatus(PaymentStatus.SUCCESS);

            log.info("NFC payment completed successfully: paymentId={}, transactionId={}", savedPayment.getId(), transactionResponse.getId());

        } else if ("FAILED".equalsIgnoreCase(transactionResponse.getStatus())) {

            savedPayment.setStatus(PaymentStatus.FAILED);

            log.warn("NFC payment failed: paymentId={}, transactionId={}", savedPayment.getId(), transactionResponse.getId());

        } else {

            savedPayment.setStatus(PaymentStatus.PROCESSING);

            log.info("NFC payment remains PROCESSING: paymentId={}, transactionId={}, transactionStatus={}", savedPayment.getId(), transactionResponse.getId(), transactionResponse.getStatus());
        }

        Payment finalPayment = paymentRepository.save(savedPayment);

        return new NfcPaymentResponse(
                paymentMapper.toResponse(finalPayment),
                cardResponse.getCardId(),
                cardResponse.getCardNumber()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID paymentId) {

        log.debug("Fetching payment: paymentId={}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {

                    log.warn("Payment not found: paymentId={}", paymentId);

                    return new PaymentNotFoundException("Payment not found: " + paymentId);
                });

        log.debug("Payment found: paymentId={}, paymentReference={}, status={}", payment.getId(), payment.getPaymentReference(), payment.getStatus());

        return paymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByReference(String paymentReference) {

        log.debug("Fetching payment by reference: paymentReference={}", paymentReference);

        Payment payment = paymentRepository
                .findByPaymentReference(paymentReference)
                .orElseThrow(() -> {
                    log.warn(
                            "Payment not found: paymentReference={}",
                            paymentReference
                    );

                    return new PaymentNotFoundException("Payment not found: " + paymentReference);
                });


        if (payment.getStatus() == PaymentStatus.WAITING && payment.getExpiresAt() != null && payment.getExpiresAt().isBefore(Instant.now())) {

            log.info("Payment expired: paymentId={}, paymentReference={}", payment.getId(), payment.getPaymentReference());

            payment.setStatus(PaymentStatus.EXPIRED);

            paymentRepository.save(payment);

            throw new PaymentExpiredException("Payment has expired: " + payment.getPaymentReference());
        }

        log.debug("Payment found: paymentId={}, paymentReference={}, status={}", payment.getId(), payment.getPaymentReference(), payment.getStatus());

        return paymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse cancelPayment(UUID paymentId) {

        log.info("Cancelling payment: paymentId={}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    log.warn("Payment not found: paymentId={}", paymentId);
                    return new PaymentNotFoundException("Payment not found: " + paymentId);
                });

        if (payment.getStatus() != PaymentStatus.CREATED && payment.getStatus() != PaymentStatus.WAITING) {

            log.warn("Payment cannot be cancelled: paymentId={}, status={}", paymentId, payment.getStatus());

            throw new InvalidPaymentStateException("Payment cannot be cancelled in status: " + payment.getStatus());
        }

        payment.setStatus(PaymentStatus.CANCELLED);

        log.info("Payment cancelled successfully: paymentId={}, paymentReference={}", payment.getId(), payment.getPaymentReference());

        return paymentMapper.toResponse(payment);
    }


    private String generatePaymentReference() {
        return "PAY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
