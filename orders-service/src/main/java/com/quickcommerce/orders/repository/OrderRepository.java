package com.quickcommerce.orders.repository;

import com.quickcommerce.orders.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
