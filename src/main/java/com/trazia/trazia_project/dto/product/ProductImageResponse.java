package com.trazia.trazia_project.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageResponse {
    private String message;
    private Long productId;
    private String imagePath;
    private String thumbnailPath;
    private String imageUrl;
    private String thumbnailUrl;
    
    public static ProductImageResponse success(ProductDTO product, String baseUrl) {
        return new ProductImageResponse(
            "Image uploaded successfully",
            product.getId(),
            product.getImagePath(),
            product.getThumbnailPath(),
            baseUrl + "/api/products/" + product.getId() + "/image",
            baseUrl + "/api/products/" + product.getId() + "/image/thumbnail"
        );
    }
}
