package com.quickcommerce.catalogue.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class CategoryResponse {
    private UUID id;
    private String title;
    private String description;
    private String slug;
    private String imageUrl;
    private Instant createdAt;
    private Instant updatedAt;
}
