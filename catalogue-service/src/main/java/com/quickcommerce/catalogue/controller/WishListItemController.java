package com.quickcommerce.catalogue.controller;

import com.quickcommerce.catalogue.dto.WishListItemRequest;
import com.quickcommerce.catalogue.dto.WishListItemResponse;
import com.quickcommerce.shared.dto.PageResponse;
import com.quickcommerce.catalogue.service.WishListItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wishlist-items")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "Wishlist item operations")
public class WishListItemController {

    private final WishListItemService wishListItemService;

    @PostMapping
    @Operation(summary = "Add item to wishlist")
    public ResponseEntity<WishListItemResponse> add(@Valid @RequestBody WishListItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wishListItemService.add(request));
    }

    @DeleteMapping("/{userId}/products/{productId}")
    @Operation(summary = "Remove product from wishlist")
    public ResponseEntity<Void> remove(@PathVariable UUID userId, @PathVariable UUID productId) {
        wishListItemService.remove(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Clear wishlist for user")
    public ResponseEntity<Void> clear(@PathVariable UUID userId) {
        wishListItemService.clear(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    @Operation(summary = "List wishlist items for user")
    public PageResponse<WishListItemResponse> list(@PathVariable UUID userId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20") int size,
                                                   @RequestParam(required = false) String sort) {
        return wishListItemService.list(userId, page, size, sort);
    }
}
