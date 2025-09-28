package com.quickcommerce.orders.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;
@Data
public class CartCheckoutRequest {
    @NotNull
    private UUID userId;
}
