package com.quickcommerce.catalogue.mapper;

import com.quickcommerce.catalogue.dto.WishListItemResponse;
import com.quickcommerce.catalogue.dto.ProductCard;
import com.quickcommerce.catalogue.entity.Product;
import com.quickcommerce.catalogue.entity.WishListItem;
import org.springframework.stereotype.Component;

@Component
public class WishListItemMapper {
    public WishListItemResponse toResponse(WishListItem c) {
        Product p = c.getProduct();
        ProductCard card = ProductCard.builder()
                .id(p.getId())
                .title(p.getTitle())
                .mrp(p.getMrp())
                .discountPrice(p.getDiscountPrice())
                .imageUrl(p.getImageUrl())
                .sku(p.getSku())
                .build();
        return WishListItemResponse.builder()
                .id(c.getId())
                .userId(c.getUserId())
                .product(card)
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
