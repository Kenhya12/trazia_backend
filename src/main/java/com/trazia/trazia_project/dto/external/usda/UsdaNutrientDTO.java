package com.trazia.trazia_project.dto.external.usda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsdaNutrientDTO {
    
    @JsonProperty("nutrientId")
    private Integer nutrientId;
    
    @JsonProperty("nutrientName")
    private String nutrientName;
    
    @JsonProperty("nutrientNumber")
    private String nutrientNumber;
    
    @JsonProperty("unitName")
    private String unitName;
    
    private Double value;
}
