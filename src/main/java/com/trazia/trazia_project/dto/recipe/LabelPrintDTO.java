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
    private String batchNumber;  // Número de lote del producto terminado
    private LocalDate productionDate;
    private LocalDate expiryDate;

    // Ingredientes y alérgenos
    private String ingredients;
    private String highlightedAllergens;

    // Información nutricional por 100g y por porción
    private BigDecimal energyPer100g;
    private BigDecimal energyPerServing;
    private BigDecimal fat;
    private BigDecimal saturatedFat;
    private BigDecimal carbohydrates;
    private BigDecimal sugars;
    private BigDecimal proteins;
    private BigDecimal salt;
    private BigDecimal fiber;

    // Porcentaje del Valor Diario (%VD)
    private BigDecimal dvEnergy;
    private BigDecimal dvFat;
    private BigDecimal dvSugars;
    private BigDecimal dvProteins;
    private BigDecimal dvSalt;

    // Etiquetas de dieta / estilo de vida
    private boolean vegan;            // Indica si el producto es apto para veganos
    private boolean vegetarian;       // Indica si el producto es apto para vegetarianos
    private boolean keto;              // Indica si el producto es apto para dieta keto
    private boolean paleo;             // Indica si el producto es apto para dieta paleo
    private boolean wholeFoods;         // Indica si el producto es apto para naturistas / whole foods
    private boolean glutenFree;             // Indica si el producto es sin gluten
    private boolean lactoseFree;            // Indica si el producto es libre de lactosa / dairy free
    private boolean organic;              // Indica si el producto es orgánico
    private boolean lowSugar;          // Indica si el producto es bajo en azúcar
    private boolean noAddedSugar;      // Indica si el producto no tiene azúcar añadida
    private boolean noAdditives;           // Indica si el producto no contiene aditivos
    private boolean noPreservatives;       // Indica si el producto no contiene conservantes
    private boolean otherLabels;             // Indica si el producto cumple con otras etiquetas dietéticas

    // Extras
    private String barcode;
    private String qrCode;
    private String nutriScore;
}