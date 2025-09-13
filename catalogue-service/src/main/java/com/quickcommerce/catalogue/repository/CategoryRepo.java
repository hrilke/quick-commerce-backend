package com.quickcommerce.catalogue.repository;

import com.quickcommerce.catalogue.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CategoryRepo extends JpaRepository<Category, UUID> {
	List<Category> findBySlugIn(Set<String> slugs);
	Page<Category> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description, Pageable pageable);
}
