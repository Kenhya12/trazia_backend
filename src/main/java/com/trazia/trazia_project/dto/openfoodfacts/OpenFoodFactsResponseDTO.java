package com.trazia.trazia_project.dto.openfoodfacts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenFoodFactsResponseDTO {
    
    @JsonProperty("status")
    private Integer status;
    
    @JsonProperty("product")
    private OpenFoodFactsProductDTO product;
}


