package com.quickcommerse.payment.dto;

import com.quickcommerse.payment.model.PaymentStatus;
import lombok.Data;

import java.util.Map;
import java.util.UUID;
@Data
public class PaymentProcessResponse {
    private UUID paymentId;
    private String gateway;
    private String gatewayPaymentId;
    private PaymentStatus status;
    private String message;
    private Map<String, Object> clientPayload;
}
