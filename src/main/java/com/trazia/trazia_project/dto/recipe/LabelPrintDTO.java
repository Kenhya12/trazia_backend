package com.trazia.trazia_project.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabelPrintDTO {

    // Información general del producto
    private String productName;
    private String brand;
    private String companyName;
    private String companyAddress;
    private String countryOfOrigin;

    // Información de trazabilidad
    private String batchNumber;
    private LocalDate expiryDate;
    private LocalDate productionDate;
    private String usageInstructions;
    
    // Ingredientes y alérgenos
    private String ingredients;
    private java.util.List<String> allergens;

    // Información nutricional por 100g
    private BigDecimal energyPer100g;
    private BigDecimal fatPer100g;
    private BigDecimal saturatedFatPer100g;
    private BigDecimal carbsPer100g;
    private BigDecimal sugarsPer100g;
    private BigDecimal proteinPer100g;
    private BigDecimal saltPer100g;
    private BigDecimal fiberPer100g;
    private BigDecimal sodiumPer100g;

    // Porcentaje del Valor Diario (%VD)
    private BigDecimal dvEnergy;
    private BigDecimal dvFat;
    private BigDecimal dvSaturatedFat;
    private BigDecimal dvCarbohydrates;
    private BigDecimal dvSugars;
    private BigDecimal dvProteins;
    private BigDecimal dvSalt;

    // Etiquetas de dieta / estilo de vida
    private boolean vegan;
    private boolean vegetarian;
    private boolean keto;
    private boolean paleo;
    private boolean wholeFoods;
    private boolean glutenFree;
    private boolean lactoseFree;
    private boolean organic;
    private boolean lowSugar;
    private boolean noAddedSugar;
    private boolean noAdditives;
    private boolean noPreservatives;
    private boolean otherLabels;

    // Extras
    private String barcode;
    private String qrCode;
    private String nutriScore;

    // Campos de receta
    private String recipeName;
    private String recipeDescription;
    private String ingredientsList;
    private BigDecimal yieldWeightGrams;
    private String legalDisclaimer;

}