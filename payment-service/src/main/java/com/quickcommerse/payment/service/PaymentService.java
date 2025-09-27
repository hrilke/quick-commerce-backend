package com.quickcommerse.payment.service;

import com.quickcommerse.payment.dto.PaymentProcessResponse;
import com.quickcommerse.payment.gateway.dto.PaymentRequest;
import java.util.UUID;

public interface PaymentService {
    PaymentProcessResponse createPayment(String gateway, PaymentRequest request);
    PaymentProcessResponse capture(String gateway, String paymentIdOrGatewayPaymentId, long amountCents);
    PaymentProcessResponse refund(String gateway, String paymentIdOrGatewayPaymentId, long amountCents);
    void handleWebhook(String gateway, String signature, String payload);
    PaymentProcessResponse getById(UUID paymentId);
}
