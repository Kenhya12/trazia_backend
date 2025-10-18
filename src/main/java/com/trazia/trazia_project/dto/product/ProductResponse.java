package com.trazia.trazia_project.dto.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trazia.trazia_project.entity.LabelingRegion;
import com.trazia.trazia_project.entity.product.ProductCategory;
import com.trazia.trazia_project.entity.product.ProductNutriments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respuesta con detalles completos del producto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    
    private Long id;
    private String name;
    private String description;
    private String brand;
    private ProductCategory category;
    
    // URLs de imágenes
    private String imageUrl;        // URL imagen completa
    private String thumbnailUrl;    // URL thumbnail
    private String imagePath;       // Path en filesystem
    private String thumbnailPath;   // Path thumbnail
    
    private ProductNutriments nutriments;
    private Integer servingSizeGrams;
    private String servingDescription;
    private LabelingRegion labelingRegion;

    // Información del creador
    private Long userId;
    private String createdByUsername;

    // Timestamps
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Campos calculados
    private Boolean hasCompleteNutrition;
    private ProductNutriments nutrimentsPerServing;
}
