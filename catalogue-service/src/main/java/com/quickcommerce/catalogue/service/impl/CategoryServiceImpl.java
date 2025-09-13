package com.quickcommerce.catalogue.service.impl;

import com.quickcommerce.catalogue.dto.CategoryRequest;
import com.quickcommerce.catalogue.dto.CategoryResponse;
import com.quickcommerce.shared.dto.PageResponse;
import com.quickcommerce.catalogue.entity.Category;
import com.quickcommerce.catalogue.repository.CategoryRepo;
import com.quickcommerce.catalogue.service.CategoryService;
import com.quickcommerce.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;

    @Override
    public CategoryResponse create(CategoryRequest request) {
        Category c = new Category();
        c.setTitle(request.getTitle());
        c.setDescription(request.getDescription());
        c.setSlug(request.getSlug());
        c.setImageUrl(request.getImageUrl());
        c = categoryRepo.save(c);
        return toResponse(c);
    }

    @Override
    public CategoryResponse update(UUID id, CategoryRequest request) {
        Category c = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        c.setTitle(request.getTitle());
        c.setDescription(request.getDescription());
        c.setSlug(request.getSlug());
        c.setImageUrl(request.getImageUrl());
        return toResponse(c);
    }

    @Override
    public void delete(UUID id) {
        if (!categoryRepo.existsById(id)) throw new ResourceNotFoundException("Category", "id", id);
        categoryRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse get(UUID id) {
        Category c = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return toResponse(c);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> list(int page, int size, String search, String sort) {
        size = Math.min(Math.max(size, 1), 100);
        page = Math.max(page, 0);
        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Category> result;
        if (search == null || search.isBlank()) {
            result = categoryRepo.findAll(pageable);
        } else {
            result = categoryRepo.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search, pageable);
        }
        List<CategoryResponse> content = result.getContent().stream().map(this::toResponse).toList();
        return PageResponse.<CategoryResponse>builder()
                .content(content)
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .page(page)
                .size(size)
                .first(result.isFirst())
                .last(result.isLast())
                .build();
    }

    private CategoryResponse toResponse(Category c) {
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

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) return Sort.by(Sort.Direction.DESC, "updatedAt");
        String[] parts = sort.split(",");
        List<Sort.Order> orders = new java.util.ArrayList<>();
        for (String part : parts) {
            String[] kv = part.split(":");
            String field = kv[0];
            Sort.Direction dir = (kv.length > 1 && kv[1].equalsIgnoreCase("asc")) ? Sort.Direction.ASC : Sort.Direction.DESC;
            orders.add(new Sort.Order(dir, field));
        }
        return Sort.by(orders);
    }
}
