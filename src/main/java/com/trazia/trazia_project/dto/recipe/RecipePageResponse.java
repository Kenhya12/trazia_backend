package com.trazia.trazia_project.dto.recipe;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipePageResponse {
    private List<RecipeSummaryResponse> recipes;
    private int currentPage;
    private int totalPages;
    private long totalRecipes;
    private int pageSize;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;
}