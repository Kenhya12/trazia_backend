package com.trazia.trazia_project.dto.openfoodfacts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NutrimentsDTO {
    
    @JsonProperty("energy-kcal_100g")
    private BigDecimal energyKcal100g;
    
    @JsonProperty("proteins_100g")
    private BigDecimal proteins100g;
    
    @JsonProperty("carbohydrates_100g")
    private BigDecimal carbohydrates100g;
    
    @JsonProperty("fat_100g")
    private BigDecimal fat100g;
    
    @JsonProperty("fiber_100g")
    private BigDecimal fiber100g;
    
    @JsonProperty("sodium_100g")
    private BigDecimal sodium100g;
    
    @JsonProperty("sugars_100g")
    private BigDecimal sugars100g;
    
    @JsonProperty("saturated-fat_100g")
    private BigDecimal saturatedFat100g;
}


