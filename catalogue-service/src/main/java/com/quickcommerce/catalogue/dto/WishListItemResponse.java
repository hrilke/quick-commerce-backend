package com.quickcommerce.catalogue.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class WishListItemResponse {
    private UUID id;
    private UUID userId;
    private ProductCard product;
    private Instant createdAt;
    private Instant updatedAt;
}
