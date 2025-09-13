package com.quickcommerce.catalogue.service.impl;

import com.quickcommerce.catalogue.dto.CartItemRequest;
import com.quickcommerce.catalogue.dto.CartItemResponse;
import com.quickcommerce.catalogue.dto.ProductCard;
import com.quickcommerce.shared.dto.PageResponse;
import com.quickcommerce.catalogue.entity.CartItem;
import com.quickcommerce.catalogue.entity.Product;
import com.quickcommerce.catalogue.repository.CartItemRepo;
import com.quickcommerce.catalogue.repository.ProductRepo;
import com.quickcommerce.catalogue.service.CartItemService;
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
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepo cartItemRepo;
    private final ProductRepo productRepo;

    @Override
    /**
     * Add (or stack) a product in the user's cart.
     *
     * CONTRACT:
     *   Use Cases : User adds items to cart from catalogue/product page.
     *   Invariants: Existing line -> quantity increments; new line -> created; quantity from request > 0 expected.
     *
     * RATIONALE:
     *   Consolidates idempotent add logic so controllers & clients don't replicate merge semantics.
     *
     * @param request cart item creation request
     * @return resulting cart line DTO
     */
    public CartItemResponse add(CartItemRequest request) {
        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));
        CartItem item = cartItemRepo.findByUserIdAndProduct_Id(request.getUserId(), request.getProductId())
                .orElse(null);
        if (item == null) {
            item = new CartItem();
            item.setUserId(request.getUserId());
            item.setProduct(product);
            item.setQuantity(request.getQuantity());
            item = cartItemRepo.save(item);
        } else {
            item.setQuantity(item.getQuantity() + request.getQuantity());
        }
        return toResponse(item);
    }

    @Override
    /**
     * Set an absolute quantity for a cart line.
     *
     * CONTRACT:
     *   Use Cases : User adjusts quantity in cart UI.
     *   Invariants: Minimum enforced at 1; 404 if (user, product) line absent.
     *
     * RATIONALE:
     *   Central place for future max quantity / stock validation.
     *
     * @param userId user id
     * @param productId product id
     * @param quantity desired quantity (<=0 coerced to 1)
     * @return updated cart line DTO
     */
    public CartItemResponse updateQuantity(UUID userId, UUID productId, int quantity) {
        if (quantity < 1) quantity = 1;
        CartItem item = cartItemRepo.findByUserIdAndProduct_Id(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId", productId));
        item.setQuantity(quantity);
        return toResponse(item);
    }

    @Override
    /**
     * Remove a single cart line.
     *
     * CONTRACT:
     *   Use Cases : User deletes product from cart.
     *   Invariants: Silent if already absent.
     *
     * RATIONALE:
     *   Keeps controller free of repository details & enforces consistent semantics.
     *
     * @param userId user id
     * @param productId product id
     */
    public void remove(UUID userId, UUID productId) {
        cartItemRepo.deleteByUserIdAndProduct_Id(userId, productId);
    }

    @Override
    /**
     * Clear entire cart for a user.
     *
     * CONTRACT:
     *   Use Cases : Checkout completion / manual clear.
     *   Invariants: Operation idempotent.
     *
     * RATIONALE:
     *   Batch deletion avoids per-row loop at repository layer.
     *
     * @param userId user id
     */
    public void clear(UUID userId) {
        List<CartItem> all = cartItemRepo.findByUserId(userId);
        cartItemRepo.deleteAllInBatch(all);
    }

    @Override
    @Transactional(readOnly = true)
    /**
     * List paginated cart items for a user.
     *
     * CONTRACT:
     *   Use Cases : Cart view rendering.
     *   Invariants: size bounded [1,100]; default sort updatedAt DESC.
     *
     * RATIONALE:
     *   Standardized pagination contract & DTO mapping.
     *
     * @param userId user id
     * @param page page index
     * @param size page size
     * @param sort sort directives
     * @return paged cart items
     */
    public PageResponse<CartItemResponse> list(UUID userId, int page, int size, String sort) {
        size = Math.min(Math.max(size, 1), 100);
        page = Math.max(page, 0);
        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<CartItem> result = cartItemRepo.findByUserId(userId, pageable);
        List<CartItemResponse> content = result.getContent().stream().map(this::toResponse).toList();
        return PageResponse.<CartItemResponse>builder()
                .content(content)
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .page(page)
                .size(size)
                .first(result.isFirst())
                .last(result.isLast())
                .build();
    }

    private CartItemResponse toResponse(CartItem c) {
    Product p = c.getProduct();
    ProductCard card = ProductCard.builder()
        .id(p.getId())
        .title(p.getTitle())
        .mrp(p.getMrp())
        .discountPrice(p.getDiscountPrice())
        .imageUrl(p.getImageUrl())
        .sku(p.getSku())
        .build();
    return CartItemResponse.builder()
        .id(c.getId())
        .userId(c.getUserId())
        .product(card)
        .quantity(c.getQuantity())
        .createdAt(c.getCreatedAt())
        .updatedAt(c.getUpdatedAt())
        .build();
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) return Sort.by(Sort.Direction.DESC, "updatedAt");
        String[] parts = sort.split(",");
        java.util.List<Sort.Order> orders = new java.util.ArrayList<>();
        for (String part : parts) {
            String[] kv = part.split(":");
            String field = kv[0];
            Sort.Direction dir = (kv.length > 1 && kv[1].equalsIgnoreCase("asc")) ? Sort.Direction.ASC : Sort.Direction.DESC;
            orders.add(new Sort.Order(dir, field));
        }
        return Sort.by(orders);
    }
}
