package com.trazia.trazia_project.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
