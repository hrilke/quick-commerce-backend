package com.quickcommerce.catalogue.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class CartItemResponse {
    private UUID id;
    private UUID userId;
    private ProductCard product;
    private Integer quantity;
    private Instant createdAt;
    private Instant updatedAt;
}
