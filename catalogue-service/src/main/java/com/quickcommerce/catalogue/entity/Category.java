package com.quickcommerce.catalogue.entity;

import com.quickcommerce.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories", schema = "catalogue")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"products"})
public class Category extends BaseEntity {

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "slug", unique = true, nullable = false)
    private String slug; // URL friendly name: "fresh-vegetables"

    @Column(name = "image_url")
    private String imageUrl;

    // Many-to-Many relationship with Product
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_categories",
            schema = "catalogue",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> products = new HashSet<>();
}
