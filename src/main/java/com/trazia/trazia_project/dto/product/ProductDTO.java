package com.trazia.trazia_project.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import com.trazia.trazia_project.entity.product.ProductCategory;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    private String name;
    private String brand;
    private String barcode;
    private String description;
    private String imagePath;
    private String thumbnailPath;
    private String imageUrl;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProductCategory category;
    private NutrimentsDTO nutriments; // ✅ Añadido campo para nutrición
}
