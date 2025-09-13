package com.quickcommerce.catalogue.service;

import com.quickcommerce.catalogue.dto.CategoryRequest;
import com.quickcommerce.catalogue.dto.CategoryResponse;
import com.quickcommerce.shared.dto.PageResponse;

import java.util.UUID;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(UUID id, CategoryRequest request);
    void delete(UUID id);
    CategoryResponse get(UUID id);
    PageResponse<CategoryResponse> list(int page, int size, String search, String sort);
}
