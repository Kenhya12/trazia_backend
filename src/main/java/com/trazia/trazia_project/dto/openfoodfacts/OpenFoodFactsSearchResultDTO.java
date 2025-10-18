package com.trazia.trazia_project.dto.openfoodfacts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenFoodFactsSearchResultDTO {
    
    @JsonProperty("count")
    private Integer count;
    
    @JsonProperty("page")
    private Integer page;
    
    @JsonProperty("page_size")
    private Integer pageSize;
    
    @JsonProperty("products")
    private List<OpenFoodFactsProductDTO> products;
}

