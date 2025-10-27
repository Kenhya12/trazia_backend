package com.trazia.trazia_project.dto.external.usda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsdaFoodDTO {
    
    @JsonProperty("fdcId")
    private Long fdcId;
    
    private String description;
    
    @JsonProperty("brandOwner")
    private String brandOwner;
    
    @JsonProperty("dataType")
    private String dataType;
    
    @JsonProperty("foodNutrients")
    private List<UsdaNutrientDTO> foodNutrients;
    
    @JsonProperty("servingSize")
    private Double servingSize;
    
    @JsonProperty("servingSizeUnit")
    private String servingSizeUnit;
}

