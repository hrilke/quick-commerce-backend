package com.quickcommerce.catalogue.entity;

import com.quickcommerce.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products", schema = "catalogue")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"categories"})
public class Product extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "sku", unique = true, nullable = false)
    private String sku;

    @Column(name = "mrp", nullable = false, precision = 10, scale = 2)
    private BigDecimal mrp;

    @Column(name = "discount_price", precision = 10, scale = 2)
    private BigDecimal discountPrice;

    @Column(name = "unit", nullable = false)
    private String unit; // kg, gm, litre, ml, pieces, etc.

    @Column(name = "seller_name")
    private String sellerName;

    @Column(name = "seller_description")
    private String sellerDescription;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "min_order_quantity")
    private Integer minOrderQuantity = 1;

    @Column(name = "max_order_quantity")
    private Integer maxOrderQuantity = 10;

    // Many-to-Many relationship with Category
    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    private Set<Category> categories = new HashSet<>();
}

