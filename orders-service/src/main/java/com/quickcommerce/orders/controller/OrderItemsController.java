package com.quickcommerce.orders.controller;

import com.quickcommerce.orders.dto.OrderItemResponse;
import com.quickcommerce.orders.service.OrderItemService;
import com.quickcommerce.shared.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/order-items", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OrderItemsController {

    private final OrderItemService orderItemService;

    @GetMapping(path = "/{userId}")
    public PageResponse<OrderItemResponse> getOrderItemListByUserId(
            @PathVariable UUID userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "createdAt:desc") String sort
    ) {
        return orderItemService.getOrderItemsByUser(userId, page, size, sort);
    }
}
