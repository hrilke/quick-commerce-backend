package com.quickcommerce.catalogue.repository;

import com.quickcommerce.catalogue.entity.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepo extends JpaRepository<CartItem, UUID> {
    List<CartItem> findByUserId(UUID userId);
    Page<CartItem> findByUserId(UUID userId, Pageable pageable);
    Optional<CartItem> findByUserIdAndProduct_Id(UUID userId, UUID productId);
    void deleteByUserIdAndProduct_Id(UUID userId, UUID productId);
}
