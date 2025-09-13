package com.quickcommerce.catalogue.mapper;

import com.quickcommerce.catalogue.dto.CategoryRequest;
import com.quickcommerce.catalogue.dto.CategoryResponse;
import com.quickcommerce.catalogue.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public Category toEntity(CategoryRequest r) {
        Category c = new Category();
        c.setTitle(r.getTitle());
        c.setDescription(r.getDescription());
        c.setSlug(r.getSlug());
        c.setImageUrl(r.getImageUrl());
        return c;
    }
    public void update(Category c, CategoryRequest r) {
        c.setTitle(r.getTitle());
        c.setDescription(r.getDescription());
        c.setSlug(r.getSlug());
        c.setImageUrl(r.getImageUrl());
    }
    public CategoryResponse toResponse(Category c) {
        return CategoryResponse.builder()
                .id(c.getId())
                .title(c.getTitle())
                .description(c.getDescription())
                .slug(c.getSlug())
                .imageUrl(c.getImageUrl())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
