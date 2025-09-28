package com.quickcommerce.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.UUID;
@Data
@AllArgsConstructor
public class CheckoutResponse {
    private UUID orderId;
    private UUID paymentId;
    private String gateway;
    private Map<String, Object> clientPayload;
}
