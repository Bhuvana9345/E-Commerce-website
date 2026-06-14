package com.stylegpt.store.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class ProductDtos {
    public record ProductRequest(
            @NotBlank String name,
            @NotBlank String description,
            @NotBlank String category,
            @NotBlank String brand,
            @NotBlank String color,
            @NotBlank String sizeOptions,
            @DecimalMin("0.0") BigDecimal price,
            @DecimalMin("0.0") BigDecimal discountPrice,
            @Min(0) int stock,
            String tags,
            String imageUrl,
            boolean active
    ) {}

    public record ProductResponse(
            Long id,
            String name,
            String description,
            String category,
            String brand,
            String color,
            String sizeOptions,
            BigDecimal price,
            BigDecimal discountPrice,
            int stock,
            String tags,
            String imageUrl,
            boolean active
    ) {}
}
