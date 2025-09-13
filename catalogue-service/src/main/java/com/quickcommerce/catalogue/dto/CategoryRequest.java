package com.quickcommerce.catalogue.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank
    private String title;
    private String description;
    @NotBlank
    private String slug;
    private String imageUrl;
}
