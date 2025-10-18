package com.trazia.trazia_project.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.trazia.trazia_project.dto.product.NutrimentsDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para devolver información completa de una receta al cliente
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeResponse {

    /**
     * ID de la receta
     */
    private Long id;

    /**
     * Nombre de la receta
     */
    private String name;

    /**
     * Descripción de la receta
     */
    private String description;

    /**
     * Peso final del rendimiento (en gramos)
     */
    private Double yieldWeightGrams;

    /**
     * Lista de ingredientes con sus detalles
     */
    @Builder.Default
    private List<RecipeIngredientResponse> ingredients = new ArrayList<>();

    /**
     * Costo total de la receta (suma de todos los ingredientes)
     */
    private Double totalCost;

    /**
     * Costo por gramo de la receta (totalCost / yieldWeightGrams)
     */
    private Double costPerGram;

    /**
     * Costo por 100g de la receta
     */
    private Double costPer100g;

    /**
     * Peso total de los ingredientes usados (en gramos)
     */
    private Double totalIngredientsWeight;

    /**
     * Pérdida de rendimiento en porcentaje
     * (totalIngredientsWeight - yieldWeightGrams) / totalIngredientsWeight * 100
     */
    private Double yieldLossPercentage;

    /**
     * Fecha de creación de la receta
     */
    private LocalDateTime createdAt;

    /**
     * Fecha de última modificación
     */
    private LocalDateTime updatedAt;

    /**
     * ID del usuario propietario de la receta
     */
    private Long userId;

    private NutrimentsDTO calculatedNutrition;
}
