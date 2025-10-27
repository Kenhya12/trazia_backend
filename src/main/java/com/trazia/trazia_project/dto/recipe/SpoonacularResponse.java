package com.trazia.trazia_project.dto.recipe;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpoonacularResponse {
    private Double calories;
    private Double fat;
    private Double protein;
    private Double carbs;
    private String diets;
    private Double healthScore;
}