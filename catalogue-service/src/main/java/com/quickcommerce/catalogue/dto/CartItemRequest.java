package com.quickcommerce.catalogue.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CartItemRequest {
    @NotNull
    private UUID userId;
    @NotNull
    private UUID productId;
    @Min(1)
    private Integer quantity = 1;
}
