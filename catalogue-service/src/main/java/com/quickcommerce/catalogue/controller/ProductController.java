package com.quickcommerce.catalogue.controller;

import com.quickcommerce.shared.dto.PageResponse;
import com.quickcommerce.catalogue.dto.ProductRequest;
import com.quickcommerce.catalogue.dto.ProductResponse;
import com.quickcommerce.catalogue.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product CRUD and listing APIs")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Create product")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product")
    public ProductResponse update(@PathVariable UUID id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by id")
    public ProductResponse get(@PathVariable UUID id) {
        return productService.get(id);
    }

    @GetMapping
    @Operation(summary = "List products with search & filters")
    public PageResponse<ProductResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Set<String> categories,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false)
            @Parameter(description = "Comma separated sort instructions field:dir e.g. title:asc,updatedAt:desc") String sort
    ) {
        return productService.list(page, size, search, categories, minPrice, maxPrice, sort);
    }
}
