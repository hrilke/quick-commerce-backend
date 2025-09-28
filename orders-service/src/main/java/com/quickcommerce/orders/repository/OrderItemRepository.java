package com.quickcommerce.orders.repository;

import com.quickcommerce.orders.model.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
	Page<OrderItem> findByOrder_UserId(UUID userId, Pageable pageable);
}
