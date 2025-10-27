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
    public NutrimentsDTO calculatePer100g(NutrimentsDTO totalNutrients, BigDecimal yieldWeightGrams) {
        if (totalNutrients == null || yieldWeightGrams == null || yieldWeightGrams.compareTo(BigDecimal.ZERO) == 0) {
            return new NutrimentsDTO();
        }

        BigDecimal factor = BigDecimal.valueOf(100).divide(yieldWeightGrams, 6, RoundingMode.HALF_UP);

        NutrimentsDTO per100g = new NutrimentsDTO();
        per100g.setEnergyKcal(safeMultiply(totalNutrients.getEnergyKcal(), factor));
        per100g.setProtein(safeMultiply(totalNutrients.getProtein(), factor));
        per100g.setCarbohydrates(safeMultiply(totalNutrients.getCarbohydrates(), factor));
        per100g.setFat(safeMultiply(totalNutrients.getFat(), factor));
        per100g.setFiber(safeMultiply(totalNutrients.getFiber(), factor));
        per100g.setSugars(safeMultiply(totalNutrients.getSugars(), factor));
        per100g.setSodium(safeMultiply(totalNutrients.getSodium(), factor));
        per100g.setSaturatedFat(safeMultiply(totalNutrients.getSaturatedFat(), factor));

        return per100g;
    }

    /**
     * Normaliza nutrientes según tamaño de porción y cantidad usada.
     */
    public NutrimentsDTO normalizeNutrients(NutrimentsDTO nutrients, BigDecimal servingSize, BigDecimal quantityGrams) {
        if (nutrients == null) return new NutrimentsDTO();

        BigDecimal divisor = (servingSize != null && servingSize.compareTo(BigDecimal.ZERO) > 0) 
                ? servingSize 
                : BigDecimal.valueOf(100);
        BigDecimal factor = quantityGrams.divide(divisor, 6, RoundingMode.HALF_UP);

        NutrimentsDTO normalized = new NutrimentsDTO();
        normalized.setEnergyKcal(safeMultiply(nutrients.getEnergyKcal(), factor));
        normalized.setProtein(safeMultiply(nutrients.getProtein(), factor));
        normalized.setCarbohydrates(safeMultiply(nutrients.getCarbohydrates(), factor));
        normalized.setFat(safeMultiply(nutrients.getFat(), factor));
        normalized.setFiber(safeMultiply(nutrients.getFiber(), factor));
        normalized.setSugars(safeMultiply(nutrients.getSugars(), factor));
        normalized.setSodium(safeMultiply(nutrients.getSodium(), factor));
        normalized.setSaturatedFat(safeMultiply(nutrients.getSaturatedFat(), factor));

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
        dailyValue.setEnergyKcal(percentageOfTotal(nutriments.getEnergyKcal(), ReferenceDailyIntakes.CALORIES));
        dailyValue.setProtein(percentageOfTotal(nutriments.getProtein(), ReferenceDailyIntakes.PROTEIN));
        dailyValue.setCarbohydrates(percentageOfTotal(nutriments.getCarbohydrates(), ReferenceDailyIntakes.CARBOHYDRATES));
        dailyValue.setSugars(percentageOfTotal(nutriments.getSugars(), ReferenceDailyIntakes.SUGARS));
        dailyValue.setFat(percentageOfTotal(nutriments.getFat(), ReferenceDailyIntakes.FAT));
        dailyValue.setSaturatedFat(percentageOfTotal(nutriments.getSaturatedFat(), ReferenceDailyIntakes.SATURATED_FAT));
        dailyValue.setFiber(percentageOfTotal(nutriments.getFiber(), ReferenceDailyIntakes.FIBER));
        dailyValue.setSodium(percentageOfTotal(nutriments.getSodium(), ReferenceDailyIntakes.SODIUM));

        return dailyValue;
    }

    // ---------------- Private helpers ----------------

    private Double addNullable(Double a, Double b) {
        if (a == null && b == null) return null;
        return (a != null ? a : 0.0) + (b != null ? b : 0.0);
    }

    private Double safeMultiply(Double value, BigDecimal factor) {
        if (value == null) return 0.0;
        return BigDecimal.valueOf(value).multiply(factor).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private Double percentageOfTotal(Double value, Double reference) {
        if (value == null || reference == null || reference == 0) return 0.0;
        return BigDecimal.valueOf(value)
                .divide(BigDecimal.valueOf(reference), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}