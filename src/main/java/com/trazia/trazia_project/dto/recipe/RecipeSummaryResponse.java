package com.trazia.trazia_project.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO simplificado para listar recetas (sin detalles de ingredientes)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeSummaryResponse {

    /**
     * ID de la receta
     */
    private Long id;

    /**
     * Nombre de la receta
     */
    private String name;

    /**
     * Descripción corta de la receta
     */
    private String description;

    /**
     * Peso del rendimiento (en gramos)
     */
    private Double yieldWeightGrams;

    /**
     * Costo total de la receta
     */
    private Double totalCost;

    /**
     * Costo por 100g
     */
    private Double costPer100g;

    /**
     * Número de ingredientes en la receta
     */
    private Integer ingredientCount;

    /**
     * Fecha de creación
     */
    private LocalDateTime createdAt;

    /**
     * Fecha de última modificación
     */
    private LocalDateTime updatedAt;
}
