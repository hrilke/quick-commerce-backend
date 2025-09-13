package com.quickcommerce.catalogue.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class ProductRequest {
    @NotBlank
    private String title;
    private String description;
    @NotBlank
    private String sku;
    @NotNull @DecimalMin("0.0")
    private BigDecimal mrp;
    @DecimalMin("0.0")
    private BigDecimal discountPrice;
    @NotBlank
    private String unit;
    private String sellerName;
    private String sellerDescription;
    private String imageUrl;
    private String barcode;
    private Integer minOrderQuantity = 1;
    private Integer maxOrderQuantity = 10;
    private Set<String> categorySlugs;
}
