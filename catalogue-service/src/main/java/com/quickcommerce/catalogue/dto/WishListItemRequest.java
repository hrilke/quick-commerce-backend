package com.quickcommerce.catalogue.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class WishListItemRequest {
    @NotNull
    private UUID userId;
    @NotNull
    private UUID productId;
}
