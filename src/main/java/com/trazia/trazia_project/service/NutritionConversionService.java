package com.trazia.trazia_project.service;

import com.trazia.trazia_project.dto.product.NutrimentsDTO;
import org.springframework.stereotype.Service;

@Service
public class NutritionConversionService {

    private static final double GRAMS_TO_OUNCES = 0.03527396;
    private static final double OUNCES_TO_GRAMS = 28.3495;
    private static final double CALORIES_TO_JOULES = 4.184;

    /**
     * Convierte gramos a onzas
     */
    public double gramsToOunces(double grams) {
        return grams * GRAMS_TO_OUNCES;
    }

    /**
     * Convierte onzas a gramos
     */
    public double ouncesToGrams(double ounces) {
        return ounces * OUNCES_TO_GRAMS;
    }

    /**
     * Convierte calorías a julios
     */
    public double caloriesToJoules(double calories) {
        return calories * CALORIES_TO_JOULES;
    }

    /**
     * Convierte julios a calorías
     */
    public double joulesToCalories(double joules) {
        return joules / CALORIES_TO_JOULES;
    }

    /**
     * Normaliza valores nutricionales para cálculo ponderado:
     * Ajusta según tamaño porción US o base 100g EU, multiplicado por cantidad usada.
     */
    public NutrimentsDTO normalizeNutrients(
            NutrimentsDTO nutriments,
            Integer servingSizeGrams,
            double amountInGrams) {

        double factor;
        if (servingSizeGrams != null && servingSizeGrams > 0) {
            factor = amountInGrams / servingSizeGrams;
        } else {
            factor = amountInGrams / 100.0;
        }

        return NutrimentsDTO.builder()
                .calories(nutriments.getCalories() != null ? nutriments.getCalories() * factor : 0)
                .energyJoules(nutriments.getEnergyJoules() != null ? nutriments.getEnergyJoules() * factor : 0)
                .fat(nutriments.getFat() != null ? nutriments.getFat() * factor : 0)
                .saturatedFat(nutriments.getSaturatedFat() != null ? nutriments.getSaturatedFat() * factor : 0)
                .carbohydrates(nutriments.getCarbohydrates() != null ? nutriments.getCarbohydrates() * factor : 0)
                .sugars(nutriments.getSugars() != null ? nutriments.getSugars() * factor : 0)
                .fiber(nutriments.getFiber() != null ? nutriments.getFiber() * factor : 0)
                .protein(nutriments.getProtein() != null ? nutriments.getProtein() * factor : 0)
                .salt(nutriments.getSalt() != null ? nutriments.getSalt() * factor : 0)
                .build();
    }

    /**
     * Calcula valor nutricional por 100g basándose en total lote y peso final.
     */
    public NutrimentsDTO calculatePer100g(NutrimentsDTO totalNutrients, double yieldWeightGrams) {
        if (yieldWeightGrams == 0) {
            return NutrimentsDTO.builder().build();
        }
        double factor = 100.0 / yieldWeightGrams;
        return NutrimentsDTO.builder()
                .calories(totalNutrients.getCalories() * factor)
                .energyJoules(totalNutrients.getEnergyJoules() * factor)
                .fat(totalNutrients.getFat() * factor)
                .saturatedFat(totalNutrients.getSaturatedFat() * factor)
                .carbohydrates(totalNutrients.getCarbohydrates() * factor)
                .sugars(totalNutrients.getSugars() * factor)
                .fiber(totalNutrients.getFiber() * factor)
                .protein(totalNutrients.getProtein() * factor)
                .salt(totalNutrients.getSalt() * factor)
                .build();
    }

    /**
     * Suma dos objetos NutrimentsDTO.
     */
    public NutrimentsDTO sumNutrients(NutrimentsDTO a, NutrimentsDTO b) {
        if (a == null) return b;
        if (b == null) return a;

        return NutrimentsDTO.builder()
                .calories((a.getCalories() != null ? a.getCalories() : 0) + (b.getCalories() != null ? b.getCalories() : 0))
                .energyJoules((a.getEnergyJoules() != null ? a.getEnergyJoules() : 0) + (b.getEnergyJoules() != null ? b.getEnergyJoules() : 0))
                .fat((a.getFat() != null ? a.getFat() : 0) + (b.getFat() != null ? b.getFat() : 0))
                .saturatedFat((a.getSaturatedFat() != null ? a.getSaturatedFat() : 0) + (b.getSaturatedFat() != null ? b.getSaturatedFat() : 0))
                .carbohydrates((a.getCarbohydrates() != null ? a.getCarbohydrates() : 0) + (b.getCarbohydrates() != null ? b.getCarbohydrates() : 0))
                .sugars((a.getSugars() != null ? a.getSugars() : 0) + (b.getSugars() != null ? b.getSugars() : 0))
                .fiber((a.getFiber() != null ? a.getFiber() : 0) + (b.getFiber() != null ? b.getFiber() : 0))
                .protein((a.getProtein() != null ? a.getProtein() : 0) + (b.getProtein() != null ? b.getProtein() : 0))
                .salt((a.getSalt() != null ? a.getSalt() : 0) + (b.getSalt() != null ? b.getSalt() : 0))
                .build();
    }
}
