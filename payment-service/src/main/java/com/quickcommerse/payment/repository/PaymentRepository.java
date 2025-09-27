package com.quickcommerse.payment.repository;

import com.quickcommerse.payment.model.Payment;
import com.quickcommerse.payment.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByGatewayPaymentId(String gatewayPaymentId);
}
