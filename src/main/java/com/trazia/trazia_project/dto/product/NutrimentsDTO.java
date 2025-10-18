package com.trazia.trazia_project.dto.product;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutrimentsDTO {
    private Double calories;
    private Double energyJoules;
    private Double fat;
    private Double saturatedFat;
    private Double carbohydrates;
    private Double sugars;
    private Double fiber;
    private Double protein;
    private Double salt;
}
