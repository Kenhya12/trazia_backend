package com.trazia.trazia_project.dto.recipe;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para crear o actualizar una receta
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeRequest {

    /**
     * Nombre de la receta
     */
    @NotBlank(message = "El nombre de la receta es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String name;

    /**
     * Descripción de la receta (instrucciones, notas, etc.)
     */
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String description;

    /**
     * Peso final del rendimiento de la receta (en gramos)
     * Ej: Si la receta produce 500g de producto final
     */
    @NotNull(message = "El peso de rendimiento es obligatorio")
    @Positive(message = "El peso de rendimiento debe ser mayor a 0")
    private Double yieldWeightGrams;

    /**
     * Lista de ingredientes que componen la receta
     */
    @Valid
    @NotNull(message = "La lista de ingredientes es obligatoria")
    @Size(min = 1, message = "La receta debe tener al menos un ingrediente")
    private List<RecipeIngredientRequest> ingredients = new ArrayList<>();
}

