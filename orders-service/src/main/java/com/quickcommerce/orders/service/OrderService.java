package com.quickcommerce.orders.service;

import com.quickcommerce.orders.dto.CartCheckoutRequest;
import com.quickcommerce.orders.dto.CheckoutResponse;
import com.quickcommerce.orders.dto.SingleCheckoutRequest;

public interface OrderService {
    public static final String DEFAULT_CURRENCY = "INR";
    CheckoutResponse checkoutSingle(SingleCheckoutRequest request);
    CheckoutResponse checkoutCart(CartCheckoutRequest request);
}
