package com.trazia.trazia_project.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageUploadRequest {
    private Long productId;
    private String imageFormat;
    private Long imageSize;
}
