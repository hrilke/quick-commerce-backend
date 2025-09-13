package com.quickcommerce.catalogue.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Value
@Builder
public class ProductResponse {
    UUID id;
    String title;
    String description;
    String sku;
    BigDecimal mrp;
    BigDecimal discountPrice;
    String unit;
    String sellerName;
    String sellerDescription;
    String imageUrl;
    String barcode;
    Integer minOrderQuantity;
    Integer maxOrderQuantity;
    Set<String> categories;
    Instant createdAt;
    Instant updatedAt;
}
