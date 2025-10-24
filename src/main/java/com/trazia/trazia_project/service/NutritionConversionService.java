package com.trazia.trazia_project.service;

import com.trazia.trazia_project.dto.product.NutrimentsDTO;
import com.trazia.trazia_project.constants.ReferenceDailyIntakes;
import org.springframework.stereotype.Service;

@Service
public class NutritionConversionService {

    public NutrimentsDTO calculatePer100g(NutrimentsDTO totalNutrients, Double yieldWeightGrams) {
        if (totalNutrients == null || yieldWeightGrams == null || yieldWeightGrams == 0) {
            return new NutrimentsDTO();
        }
        
        NutrimentsDTO per100g = new NutrimentsDTO();
        double factor = 100.0 / yieldWeightGrams;
        
        // Convertir todos los valores a por 100g
        if (totalNutrients.getEnergyKcal() != null) 
            per100g.setEnergyKcal(totalNutrients.getEnergyKcal() * factor);
        if (totalNutrients.getProtein() != null) 
            per100g.setProtein(totalNutrients.getProtein() * factor);
        if (totalNutrients.getCarbohydrates() != null) 
            per100g.setCarbohydrates(totalNutrients.getCarbohydrates() * factor);
        if (totalNutrients.getFat() != null) 
            per100g.setFat(totalNutrients.getFat() * factor);
        if (totalNutrients.getFiber() != null) 
            per100g.setFiber(totalNutrients.getFiber() * factor);
        if (totalNutrients.getSugars() != null) 
            per100g.setSugars(totalNutrients.getSugars() * factor);
        if (totalNutrients.getSodium() != null) 
            per100g.setSodium(totalNutrients.getSodium() * factor);
        if (totalNutrients.getSaturatedFat() != null) 
            per100g.setSaturatedFat(totalNutrients.getSaturatedFat() * factor);
        
        return per100g;
    }

    public NutrimentsDTO normalizeNutrients(NutrimentsDTO nutrients, Double servingSize, Double quantityGrams) {
        if (nutrients == null) return new NutrimentsDTO();
        
        double factor;
        if (servingSize != null && servingSize > 0) {
            factor = quantityGrams / servingSize;
        } else {
            factor = quantityGrams / 100.0;
        }
        
        NutrimentsDTO normalized = new NutrimentsDTO();
        if (nutrients.getEnergyKcal() != null) 
            normalized.setEnergyKcal(nutrients.getEnergyKcal() * factor);
        if (nutrients.getProtein() != null) 
            normalized.setProtein(nutrients.getProtein() * factor);
        if (nutrients.getCarbohydrates() != null) 
            normalized.setCarbohydrates(nutrients.getCarbohydrates() * factor);
        if (nutrients.getFat() != null) 
            normalized.setFat(nutrients.getFat() * factor);
        if (nutrients.getFiber() != null) 
            normalized.setFiber(nutrients.getFiber() * factor);
        if (nutrients.getSugars() != null) 
            normalized.setSugars(nutrients.getSugars() * factor);
        if (nutrients.getSodium() != null) 
            normalized.setSodium(nutrients.getSodium() * factor);
        if (nutrients.getSaturatedFat() != null) 
            normalized.setSaturatedFat(nutrients.getSaturatedFat() * factor);
        
        return normalized;
    }

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
    
    private Double addNullable(Double a, Double b) {
        if (a == null && b == null) return null;
        return (a != null ? a : 0.0) + (b != null ? b : 0.0);
    }

    public NutrimentsDTO calculateDailyValue(NutrimentsDTO nutriments) {
        if (nutriments == null) {
            return null;
        }

        NutrimentsDTO dailyValue = new NutrimentsDTO();

        // Calcular el porcentaje del Valor Diario para cada nutriente basado en los valores de referencia
        if (nutriments.getEnergyKcal() != null) {
            // %VD de calorías basado en CALORIES de referencia
            dailyValue.setEnergyKcal((nutriments.getEnergyKcal() / ReferenceDailyIntakes.CALORIES) * 100);
        }
        if (nutriments.getProtein() != null) {
            // %VD de proteína basado en PROTEIN de referencia
            dailyValue.setProtein((nutriments.getProtein() / ReferenceDailyIntakes.PROTEIN) * 100);
        }
        if (nutriments.getCarbohydrates() != null) {
            // %VD de carbohidratos basado en CARBOHYDRATES de referencia
            dailyValue.setCarbohydrates((nutriments.getCarbohydrates() / ReferenceDailyIntakes.CARBOHYDRATES) * 100);
        }
        if (nutriments.getSugars() != null) {
            // %VD de azúcares basado en SUGARS de referencia
            dailyValue.setSugars((nutriments.getSugars() / ReferenceDailyIntakes.SUGARS) * 100);
        }
        if (nutriments.getFat() != null) {
            // %VD de grasa basado en FAT de referencia
            dailyValue.setFat((nutriments.getFat() / ReferenceDailyIntakes.FAT) * 100);
        }
        if (nutriments.getSaturatedFat() != null) {
            // %VD de grasa saturada basado en SATURATED_FAT de referencia
            dailyValue.setSaturatedFat((nutriments.getSaturatedFat() / ReferenceDailyIntakes.SATURATED_FAT) * 100);
        }
        if (nutriments.getFiber() != null) {
            // %VD de fibra basado en FIBER de referencia
            dailyValue.setFiber((nutriments.getFiber() / ReferenceDailyIntakes.FIBER) * 100);
        }
        if (nutriments.getSodium() != null) {
            // %VD de sodio basado en SODIUM de referencia
            dailyValue.setSodium((nutriments.getSodium() / ReferenceDailyIntakes.SODIUM) * 100);
        }

        return dailyValue;
    }
}
