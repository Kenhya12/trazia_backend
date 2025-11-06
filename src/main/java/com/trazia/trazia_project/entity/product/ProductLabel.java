package com.trazia.trazia_project.entity.product;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "product_labels")
public class ProductLabel {

    @Id
    private String id;

    // Información básica del producto
    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer version = 1;

    @Column(nullable = false)
    private String status = "draft";

    @Column(nullable = false)
    private String language = "es";

    // Información de la empresa
    private String companyName;
    private String companyAddress;

    // Información del producto
    private String countryOfOrigin;
    private String batchNumber;
    private String netWeight;
    private LocalDate expirationDate;
    private String usageInstructions;
    private String legalDisclaimer;

    // Información nutricional
    private BigDecimal energyKcalPer100g;
    private BigDecimal energyKjPer100g;
    private BigDecimal fatPer100g;
    private BigDecimal saturatedFatPer100g;
    private BigDecimal carbsPer100g;
    private BigDecimal sugarsPer100g;
    private BigDecimal proteinPer100g;
    private BigDecimal saltPer100g;
    private BigDecimal fiberPer100g;

    // Ingredientes y alérgenos
    @ElementCollection
    @CollectionTable(name = "label_ingredients", joinColumns = @JoinColumn(name = "label_id"))
    private List<Ingredient> ingredients;

    @ElementCollection
    @CollectionTable(name = "label_allergens", joinColumns = @JoinColumn(name = "label_id"))
    private List<String> allergens;

    // Fechas de auditoría
    private LocalDate createdAt;
    private LocalDate updatedAt;

    // Clase embebida para ingredientes
    @Embeddable
    public static class Ingredient {
        private String name;
        private String quantity;
        private Boolean isAllergen = false;

        // Constructors
        public Ingredient() {}

        public Ingredient(String name, String quantity, Boolean isAllergen) {
            this.name = name;
            this.quantity = quantity;
            this.isAllergen = isAllergen;
        }

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getQuantity() { return quantity; }
        public void setQuantity(String quantity) { this.quantity = quantity; }

        public Boolean getIsAllergen() { return isAllergen; }
        public void setIsAllergen(Boolean isAllergen) { this.isAllergen = isAllergen; }
    }

    // Constructors
    public ProductLabel() {}

    // Getters and Setters - Información básica
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    // Getters and Setters - Información de la empresa
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyAddress() { return companyAddress; }
    public void setCompanyAddress(String companyAddress) { this.companyAddress = companyAddress; }

    // Getters and Setters - Información del producto
    public String getCountryOfOrigin() { return countryOfOrigin; }
    public void setCountryOfOrigin(String countryOfOrigin) { this.countryOfOrigin = countryOfOrigin; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public String getNetWeight() { return netWeight; }
    public void setNetWeight(String netWeight) { this.netWeight = netWeight; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public String getUsageInstructions() { return usageInstructions; }
    public void setUsageInstructions(String usageInstructions) { this.usageInstructions = usageInstructions; }

    public String getLegalDisclaimer() { return legalDisclaimer; }
    public void setLegalDisclaimer(String legalDisclaimer) { this.legalDisclaimer = legalDisclaimer; }

    // Getters and Setters - Información nutricional
    public BigDecimal getEnergyKcalPer100g() { return energyKcalPer100g; }
    public void setEnergyKcalPer100g(BigDecimal energyKcalPer100g) { this.energyKcalPer100g = energyKcalPer100g; }

    public BigDecimal getEnergyKjPer100g() { return energyKjPer100g; }
    public void setEnergyKjPer100g(BigDecimal energyKjPer100g) { this.energyKjPer100g = energyKjPer100g; }

    public BigDecimal getFatPer100g() { return fatPer100g; }
    public void setFatPer100g(BigDecimal fatPer100g) { this.fatPer100g = fatPer100g; }

    public BigDecimal getSaturatedFatPer100g() { return saturatedFatPer100g; }
    public void setSaturatedFatPer100g(BigDecimal saturatedFatPer100g) { this.saturatedFatPer100g = saturatedFatPer100g; }

    public BigDecimal getCarbsPer100g() { return carbsPer100g; }
    public void setCarbsPer100g(BigDecimal carbsPer100g) { this.carbsPer100g = carbsPer100g; }

    public BigDecimal getSugarsPer100g() { return sugarsPer100g; }
    public void setSugarsPer100g(BigDecimal sugarsPer100g) { this.sugarsPer100g = sugarsPer100g; }

    public BigDecimal getProteinPer100g() { return proteinPer100g; }
    public void setProteinPer100g(BigDecimal proteinPer100g) { this.proteinPer100g = proteinPer100g; }

    public BigDecimal getSaltPer100g() { return saltPer100g; }
    public void setSaltPer100g(BigDecimal saltPer100g) { this.saltPer100g = saltPer100g; }

    public BigDecimal getFiberPer100g() { return fiberPer100g; }
    public void setFiberPer100g(BigDecimal fiberPer100g) { this.fiberPer100g = fiberPer100g; }

    // Getters and Setters - Ingredientes y alérgenos
    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }

    public List<String> getAllergens() { return allergens; }
    public void setAllergens(List<String> allergens) { this.allergens = allergens; }

    // Getters and Setters - Fechas de auditoría
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }

    // Métodos de compatibilidad
    public LocalDate getExpiryDate() { return expirationDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expirationDate = expiryDate; }

    @Override
    public String toString() {
        return "ProductLabel{" +
                "id='" + id + '\'' +
                ", productName='" + productName + '\'' +
                ", version=" + version +
                ", status='" + status + '\'' +
                ", countryOfOrigin='" + countryOfOrigin + '\'' +
                ", batchNumber='" + batchNumber + '\'' +
                ", ingredients=" + (ingredients != null ? ingredients.size() : 0) + " items" +
                '}';
    }
}