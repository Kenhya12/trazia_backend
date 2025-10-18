package com.trazia.trazia_project.dto.openfoodfacts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenFoodFactsProductDTO {
    
    @JsonProperty("code")
    private String code;
    
    @JsonProperty("product_name")
    private String productName;
    
    @JsonProperty("brands")
    private String brands;
    
    @JsonProperty("quantity")
    private String quantity;
    
    @JsonProperty("image_url")
    private String imageUrl;
    
    @JsonProperty("ingredients_text")
    private String ingredientsText;
    
    @JsonProperty("allergens")
    private String allergens;
    
    @JsonProperty("nutriments")
    private NutrimentsDTO nutriments;
}




