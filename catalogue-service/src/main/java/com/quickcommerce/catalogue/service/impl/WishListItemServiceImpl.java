package com.quickcommerce.catalogue.service.impl;

import com.quickcommerce.catalogue.dto.WishListItemRequest;
import com.quickcommerce.catalogue.dto.WishListItemResponse;
import com.quickcommerce.catalogue.dto.ProductCard;
import com.quickcommerce.catalogue.mapper.WishListItemMapper;
import com.quickcommerce.shared.dto.PageResponse;
import com.quickcommerce.catalogue.entity.Product;
import com.quickcommerce.catalogue.entity.WishListItem;
import com.quickcommerce.catalogue.repository.ProductRepo;
import com.quickcommerce.catalogue.repository.WishListItemRepo;
import com.quickcommerce.catalogue.service.WishListItemService;
import com.quickcommerce.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WishListItemServiceImpl implements WishListItemService {

    private final WishListItemRepo wishListItemRepo;
    private final ProductRepo productRepo;
    private final WishListItemMapper mapper;

    @Override
    public WishListItemResponse add(WishListItemRequest request) {
        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));
    WishListItem item = wishListItemRepo.findByUserIdAndProduct_Id(request.getUserId(), request.getProductId())
        .orElse(null);
        if (item == null) {
            item = new WishListItem();
            item.setUserId(request.getUserId());
            item.setProduct(product);
            item = wishListItemRepo.save(item);
        }
        return mapper.toResponse(item);
    }

    @Override
    public void remove(UUID userId, UUID productId) {
        wishListItemRepo.deleteByUserIdAndProduct_Id(userId, productId);
    }

    @Override
    public void clear(UUID userId) {
        List<WishListItem> all = wishListItemRepo.findByUserId(userId);
        wishListItemRepo.deleteAllInBatch(all);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WishListItemResponse> list(UUID userId, int page, int size, String sort) {
        size = Math.min(Math.max(size, 1), 100);
        page = Math.max(page, 0);
        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<WishListItem> result = wishListItemRepo.findByUserId(userId, pageable);
        List<WishListItemResponse> content = result.getContent().stream().map(mapper::toResponse).toList();
        return PageResponse.<WishListItemResponse>builder()
                .content(content)
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .page(page)
                .size(size)
                .first(result.isFirst())
                .last(result.isLast())
                .build();
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) return Sort.by(Sort.Direction.DESC, "updatedAt");
        String[] parts = sort.split(",");
        java.util.List<Sort.Order> orders = new java.util.ArrayList<>();
        for (String part : parts) {
            String[] kv = part.split(":");
            String field = kv[0];
            Sort.Direction dir = (kv.length > 1 && kv[1].equalsIgnoreCase("asc")) ? Sort.Direction.ASC : Sort.Direction.DESC;
            orders.add(new Sort.Order(dir, field));
        }
        return Sort.by(orders);
    }
}
