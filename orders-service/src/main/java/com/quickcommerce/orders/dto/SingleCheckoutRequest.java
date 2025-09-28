package com.quickcommerce.orders.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;
@Data
public class SingleCheckoutRequest {
    @NotNull
    private UUID userId;
    @NotNull
    private UUID productId;
    @NotNull
    private Integer quantity;
    @NotNull
    private BigDecimal mrp;
    @NotNull
    private BigDecimal discountPrice;
    @NotNull
    private String sku;
}
