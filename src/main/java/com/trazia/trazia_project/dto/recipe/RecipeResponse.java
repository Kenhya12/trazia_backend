package com.trazia.trazia_project.dto.recipe;

import com.trazia.trazia_project.entity.product.ProductNutriments;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal yieldWeightGrams;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RecipeIngredientResponse> ingredients;
    private ProductNutriments calculatedNutrition;
    private BigDecimal totalCost;
    private BigDecimal costPer100g;
}