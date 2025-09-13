package com.quickcommerce.catalogue.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder
public class ProductCard {
    UUID id;
    String title;
    BigDecimal mrp;
    BigDecimal discountPrice;
    String imageUrl;
    String sku;
    String unit;
    Integer minOrderQuantity;
    Integer maxOrderQuantity;
}
