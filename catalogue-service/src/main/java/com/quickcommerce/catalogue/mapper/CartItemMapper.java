package com.quickcommerce.catalogue.mapper;

import com.quickcommerce.catalogue.dto.CartItemResponse;
import com.quickcommerce.catalogue.dto.ProductCard;
import com.quickcommerce.catalogue.entity.CartItem;
import com.quickcommerce.catalogue.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartItemMapper {
    private final ProductMapper productMapper;
    public CartItemResponse toResponse(CartItem c) {
        Product p = c.getProduct();
        ProductCard card = productMapper.toCard(p);
    return CartItemResponse.builder()
        .id(c.getId())
        .userId(c.getUserId())
        .product(card)
        .quantity(c.getQuantity())
        .createdAt(c.getCreatedAt())
        .updatedAt(c.getUpdatedAt())
        .build();
    }
}
