package com.quickcommerce.orders.integration.catalogue.dto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@FeignClient(name = "catalogue-service", url = "${services.catalogue-service.url:http://localhost:8081}")
public interface CatalogueClient {
    @DeleteMapping(path = "/api/v1/cart-items/empty-cart")
    void createPayment(@RequestParam(value = "userId", required = false) UUID userId);

    @GetMapping(path = "/api/v1/cart-items/checkout")
    CartToCheckoutPayload getPayment(@PathVariable("userId") UUID userId);
}
