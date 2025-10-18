package com.trazia.trazia_project.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para respuestas paginadas de recetas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipePageResponse {

    /**
     * Lista de recetas en la página actual
     */
    private List<RecipeSummaryResponse> recipes;

    /**
     * Número de la página actual (comienza en 0)
     */
    private int currentPage;

    /**
     * Número total de páginas
     */
    private int totalPages;

    /**
     * Número total de recetas
     */
    private long totalRecipes;

    /**
     * Tamaño de página (elementos por página)
     */
    private int pageSize;

    /**
     * Indica si es la primera página
     */
    private boolean first;

    /**
     * Indica si es la última página
     */
    private boolean last;

    /**
     * Indica si hay una página siguiente
     */
    private boolean hasNext;

    /**
     * Indica si hay una página anterior
     */
    private boolean hasPrevious;
}
