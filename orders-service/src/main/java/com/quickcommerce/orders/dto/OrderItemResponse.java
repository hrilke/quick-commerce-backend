package com.quickcommerce.orders.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class OrderItemResponse {
    private UUID id;
    private UUID orderId;
    private UUID productId;
    private Integer quantity;
    private BigDecimal mrp;
    private BigDecimal discountPrice;
    private String sku;
    private Instant createdAt;
}
