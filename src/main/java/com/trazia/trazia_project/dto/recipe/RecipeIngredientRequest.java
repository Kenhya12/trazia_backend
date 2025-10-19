package com.trazia.trazia_project.dto.recipe;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para recibir datos de un ingrediente de receta desde el cliente
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeIngredientRequest {

    /**
     * ID del producto (insumo) a usar en la receta
     */
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;

    /**
     * Cantidad del producto a usar (en gramos)
     */
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer quantityGrams;

    /**
     * Orden de visualización del ingrediente en la lista (opcional)
     * Si no se especifica, se agrega al final
     */
    private Integer displayOrder;
}
