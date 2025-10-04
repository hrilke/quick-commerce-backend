package com.quickcommerce.orders.integration.catalogue.dto;

import com.quickcommerce.shared.dto.CartItemCheckoutDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "catalogue-service")
public interface CatalogueClient {
    @DeleteMapping(path = "/api/v1/cart-items/empty-cart/{userId}")
    void emptyCart(@PathVariable(value = "userId") UUID userId);

    @GetMapping(path = "/api/v1/cart-items/checkout/{userId}")
    List<CartItemCheckoutDto> getCartItemListByUserId(@PathVariable(value = "userId") UUID userId);
}
