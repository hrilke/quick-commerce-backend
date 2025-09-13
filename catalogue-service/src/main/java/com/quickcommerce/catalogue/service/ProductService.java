package com.quickcommerce.catalogue.service;

import com.quickcommerce.catalogue.dto.*;
import com.quickcommerce.shared.dto.PageResponse;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public interface ProductService {
    ProductResponse create(ProductRequest request);
    ProductResponse update(UUID id, ProductRequest request);
    void delete(UUID id);
    ProductResponse get(UUID id);
    PageResponse<ProductResponse> list(int page, int size, String search, Set<String> categories, BigDecimal minPrice, BigDecimal maxPrice, String sort);
}
