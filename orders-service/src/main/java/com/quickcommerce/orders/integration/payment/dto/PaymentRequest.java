package com.quickcommerce.orders.integration.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Map;
import java.util.UUID;
@Data
public class PaymentRequest {
    @NotNull
    private UUID orderId;
    @NotNull
    private UUID userId;
    @Positive
    private long amountCents;
    @NotBlank
    private String currency;
    private String description;
    private Map<String, Object> metadata;
}
