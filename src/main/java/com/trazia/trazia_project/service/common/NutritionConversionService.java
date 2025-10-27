package com.trazia.trazia_project.service.common;

import com.trazia.trazia_project.constants.ReferenceDailyIntakes;
import com.trazia.trazia_project.model.NutrimentsDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class NutritionConversionService {

    /**
     * Calcula los nutrientes por 100g de receta.
     */
    public NutrimentsDTO calculatePer100g(NutrimentsDTO totalNutrients, Double yieldWeightGrams) {
        if (totalNutrients == null || yieldWeightGrams == null || yieldWeightGrams == 0) {
            return new NutrimentsDTO();
        }

        BigDecimal factor = BigDecimal.valueOf(100).divide(BigDecimal.valueOf(yieldWeightGrams), 6, RoundingMode.HALF_UP);

        NutrimentsDTO per100g = new NutrimentsDTO();
        per100g.setEnergyKcal(multiply(totalNutrients.getEnergyKcal(), factor).doubleValue());
        per100g.setProtein(multiply(totalNutrients.getProtein(), factor).doubleValue());
        per100g.setCarbohydrates(multiply(totalNutrients.getCarbohydrates(), factor).doubleValue());
        per100g.setFat(multiply(totalNutrients.getFat(), factor).doubleValue());
        per100g.setFiber(multiply(totalNutrients.getFiber(), factor).doubleValue());
        per100g.setSugars(multiply(totalNutrients.getSugars(), factor).doubleValue());
        per100g.setSodium(multiply(totalNutrients.getSodium(), factor).doubleValue());
        per100g.setSaturatedFat(multiply(totalNutrients.getSaturatedFat(), factor).doubleValue());

        return per100g;
    }

    /**
     * Normaliza nutrientes según tamaño de porción y cantidad usada.
     */
    public NutrimentsDTO normalizeNutrients(NutrimentsDTO nutrients, Double servingSize, Double quantityGrams) {
        if (nutrients == null) return new NutrimentsDTO();

        BigDecimal factor = BigDecimal.valueOf(quantityGrams);
        if (servingSize != null && servingSize > 0) {
            factor = factor.divide(BigDecimal.valueOf(servingSize), 6, RoundingMode.HALF_UP);
        } else {
            factor = factor.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        }

        NutrimentsDTO normalized = new NutrimentsDTO();
        normalized.setEnergyKcal(multiply(nutrients.getEnergyKcal(), factor).doubleValue());
        normalized.setProtein(multiply(nutrients.getProtein(), factor).doubleValue());
        normalized.setCarbohydrates(multiply(nutrients.getCarbohydrates(), factor).doubleValue());
        normalized.setFat(multiply(nutrients.getFat(), factor).doubleValue());
        normalized.setFiber(multiply(nutrients.getFiber(), factor).doubleValue());
        normalized.setSugars(multiply(nutrients.getSugars(), factor).doubleValue());
        normalized.setSodium(multiply(nutrients.getSodium(), factor).doubleValue());
        normalized.setSaturatedFat(multiply(nutrients.getSaturatedFat(), factor).doubleValue());

        return normalized;
    }

    /**
     * Suma dos objetos NutrimentsDTO manejando nulos.
     */
    public NutrimentsDTO sumNutrients(NutrimentsDTO total, NutrimentsDTO toAdd) {
        if (total == null) return toAdd;
        if (toAdd == null) return total;

        NutrimentsDTO result = new NutrimentsDTO();
        result.setEnergyKcal(addNullable(total.getEnergyKcal(), toAdd.getEnergyKcal()));
        result.setProtein(addNullable(total.getProtein(), toAdd.getProtein()));
        result.setCarbohydrates(addNullable(total.getCarbohydrates(), toAdd.getCarbohydrates()));
        result.setFat(addNullable(total.getFat(), toAdd.getFat()));
        result.setFiber(addNullable(total.getFiber(), toAdd.getFiber()));
        result.setSugars(addNullable(total.getSugars(), toAdd.getSugars()));
        result.setSodium(addNullable(total.getSodium(), toAdd.getSodium()));
        result.setSaturatedFat(addNullable(total.getSaturatedFat(), toAdd.getSaturatedFat()));

        return result;
    }

    /**
     * Calcula el porcentaje de Valor Diario (%VD) para cada nutriente.
     */
    public NutrimentsDTO calculateDailyValue(NutrimentsDTO nutriments) {
        if (nutriments == null) return new NutrimentsDTO();

        NutrimentsDTO dailyValue = new NutrimentsDTO();
        dailyValue.setEnergyKcal(percentageOfTotal(nutriments.getEnergyKcal(), ReferenceDailyIntakes.CALORIES).doubleValue());
        dailyValue.setProtein(percentageOfTotal(nutriments.getProtein(), ReferenceDailyIntakes.PROTEIN).doubleValue());
        dailyValue.setCarbohydrates(percentageOfTotal(nutriments.getCarbohydrates(), ReferenceDailyIntakes.CARBOHYDRATES).doubleValue());
        dailyValue.setSugars(percentageOfTotal(nutriments.getSugars(), ReferenceDailyIntakes.SUGARS).doubleValue());
        dailyValue.setFat(percentageOfTotal(nutriments.getFat(), ReferenceDailyIntakes.FAT).doubleValue());
        dailyValue.setSaturatedFat(percentageOfTotal(nutriments.getSaturatedFat(), ReferenceDailyIntakes.SATURATED_FAT).doubleValue());
        dailyValue.setFiber(percentageOfTotal(nutriments.getFiber(), ReferenceDailyIntakes.FIBER).doubleValue());
        dailyValue.setSodium(percentageOfTotal(nutriments.getSodium(), ReferenceDailyIntakes.SODIUM).doubleValue());

        return dailyValue;
    }

    // ---------------- Private helpers ----------------

    private Double addNullable(Double a, Double b) {
        if (a == null && b == null) return null;
        return (a != null ? a : 0.0) + (b != null ? b : 0.0);
    }

    private BigDecimal multiply(Double value, BigDecimal factor) {
        if (value == null) return BigDecimal.ZERO;
        return BigDecimal.valueOf(value).multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal percentageOfTotal(Double value, Double reference) {
        if (value == null || reference == null || reference == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(value).divide(BigDecimal.valueOf(reference), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }
}