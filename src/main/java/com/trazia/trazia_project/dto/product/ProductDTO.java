package com.trazia.trazia_project.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private String imageUrl;          // URL completa para el frontend
    private String thumbnailUrl;      // URL completa del thumbnail
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
