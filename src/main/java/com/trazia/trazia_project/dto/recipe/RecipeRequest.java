package com.trazia.trazia_project.dto.recipe;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeRequest {
    @NotBlank
    private String name;

    private String description;

    @NotNull
    @Min(1)
    private Double yieldWeightGrams;

    @NotNull
    private List<RecipeIngredientRequest> ingredients;
}
