package com.trazia.trazia_project.dto.product;

import com.trazia.trazia_project.entity.LabelingRegion;
import com.trazia.trazia_project.entity.product.ProductCategory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequest {
    @Size(min = 3, max = 150)
    private String name;

    @Size(max = 1000)
    private String description;

    @Size(max = 100)
    private String brand;

    private ProductCategory category;

    @Valid
    private NutrimentsRequest nutriments;

    @Min(1)
    @Max(10000)
    private Integer servingSizeGrams;

    @Size(max = 100)
    private String servingDescription;

    private LabelingRegion labelingRegion;
}

