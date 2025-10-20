package com.trazia.trazia_project;

import com.trazia.trazia_project.entity.product.ProductNutriments;
import com.trazia.trazia_project.dto.product.NutrimentsRequest;
import java.math.BigDecimal;

public class ProductMapperTestUtils {

    // Método que devuelve un ProductNutriments de ejemplo para los tests
    public static ProductNutriments createSampleProductNutriments() {
        return ProductNutriments.builder()
                .calories(BigDecimal.valueOf(250))
                .protein(BigDecimal.valueOf(10))
                .carbohydrates(BigDecimal.valueOf(50))
                .sugars(BigDecimal.valueOf(5))
                .fat(BigDecimal.valueOf(8))
                .saturatedFat(BigDecimal.valueOf(2))
                .fiber(BigDecimal.valueOf(6))
                .sodium(BigDecimal.valueOf(0.5))
                .salt(BigDecimal.valueOf(1.2))
                .build();
    }

    // Método que devuelve un NutrimentsRequest de ejemplo para los tests
    public static NutrimentsRequest createSampleNutrimentsRequest() {
        return NutrimentsRequest.builder()
                .calories(BigDecimal.valueOf(250))
                .protein(BigDecimal.valueOf(10))
                .carbohydrates(BigDecimal.valueOf(50))
                .sugars(BigDecimal.valueOf(5))
                .fat(BigDecimal.valueOf(8))
                .saturatedFat(BigDecimal.valueOf(2))
                .fiber(BigDecimal.valueOf(6))
                .sodium(BigDecimal.valueOf(0.5))
                .salt(BigDecimal.valueOf(1.2))
                .build();
    }
}