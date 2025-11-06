package com.trazia.trazia_project.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabelPrintDTO {

    // Información básica del producto (REQUERIDOS)
    @NotBlank(message = "Product name is required")
    private String productName;

    @NotNull(message = "Version is required")
    private Integer version = 1;

    @NotBlank(message = "Status is required")
    private String status = "draft";

    @NotBlank(message = "Language is required")
    private String language = "es";

    // Ingredientes (REQUERIDOS)
    @NotNull(message = "Ingredients are required")
    @Size(min = 1, message = "At least one ingredient is required")
    private List<IngredientDTO> ingredients;

    @NotBlank(message = "Country of origin is required")
    private String countryOfOrigin;

    @NotBlank(message = "Batch number is required")
    private String batchNumber;

    // Información básica (OPCIONAL)
    private String netWeight;
    private LocalDate expirationDate;
    private String companyName;
    private String companyAddress;
    private String usageInstructions;
    private String legalDisclaimer;

    // INFORMACIÓN NUTRICIONAL ESENCIAL (OPCIONAL)
    private BigDecimal energyKcalPer100g; // Energía en kcal (más común)
    private BigDecimal energyKjPer100g; // Energía en kJ
    private BigDecimal fatPer100g; // Grasas totales
    private BigDecimal saturatedFatPer100g; // Grasas saturadas
    private BigDecimal carbsPer100g; // Hidratos de carbono
    private BigDecimal sugarsPer100g; // Azúcares
    private BigDecimal proteinPer100g; // Proteínas
    private BigDecimal saltPer100g; // Sal
    private BigDecimal fiberPer100g; // Fibra alimentaria
    private BigDecimal sodiumPer100g; // Sodio - NUEVO CAMPO

    // Campos adicionales para RecipeServiceImpl
    private String recipeName; // NUEVO: Para compatibilidad con RecipeServiceImpl
    private String recipeDescription; // NUEVO: Para compatibilidad con RecipeServiceImpl
    private BigDecimal yieldWeightGrams; // NUEVO: Para compatibilidad con RecipeServiceImpl
    private List<String> ingredientsList; // NUEVO: Para compatibilidad con RecipeServiceImpl
    private List<String> allergens; // NUEVO: Para compatibilidad con RecipeServiceImpl

    // Constructors
    public LabelPrintDTO() {
    }

    public LabelPrintDTO(String productName, String countryOfOrigin, String batchNumber) {
        this.productName = productName;
        this.countryOfOrigin = countryOfOrigin;
        this.batchNumber = batchNumber;
    }

    // Getters and Setters - Información básica (REQUERIDOS)
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<IngredientDTO> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientDTO> ingredients) {
        this.ingredients = ingredients;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    // Getters and Setters - Información básica (OPCIONAL)
    public String getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(String netWeight) {
        this.netWeight = netWeight;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getUsageInstructions() {
        return usageInstructions;
    }

    public void setUsageInstructions(String usageInstructions) {
        this.usageInstructions = usageInstructions;
    }

    public String getLegalDisclaimer() {
        return legalDisclaimer;
    }

    public void setLegalDisclaimer(String legalDisclaimer) {
        this.legalDisclaimer = legalDisclaimer;
    }

    // Getters and Setters - Información nutricional (OPCIONAL)
    public BigDecimal getEnergyKcalPer100g() {
        return energyKcalPer100g;
    }

    public void setEnergyKcalPer100g(BigDecimal energyKcalPer100g) {
        this.energyKcalPer100g = energyKcalPer100g;
    }

    public BigDecimal getEnergyKjPer100g() {
        return energyKjPer100g;
    }

    public void setEnergyKjPer100g(BigDecimal energyKjPer100g) {
        this.energyKjPer100g = energyKjPer100g;
    }

    public BigDecimal getFatPer100g() {
        return fatPer100g;
    }

    public void setFatPer100g(BigDecimal fatPer100g) {
        this.fatPer100g = fatPer100g;
    }

    public BigDecimal getSaturatedFatPer100g() {
        return saturatedFatPer100g;
    }

    public void setSaturatedFatPer100g(BigDecimal saturatedFatPer100g) {
        this.saturatedFatPer100g = saturatedFatPer100g;
    }

    public BigDecimal getCarbsPer100g() {
        return carbsPer100g;
    }

    public void setCarbsPer100g(BigDecimal carbsPer100g) {
        this.carbsPer100g = carbsPer100g;
    }

    public BigDecimal getSugarsPer100g() {
        return sugarsPer100g;
    }

    public void setSugarsPer100g(BigDecimal sugarsPer100g) {
        this.sugarsPer100g = sugarsPer100g;
    }

    public BigDecimal getProteinPer100g() {
        return proteinPer100g;
    }

    public void setProteinPer100g(BigDecimal proteinPer100g) {
        this.proteinPer100g = proteinPer100g;
    }

    public BigDecimal getSaltPer100g() {
        return saltPer100g;
    }

    public void setSaltPer100g(BigDecimal saltPer100g) {
        this.saltPer100g = saltPer100g;
    }

    public BigDecimal getFiberPer100g() {
        return fiberPer100g;
    }

    public void setFiberPer100g(BigDecimal fiberPer100g) {
        this.fiberPer100g = fiberPer100g;
    }

    // NUEVO: Getters y Setters para sodiumPer100g
    public BigDecimal getSodiumPer100g() {
        return sodiumPer100g;
    }

    public void setSodiumPer100g(BigDecimal sodiumPer100g) {
        this.sodiumPer100g = sodiumPer100g;
    }

    // NUEVO: Getters y Setters para campos de RecipeServiceImpl
    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
        // Si no hay productName, usar recipeName como productName
        if (this.productName == null) {
            this.productName = recipeName;
        }
    }

    public String getRecipeDescription() {
        return recipeDescription;
    }

    public void setRecipeDescription(String recipeDescription) {
        this.recipeDescription = recipeDescription;
        // Si no hay usageInstructions, usar recipeDescription como usageInstructions
        if (this.usageInstructions == null) {
            this.usageInstructions = recipeDescription;
        }
    }

    public BigDecimal getYieldWeightGrams() {
        return yieldWeightGrams;
    }

    public void setYieldWeightGrams(BigDecimal yieldWeightGrams) {
        this.yieldWeightGrams = yieldWeightGrams;
        // Si no hay netWeight, convertir yieldWeightGrams a netWeight
        if (this.netWeight == null && yieldWeightGrams != null) {
            this.netWeight = yieldWeightGrams + "g";
        }
    }

    public List<String> getIngredientsList() {
        return ingredientsList;
    }

    public void setIngredientsList(List<String> ingredientsList) {
        this.ingredientsList = ingredientsList;
        // Convertir ingredientsList a List<IngredientDTO> si es necesario
        if (this.ingredients == null && ingredientsList != null) {
            this.ingredients = ingredientsList.stream()
                    .map(ing -> {
                        IngredientDTO dto = new IngredientDTO();
                        dto.setName(ing);
                        // Puedes agregar lógica adicional para extraer cantidad/alérgenos
                        return dto;
                    })
                    .collect(java.util.stream.Collectors.toList());
        }
    }

    public List<String> getAllergens() {
        return allergens;
    }

    public void setAllergens(List<String> allergens) {
        this.allergens = allergens;
        // Actualizar isAllergen en los ingredientes si es necesario
        if (this.ingredients != null && allergens != null) {
            for (IngredientDTO ingredient : this.ingredients) {
                if (allergens.contains(ingredient.getName())) {
                    ingredient.setIsAllergen(true);
                }
            }
        }
    }

    // Métodos de compatibilidad para energyPer100g (si es necesario)
    public BigDecimal getEnergyPer100g() {
        return energyKjPer100g;
    }

    public void setEnergyPer100g(BigDecimal energyPer100g) {
        this.energyKjPer100g = energyPer100g;
    }

    // Métodos de compatibilidad
    @JsonProperty("expiryDate")
    public LocalDate getExpiryDate() {
        return expirationDate;
    }

    @JsonProperty("expiryDate")
    public void setExpiryDate(LocalDate expiryDate) {
        this.expirationDate = expiryDate;
    }

    // Método helper para información nutricional completa
    public boolean hasNutritionalInfo() {
        return energyKcalPer100g != null || energyKjPer100g != null || fatPer100g != null ||
                carbsPer100g != null || proteinPer100g != null || saltPer100g != null;
    }

    @Override
    public String toString() {
        return "LabelPrintDTO{" +
                "productName='" + productName + '\'' +
                ", version=" + version +
                ", status='" + status + '\'' +
                ", ingredients=" + (ingredients != null ? ingredients.size() : 0) + " items" +
                ", countryOfOrigin='" + countryOfOrigin + '\'' +
                ", batchNumber='" + batchNumber + '\'' +
                ", hasNutritionalInfo=" + hasNutritionalInfo() +
                '}';
    }
}