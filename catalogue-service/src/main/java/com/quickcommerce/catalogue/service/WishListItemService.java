package com.quickcommerce.catalogue.service;

import com.quickcommerce.catalogue.dto.WishListItemRequest;
import com.quickcommerce.catalogue.dto.WishListItemResponse;
import com.quickcommerce.shared.dto.PageResponse;

import java.util.UUID;

public interface WishListItemService {
    WishListItemResponse add(WishListItemRequest request);
    void remove(UUID userId, UUID productId);
    void clear(UUID userId);
    PageResponse<WishListItemResponse> list(UUID userId, int page, int size, String sort);
}
