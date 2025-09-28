package com.quickcommerce.orders.service;

import com.quickcommerce.orders.dto.OrderItemResponse;
import com.quickcommerce.shared.dto.PageResponse;

import java.util.UUID;

public interface OrderItemService {
    PageResponse<OrderItemResponse> getOrderItemsByUser(UUID userId, int page, int size, String sort);
}
