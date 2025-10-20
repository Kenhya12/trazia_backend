package com.trazia.trazia_project.dto.product;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductNutriments {
    private Double calories;
    private Double energyJoules;
    private Double fat;
    private Double saturatedFat;
    private Double carbohydrates;
    private Double sugars;
    private Double fiber;
    private Double protein;
    private Double salt;
    private Double sodium;
}
