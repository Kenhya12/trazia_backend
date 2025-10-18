package com.trazia.trazia_project.dto.product;

import com.trazia.trazia_project.entity.LabelingRegion;
import com.trazia.trazia_project.entity.ProductCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear un nuevo producto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    /**
     * Nombre del producto (obligatorio)
     */
    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 150, message = "Product name must be between 3 and 150 characters")
    private String name;

    /**
     * Descripción del producto (opcional)
     */
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    /**
     * Marca del producto (opcional)
     */
    @Size(max = 100, message = "Brand name cannot exceed 100 characters")
    private String brand;

    /**
     * Categoría del producto (obligatorio)
     */
    @NotNull(message = "Category is required")
    private ProductCategory category;

    /**
     * Información nutricional (opcional pero recomendado)
     */
    @Valid
    private NutrimentsRequest nutriments;

    /**
     * Tamaño de porción en gramos (opcional, para mercado US)
     */
    @Min(value = 1, message = "Serving size must be at least 1 gram")
    @Max(value = 10000, message = "Serving size cannot exceed 10000 grams")
    private Integer servingSizeGrams;

    /**
     * Descripción de la porción (opcional)
     * Ejemplo: "1 cup (240ml)", "3 cookies (30g)"
     */
    @Size(max = 100, message = "Serving description cannot exceed 100 characters")
    private String servingDescription;

    /**
     * Región de etiquetado (opcional, por defecto EU)
     */
    private LabelingRegion labelingRegion;
}

