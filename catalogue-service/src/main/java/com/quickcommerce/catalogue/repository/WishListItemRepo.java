package com.quickcommerce.catalogue.repository;

import com.quickcommerce.catalogue.entity.WishListItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WishListItemRepo extends JpaRepository<WishListItem, UUID> {
    List<WishListItem> findByUserId(UUID userId);
    Page<WishListItem> findByUserId(UUID userId, Pageable pageable);
    Optional<WishListItem> findByUserIdAndProduct_Id(UUID userId, UUID productId);
    void deleteByUserIdAndProduct_Id(UUID userId, UUID productId);
}
