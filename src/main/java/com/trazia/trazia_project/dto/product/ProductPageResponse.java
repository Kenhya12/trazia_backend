package com.trazia.trazia_project.dto.product;

import com.trazia.trazia_project.entity.LabelingRegion;
import com.trazia.trazia_project.entity.product.ProductCategory;
import com.trazia.trazia_project.entity.product.ProductNutriments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPageResponse {

    private List<ProductSummaryDTO> products;
    private PageMetadata pagination;
    private int page;
    private int totalPages;
    private long totalElements;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductSummaryDTO {
        private Long id;
        private String name;
        private String brand;
        private ProductCategory category; // Ahora funciona
        private String imageUrl;
        private Boolean hasCompleteNutrition;
        private NutritionSummary nutrition;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NutritionSummary {
        private Double calories;
        private Double protein;
        private Double carbohydrates;
        private Double fat;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PageMetadata {
        private int currentPage;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class ProductResponse {
        private Long id;
        private String name;
        private String description;
        private String brand;
        private ProductCategory category;

        // ← AÑADIR ESTOS CAMPOS SI NO EXISTEN
        private String imageUrl;
        private String thumbnailUrl;
        private String imagePath;
        private String thumbnailPath;

        private ProductNutriments nutriments;
        private Integer servingSizeGrams;
        private String servingDescription;
        private LabelingRegion labelingRegion;
        private Long userId;
        private String createdByUsername;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Boolean hasCompleteNutrition;
        private ProductNutriments nutrimentsPerServing;
    }

}
