package com.quickcommerce.orders.integration.catalogue.dto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@FeignClient(name = "catalogue-service")
public interface CatalogueClient {
    @DeleteMapping(path = "/api/v1/cart-items/empty-cart")
    void emptyCart(@RequestParam(value = "userId", required = false) UUID userId);

    @GetMapping(path = "/api/v1/cart-items/checkout")
    CartToCheckoutPayload checkout(@RequestParam("userId") UUID userId);
}
