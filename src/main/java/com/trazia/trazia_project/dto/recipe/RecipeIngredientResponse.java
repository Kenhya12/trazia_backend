package com.trazia.trazia_project.dto.recipe;

import com.trazia.trazia_project.dto.product.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para devolver información de un ingrediente de receta al cliente
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeIngredientResponse {

    /**
     * ID del ingrediente
     */
    private Long id;

    /**
     * Información del producto usado como ingrediente
     */
    private ProductDTO product;

    /**
     * Cantidad del producto usado (en gramos)
     */
    private Double quantityGrams;

    /**
     * Orden de visualización del ingrediente
     */
    private Integer displayOrder;

    /**
     * Costo total de este ingrediente (cantidad * costo por gramo del producto)
     */
    private Double ingredientCost;

    /**
     * Porcentaje que representa este ingrediente respecto al total de la receta
     */
    private Double percentageOfTotal;
}

