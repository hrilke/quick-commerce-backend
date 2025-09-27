package com.quickcommerse.payment.gateway;

import com.quickcommerse.payment.gateway.exception.GatewayException;
import com.quickcommerse.payment.gateway.dto.PaymentGatewayResponse;
import com.quickcommerse.payment.gateway.dto.PaymentRequest;

public interface PaymentGateway {
    PaymentGatewayResponse createPayment(PaymentRequest request) throws GatewayException;
    PaymentGatewayResponse capturePayment(String gatewayPaymentId, long amountCents) throws GatewayException;
    PaymentGatewayResponse refundPayment(String gatewayPaymentId, long amountCents) throws GatewayException;
    boolean verifyWebhookSignature(String payload, String signatureHeader);
}
