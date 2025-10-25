package com.trazia.trazia_project.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NutrimentsDTO {
    
    @JsonProperty("energy-kcal_100g")
    private Double energyKcal;

    @JsonProperty("proteins_100g")
    private Double proteins;

    @JsonProperty("carbohydrates_100g")
    private Double carbohydrates;

    @JsonProperty("fat_100g")
    private Double fat;

    @JsonProperty("fiber_100g")
    private Double fiber;

    @JsonProperty("sodium_100g")
    private Double sodium;

    @JsonProperty("sugars_100g")
    private Double sugars;

    @JsonProperty("salt_100g")
    private Double salt;

    @JsonProperty("saturated-fat_100g")
    private Double saturatedFat;

    @JsonProperty("unsaturated-fat_100g")
    private Double unsaturatedFat;

    // Campos adicionales para compatibilidad
    private Double calories;
    private Double protein;

    // Constructor vacío importante
    public NutrimentsDTO() {
        // Inicializar con valores por defecto si es necesario
    }
}