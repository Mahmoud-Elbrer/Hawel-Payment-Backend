package com.hawel.payment_service.repository;

import com.hawel.payment_service.entity.Payment;
import com.hawel.payment_service.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByPaymentReference(String paymentReference);

    Optional<Payment> findByTransactionId(UUID transactionId);

    List<Payment> findByStatusAndExpiresAtBefore(PaymentStatus status, Instant expiresAt);
}