package com.quickcommerce.orders.controller;

import com.quickcommerce.orders.dto.CartCheckoutRequest;
import com.quickcommerce.orders.dto.CheckoutResponse;
import com.quickcommerce.orders.dto.SingleCheckoutRequest;
import com.quickcommerce.orders.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/orders", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService orderService;

    @PostMapping(path = "/checkout/single", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CheckoutResponse checkoutSingle(@Valid @RequestBody SingleCheckoutRequest request) {
        return orderService.checkoutSingle(request);
    }

    @PostMapping(path = "/checkout/cart", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CheckoutResponse checkoutCart(@Valid @RequestBody CartCheckoutRequest request) {
        return orderService.checkoutCart(request);
    }
}
