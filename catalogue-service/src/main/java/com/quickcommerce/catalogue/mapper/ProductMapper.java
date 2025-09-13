package com.quickcommerce.catalogue.mapper;

import com.quickcommerce.catalogue.dto.ProductRequest;
import com.quickcommerce.catalogue.dto.ProductResponse;
import com.quickcommerce.catalogue.dto.ProductCard;
import com.quickcommerce.catalogue.entity.Product;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest req) {
        Product p = new Product();
        p.setTitle(req.getTitle());
        p.setDescription(req.getDescription());
        p.setSku(req.getSku());
        p.setMrp(req.getMrp());
        p.setDiscountPrice(req.getDiscountPrice());
        p.setUnit(req.getUnit());
        p.setSellerName(req.getSellerName());
        p.setSellerDescription(req.getSellerDescription());
        p.setImageUrl(req.getImageUrl());
        p.setBarcode(req.getBarcode());
        p.setMinOrderQuantity(req.getMinOrderQuantity());
        p.setMaxOrderQuantity(req.getMaxOrderQuantity());
        return p;
    }

    public void updateEntity(Product p, ProductRequest req) {
        p.setTitle(req.getTitle());
        p.setDescription(req.getDescription());
        p.setMrp(req.getMrp());
        p.setDiscountPrice(req.getDiscountPrice());
        p.setUnit(req.getUnit());
        p.setSellerName(req.getSellerName());
        p.setSellerDescription(req.getSellerDescription());
        p.setImageUrl(req.getImageUrl());
        p.setBarcode(req.getBarcode());
        p.setMinOrderQuantity(req.getMinOrderQuantity());
        p.setMaxOrderQuantity(req.getMaxOrderQuantity());
    }

    public ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .description(p.getDescription())
                .sku(p.getSku())
                .mrp(p.getMrp())
                .discountPrice(p.getDiscountPrice())
                .unit(p.getUnit())
                .sellerName(p.getSellerName())
                .sellerDescription(p.getSellerDescription())
                .imageUrl(p.getImageUrl())
                .barcode(p.getBarcode())
                .minOrderQuantity(p.getMinOrderQuantity())
                .maxOrderQuantity(p.getMaxOrderQuantity())
                .categories(p.getCategories() == null ? null : p.getCategories().stream().map(c -> c.getTitle()).collect(Collectors.toSet()))
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public ProductCard toCard(Product p) {
        if (p == null) return null;
        return ProductCard.builder()
                .id(p.getId())
                .title(p.getTitle())
                .mrp(p.getMrp())
                .discountPrice(p.getDiscountPrice())
                .imageUrl(p.getImageUrl())
                .sku(p.getSku())
                .unit(p.getUnit())
                .minOrderQuantity(p.getMinOrderQuantity())
                .maxOrderQuantity(p.getMaxOrderQuantity())
                .build();
    }
}
