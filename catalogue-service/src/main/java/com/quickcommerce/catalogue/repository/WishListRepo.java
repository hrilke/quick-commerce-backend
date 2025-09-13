package com.quickcommerce.catalogue.repository;

import com.quickcommerce.catalogue.entity.Category;
import com.quickcommerce.catalogue.entity.WishListItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WishListRepo extends JpaRepository<WishListItem, UUID> {
}
