package com.quickcommerce.catalogue.controller;

import com.quickcommerce.catalogue.dto.CartItemRequest;
import com.quickcommerce.catalogue.dto.CartItemResponse;
import com.quickcommerce.shared.dto.PageResponse;
import com.quickcommerce.catalogue.service.CartItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart-items")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Cart item operations")
public class CartItemController {

    private final CartItemService cartItemService;

    @PostMapping
    @Operation(summary = "Add item to cart")
    public ResponseEntity<CartItemResponse> add(@Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemService.add(request));
    }

    @PutMapping("/{userId}/products/{productId}")
    @Operation(summary = "Update quantity for a product in cart")
    public CartItemResponse updateQuantity(@PathVariable UUID userId,
                                           @PathVariable UUID productId,
                                           @RequestParam int quantity) {
        return cartItemService.updateQuantity(userId, productId, quantity);
    }

    @DeleteMapping("/{userId}/products/{productId}")
    @Operation(summary = "Remove a product from cart")
    public ResponseEntity<Void> remove(@PathVariable UUID userId, @PathVariable UUID productId) {
        cartItemService.remove(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Clear cart for user")
    public ResponseEntity<Void> clear(@PathVariable UUID userId) {
        cartItemService.clear(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    @Operation(summary = "List cart items for user")
    public PageResponse<CartItemResponse> list(@PathVariable UUID userId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size,
                                               @RequestParam(required = false) String sort) {
        return cartItemService.list(userId, page, size, sort);
    }
}
