package com.trazia.trazia_project.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabelPrintDTO {

    // === CAMPOS OBLIGATORIOS (Normativa UE) ===
    private String id;

    @NotBlank(message = "Product name is required")
    private String productName;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Company address is required")
    private String companyAddress;

    @NotBlank(message = "Country of origin is required")
    private String countryOfOrigin;

    @NotBlank(message = "Batch number is required")
    private String batchNumber;

    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;

    @NotBlank(message = "Ingredients are required")
    private String ingredients;

    // ✅ Conservar valores predeterminados al usar Builder
    @Builder.Default
    @NotNull(message = "Allergens list is required")
    private List<String> allergens = new ArrayList<>();

    // === CAMPOS DEL SISTEMA ===
    @Builder.Default
    @NotBlank(message = "Language is required")
    @Size(min = 2, max = 2, message = "Language must be 2 characters")
    private String language = "es";

    @Builder.Default
    @NotBlank(message = "Status is required")
    private String status = "draft";

    private Integer version;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    // === CAMPOS OPCIONALES/ADICIONALES ===
    private String recipeName;
    private String recipeDescription;
    private LocalDate productionDate;
    private String usageInstructions;
    private BigDecimal yieldWeightGrams;

    @Builder.Default
    private List<String> ingredientsList = new ArrayList<>();

    // === Información nutricional (opcional) ===
    private BigDecimal energyPer100g;
    private BigDecimal fatPer100g;
    private BigDecimal saturatedFatPer100g;
    private BigDecimal carbsPer100g;
    private BigDecimal sugarsPer100g;
    private BigDecimal proteinPer100g;
    private BigDecimal saltPer100g;
    private BigDecimal fiberPer100g;
    private BigDecimal sodiumPer100g;

    // === Costos (opcional) ===
    private BigDecimal totalCost;
    private BigDecimal costPer100g;

    // === Etiquetas de dieta (opcional) ===
    private Boolean vegan;
    private Boolean vegetarian;
    private Boolean glutenFree;
    private Boolean lactoseFree;
    private Boolean organic;

    // === Extras (opcional) ===
    private String barcode;
    private String qrCode;
    private String nutriScore;
    private String legalDisclaimer;
}