package com.trazia.trazia_project.dto.product;

import com.trazia.trazia_project.entity.product.ProductNutriments;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutrimentsRequest {
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 5, fraction = 2)
    private BigDecimal calories;

    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal protein;

    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal carbohydrates;

    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal sugars;

    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal fat;

    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal saturatedFat;

    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal fiber;

    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 5, fraction = 2)
    private BigDecimal sodium;

    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal salt;

    public ProductNutriments toEntity() {
        return ProductNutriments.builder()
                .calories(this.calories)
                .protein(this.protein)
                .carbohydrates(this.carbohydrates)
                .sugars(this.sugars)
                .fat(this.fat)
                .saturatedFat(this.saturatedFat)
                .fiber(this.fiber)
                .sodium(this.sodium)
                .salt(this.salt)
                .build();
    }
}
