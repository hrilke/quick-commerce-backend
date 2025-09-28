package com.quickcommerce.orders.integration.payment.dto;

import lombok.Data;

import java.util.Map;
import java.util.UUID;
@Data
public class PaymentProcessResponse {
    private UUID paymentId;
    private String gateway;
    private String gatewayPaymentId;
    private String status;
    private String message;
    private Map<String, Object> clientPayload;
}
