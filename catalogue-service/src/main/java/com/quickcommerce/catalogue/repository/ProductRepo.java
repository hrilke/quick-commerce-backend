package com.quickcommerce.catalogue.repository;

import com.quickcommerce.catalogue.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepo extends JpaRepository<Product, UUID> {
}
