package com.trazia.trazia_project.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Información nutricional SIEMPRE almacenada por 100g (estándar EU)
 * Conversión a serving size US se hace en runtime
 * Cumple con EU Regulation 1169/2011 y FDA NLEA
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductNutriments {

    /**
     * Energía en kcal por 100g
     * FDA también requiere kJ: kJ = kcal × 4.184
     */
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 5, fraction = 2)
    private BigDecimal calories;

    /**
     * Proteínas en gramos por 100g
     * US: Protein (g), EU: Protein (g)
     */
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal protein;

    /**
     * Carbohidratos totales en gramos por 100g
     * US: Total Carbohydrate, EU: Carbohydrate
     */
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal carbohydrates;

    /**
     * Azúcares en gramos por 100g
     * US: Added Sugars (nuevo en 2020), EU: of which sugars
     */
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal sugars;

    /**
     * Grasas totales en gramos por 100g
     * US: Total Fat, EU: Fat
     */
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal fat;

    /**
     * Grasas saturadas en gramos por 100g
     * US: Saturated Fat, EU: of which saturates
     */
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal saturatedFat;

    /**
     * Fibra dietética en gramos por 100g
     * US: Dietary Fiber, EU: Fibre
     */
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal fiber;

    /**
     * Sodio en MILIGRAMOS por 100g
     * IMPORTANTE: EU usa sal (g), US usa sodio (mg)
     * Conversión: Sal (g) = Sodio (mg) × 2.5 / 1000
     */
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 5, fraction = 2)
    private BigDecimal sodium; // mg

    /**
     * Sal en GRAMOS por 100g (para etiquetas EU)
     * Auto-calculado desde sodio
     */
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal salt; // g

    // ====== MÉTODOS DE CONVERSIÓN ======

    /**
     * Calcula sal (g) desde sodio (mg)
     * Fórmula EU: Salt = Sodium × 2.5
     */
    public BigDecimal calculateSaltFromSodium() {
        if (sodium == null)
            return BigDecimal.ZERO;
        return sodium.multiply(new BigDecimal("2.5"))
                .divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula energía en kJ (requerido en EU)
     * 1 kcal = 4.184 kJ
     */
    public BigDecimal getCaloriesInKj() {
        if (calories == null)
            return BigDecimal.ZERO;
        return calories.multiply(new BigDecimal("4.184"))
                .setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * Convierte valores a serving size específico (para FDA)
     * 
     * @param servingSizeGrams tamaño de porción en gramos
     * @return nuevo objeto con valores escalados
     */
    public ProductNutriments convertToServingSize(int servingSizeGrams) {
        BigDecimal factor = new BigDecimal(servingSizeGrams)
                .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

        return ProductNutriments.builder()
                .calories(multiplyNullSafe(calories, factor))
                .protein(multiplyNullSafe(protein, factor))
                .carbohydrates(multiplyNullSafe(carbohydrates, factor))
                .sugars(multiplyNullSafe(sugars, factor))
                .fat(multiplyNullSafe(fat, factor))
                .saturatedFat(multiplyNullSafe(saturatedFat, factor))
                .fiber(multiplyNullSafe(fiber, factor))
                .sodium(multiplyNullSafe(sodium, factor))
                .salt(multiplyNullSafe(salt, factor))
                .build();
    }

    private BigDecimal multiplyNullSafe(BigDecimal value, BigDecimal factor) {
        if (value == null)
            return null;
        return value.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
}
