package com.trazia.trazia_project.dto.recipe;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeRequest {
    
    @NotBlank(message = "Recipe name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Yield weight is required")
    @Positive(message = "Yield weight must be positive")
    private BigDecimal yieldWeightGrams;
    
    private List<RecipeIngredientRequest> ingredients;
}