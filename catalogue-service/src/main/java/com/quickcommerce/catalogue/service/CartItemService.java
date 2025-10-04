package com.quickcommerce.catalogue.service;

import com.quickcommerce.catalogue.dto.CartItemRequest;
import com.quickcommerce.catalogue.dto.CartItemResponse;
import com.quickcommerce.shared.dto.CartItemCheckoutDto;
import com.quickcommerce.shared.dto.PageResponse;

import java.util.List;
import java.util.UUID;

public interface CartItemService {
    CartItemResponse add(CartItemRequest request);
    CartItemResponse updateQuantity(UUID userId, UUID productId, int quantity);
    void remove(UUID userId, UUID productId);
    void clear(UUID userId);
    PageResponse<CartItemResponse> list(UUID userId, int page, int size, String sort);
    List<CartItemCheckoutDto> getCartItemListForCheckout(UUID userId);
}
