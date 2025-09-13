package com.quickcommerce.catalogue.repository;

import com.quickcommerce.catalogue.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CartRepo extends JpaRepository<CartItem, UUID> {
}
