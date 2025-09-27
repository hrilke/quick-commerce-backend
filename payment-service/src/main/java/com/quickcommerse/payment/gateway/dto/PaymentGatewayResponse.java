package com.quickcommerse.payment.gateway.dto;

import lombok.Data;

import java.util.Map;
@Data
public class PaymentGatewayResponse {
    private boolean success;
    private String gatewayPaymentId;
    private String status;
    private Map<String, Object> clientPayload;
    private String message;
}
