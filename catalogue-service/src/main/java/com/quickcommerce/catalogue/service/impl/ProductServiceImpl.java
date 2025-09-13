package com.quickcommerce.catalogue.service.impl;

import com.quickcommerce.shared.dto.PageResponse;
import com.quickcommerce.catalogue.dto.ProductRequest;
import com.quickcommerce.catalogue.dto.ProductResponse;
import com.quickcommerce.catalogue.entity.Category;
import com.quickcommerce.catalogue.entity.Product;
import com.quickcommerce.catalogue.mapper.ProductMapper;
import com.quickcommerce.catalogue.repository.CategoryRepo;
import com.quickcommerce.catalogue.repository.ProductRepo;
import com.quickcommerce.catalogue.service.ProductService;
import com.quickcommerce.catalogue.spec.ProductSpecifications;
import com.quickcommerce.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final ProductMapper mapper;

    @Override
    /**
     * Create a new product aggregate.
     *
     * CONTRACT:
     *   Use Cases : Backoffice / ingestion pipeline creating catalogue entries.
     *   Invariants: Category links synchronized both sides; request categories may be null/empty.
     *   Validation: Existence/uniqueness assumed handled at DB (e.g. SKU unique).
     *
     * RATIONALE:
     *   Centralizes mapping + relationship wiring so controllers remain thin and join table stays consistent.
     *
     * @param request incoming product definition
     * @return persisted product as DTO
     */
    public ProductResponse create(ProductRequest request) {
        Product entity = mapper.toEntity(request);
        attachCategories(entity, request.getCategorySlugs());
        entity = productRepo.save(entity);
        return mapper.toResponse(entity);
    }

    @Override
    /**
     * Update an existing product.
     *
     * CONTRACT:
     *   Use Cases : Admin edits to catalogue content.
     *   Invariants: Non-existent id -> 404; categories diff-applied (add/remove) atomically.
     *   Validation: Price/unit constraints could be added here later.
     *
     * RATIONALE:
     *   Keeps category association diff logic in one place preventing stale join rows.
     *
     * @param id product identifier
     * @param request new field values & category slugs
     * @return updated product DTO
     */
    public ProductResponse update(UUID id, ProductRequest request) {
        Product entity = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        mapper.updateEntity(entity, request);
        attachCategories(entity, request.getCategorySlugs());
        return mapper.toResponse(entity);
    }

    @Override
    /**
     * Delete a product by id.
     *
     * CONTRACT:
     *   Use Cases : Product retirement.
     *   Invariants: 404 if id absent; cascading of join rows handled by JPA relationship.
     *
     * RATIONALE:
     *   Explicit existence check yields clearer API semantics vs silent delete.
     *
     * @param id product id
     */
    public void delete(UUID id) {
        if (!productRepo.existsById(id)) throw new ResourceNotFoundException("Product", "id", id);
        productRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    /**
     * Fetch a single product.
     *
     * CONTRACT:
     *   Use Cases : Detail page / internal lookups.
     *   Invariants: 404 if not found.
     *
     * RATIONALE:
     *   Shields consumers from entity internals and guarantees consistent not-found handling.
     *
     * @param id product id
     * @return DTO representation
     */
    public ProductResponse get(UUID id) {
        Product p = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return mapper.toResponse(p);
    }

    @Override
    @Transactional(readOnly = true)
    /**
     * List products with optional filtering and sorting.
     *
     * CONTRACT:
     *   Use Cases : Catalogue browsing, search, merchandising.
     *   Invariants: size bounded to [1,100]; page coerced >= 0; default sort updatedAt DESC.
     *   Filters  : text (title/description), categories (slugs), price range.
     *
     * RATIONALE:
     *   Lazy builds the Specification only when filters exist for simpler SQL & better index use.
     *
     * @param page zero-based page index
     * @param size requested page size
     * @param search optional free text
     * @param categories optional category slugs
     * @param minPrice optional lower price
     * @param maxPrice optional upper price
     * @param sort comma separated sort directives field[:asc|desc]
     * @return paged DTOs
     */
    public PageResponse<ProductResponse> list(int page, int size, String search, Set<String> categories,
                                              BigDecimal minPrice, BigDecimal maxPrice, String sort) {
        size = Math.min(Math.max(size, 1), 100);
        page = Math.max(page, 0);
        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Specification<Product> spec = where(ProductSpecifications.titleOrDescriptionContains(search))
                .and(ProductSpecifications.inCategories(categories))
                .and(ProductSpecifications.priceBetween(minPrice, maxPrice));

        Page<Product> result = (spec == null) ? productRepo.findAll(pageable) : productRepo.findAll(spec, pageable);
        var content = result.getContent().stream().map(mapper::toResponse).toList();

        return PageResponse.<ProductResponse>builder()
                .content(content)
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .page(page)
                .size(size)
                .first(result.isFirst())
                .last(result.isLast())
                .build();
    }

    private void attachCategories(Product entity, Set<String> categorySlugs) {
        if (categorySlugs == null || categorySlugs.isEmpty()) {
            if (entity.getCategories() != null) {
                for (Category cat : new HashSet<>(entity.getCategories())) {
                    cat.getProducts().remove(entity);
                }
                entity.getCategories().clear();
            }
            return;
        }

        Set<String> distinctSlugs = categorySlugs.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        if (distinctSlugs.isEmpty()) {
            return;
        }

        List<Category> found = categoryRepo.findBySlugIn(distinctSlugs);
        Map<String, Category> bySlug = found.stream().collect(Collectors.toMap(Category::getSlug, c -> c));

        Set<Category> toRemove = new HashSet<>();
        for (Category existing : entity.getCategories()) {
            if (!distinctSlugs.contains(existing.getSlug())) {
                toRemove.add(existing);
            }
        }
        for (Category rem : toRemove) {
            rem.getProducts().remove(entity);
            entity.getCategories().remove(rem);
        }


        for (String slug : distinctSlugs) {
            Category c = bySlug.get(slug);
            if (c != null && !entity.getCategories().contains(c)) {
                entity.getCategories().add(c);
                c.getProducts().add(entity);
            }
        }
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) return Sort.by(Sort.Direction.DESC, "updatedAt");
        String[] parts = sort.split(",");
        List<Sort.Order> orders = new ArrayList<>();
        for (String part : parts) {
            String[] kv = part.split(":");
            String field = kv[0];
            Sort.Direction dir = (kv.length > 1 && kv[1].equalsIgnoreCase("asc")) ? Sort.Direction.ASC : Sort.Direction.DESC;
            orders.add(new Sort.Order(dir, field));
        }
        return Sort.by(orders);
    }
}
