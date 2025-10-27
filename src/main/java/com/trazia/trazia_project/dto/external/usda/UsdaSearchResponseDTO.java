package com.trazia.trazia_project.dto.external.usda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsdaSearchResponseDTO {
    
    private List<UsdaFoodDTO> foods;
    
    private Integer totalHits;
    
    private Integer currentPage;
    
    private Integer totalPages;
}
