package com.quickcommerce.shared.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@NotNull
public class CartItemCheckoutDto {

    private UUID productId;

    private Integer quantity;

    private BigDecimal mrp;

    private BigDecimal discountPrice;

    private String sku;
}
