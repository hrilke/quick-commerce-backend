package com.quickcommerce.orders.service.impl;

import com.quickcommerce.orders.dto.OrderItemResponse;
import com.quickcommerce.orders.model.OrderItem;
import com.quickcommerce.orders.repository.OrderItemRepository;
import com.quickcommerce.orders.service.OrderItemService;
import com.quickcommerce.shared.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    @Override
    public PageResponse<OrderItemResponse> getOrderItemsByUser(UUID userId, int page, int size, String sort) {
        Sort sortSpec = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortSpec);
        Page<OrderItem> result = orderItemRepository.findByOrder_UserId(userId, pageable);
        List<OrderItemResponse> content = result.getContent().stream().map(this::map).collect(Collectors.toList());
        return PageResponse.<OrderItemResponse>builder()
                .content(content)
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .page(result.getNumber())
                .size(result.getSize())
                .first(result.isFirst())
                .last(result.isLast())
                .build();
    }

    private OrderItemResponse map(OrderItem item) {
        OrderItemResponse dto = new OrderItemResponse();
        dto.setId(item.getId());
        dto.setOrderId(item.getOrder().getId());
        dto.setProductId(item.getProductId());
        dto.setQuantity(item.getQuantity());
        dto.setMrp(item.getMrp());
        dto.setDiscountPrice(item.getDiscountPrice());
        dto.setSku(item.getSku());
        dto.setCreatedAt(item.getCreatedAt());
        return dto;
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        Sort combined = Sort.unsorted();
        for (String token : sort.split(",")) {
            String[] parts = token.trim().split(":");
            String field = parts[0].trim();
            Sort.Direction dir = (parts.length > 1 && parts[1].equalsIgnoreCase("asc")) ? Sort.Direction.ASC : Sort.Direction.DESC;
            Sort s = Sort.by(dir, field);
            combined = combined.and(s);
        }
        return combined;
    }
}
