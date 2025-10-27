package com.trazia.trazia_project.dto.recipe;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietaryAnalysis {
    private Double calories;
    private Double fat;
    private Double protein;
    private Double carbs;
    private String dietLabels;
    private String healthScore;
}
