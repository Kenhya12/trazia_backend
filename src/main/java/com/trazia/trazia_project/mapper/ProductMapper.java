package com.trazia.trazia_project.mapper;

import com.trazia.trazia_project.dto.product.*;
import com.trazia.trazia_project.entity.User;
import com.trazia.trazia_project.entity.product.Product;
import com.trazia.trazia_project.entity.product.ProductNutriments;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    private static final String BASE_URL = "http://localhost:9090";

    // ==================== ENTITY CONVERSIONS ====================

    /**
     * Convierte ProductRequest a Product Entity
     */
    public Product toEntity(ProductRequest request, User user) {
        Product product = Product.builder()
                .user(user)
                .name(request.getName())
                .description(request.getDescription())
                .brand(request.getBrand())
                .category(request.getCategory())
                .servingSizeGrams(request.getServingSizeGrams())
                .servingDescription(request.getServingDescription())
                .labelingRegion(request.getLabelingRegion())
                .build();

        if (request.getNutriments() != null) {
            product.setNutriments(toNutrimentsEntity(request.getNutriments()));
        }

        return product;
    }

    /**
     * Actualiza Product existente con datos de UpdateProductRequest
     */
    public void updateEntity(Product product, UpdateProductRequest request) {
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getBrand() != null) {
            product.setBrand(request.getBrand());
        }
        if (request.getCategory() != null) {
            product.setCategory(request.getCategory());
        }
        if (request.getServingSizeGrams() != null) {
            product.setServingSizeGrams(request.getServingSizeGrams());
        }
        if (request.getServingDescription() != null) {
            product.setServingDescription(request.getServingDescription());
        }
        if (request.getLabelingRegion() != null) {
            product.setLabelingRegion(request.getLabelingRegion());
        }
        if (request.getNutriments() != null) {
            product.setNutriments(toNutrimentsEntity(request.getNutriments()));
        }
    }

    /**
     * Convierte NutrimentsRequest a ProductNutriments Entity
     */
    private ProductNutriments toNutrimentsEntity(NutrimentsRequest request) {
        return ProductNutriments.builder()
                .calories(request.getCalories())
                .protein(request.getProtein())
                .carbohydrates(request.getCarbohydrates())
                .sugars(request.getSugars())
                .fat(request.getFat())
                .saturatedFat(request.getSaturatedFat())
                .fiber(request.getFiber())
                .sodium(request.getSodium())
                .salt(request.getSalt())
                .build();
    }

    // ==================== RESPONSE CONVERSIONS ====================

    /**
     * Convierte Product Entity a ProductResponse COMPLETO
     * Usado para detalles de un producto individual
     */
    public ProductResponse toResponse(Product product) {
        String imageUrl = null;
        String thumbnailUrl = null;
        
        if (product.hasImage()) {
            imageUrl = BASE_URL + "/api/products/" + product.getId() + "/image";
            thumbnailUrl = BASE_URL + "/api/products/" + product.getId() + "/image/thumbnail";
        }

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .brand(product.getBrand())
                .category(product.getCategory())
                .imageUrl(imageUrl)
                .thumbnailUrl(thumbnailUrl)
                .imagePath(product.getImagePath())
                .thumbnailPath(product.getThumbnailPath())
                .nutriments(product.getNutriments())
                .servingSizeGrams(product.getServingSizeGrams())
                .servingDescription(product.getServingDescription())
                .labelingRegion(product.getLabelingRegion())
                .userId(product.getUser().getId())
                .createdByUsername(product.getUser().getUsername())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .hasCompleteNutrition(product.hasCompleteNutritionInfo())
                .nutrimentsPerServing(product.getNutrimentsPerServing())
                .build();
    }

    /**
     * Convierte Product a ProductSummaryDTO para listados paginados
     */
    public ProductPageResponse.ProductSummaryDTO toSummaryDTO(Product product) {
        String imageUrl = null;
        if (product.hasImage()) {
            imageUrl = BASE_URL + "/api/products/" + product.getId() + "/image/thumbnail";
        }

        ProductPageResponse.NutritionSummary nutritionSummary = null;
        if (product.getNutriments() != null) {
            nutritionSummary = ProductPageResponse.NutritionSummary.builder()
                    .calories(toDouble(product.getNutriments().getCalories()))
                    .protein(toDouble(product.getNutriments().getProtein()))
                    .carbohydrates(toDouble(product.getNutriments().getCarbohydrates()))
                    .fat(toDouble(product.getNutriments().getFat()))
                    .build();
        }

        return ProductPageResponse.ProductSummaryDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .category(product.getCategory())
                .imageUrl(imageUrl)
                .hasCompleteNutrition(product.hasCompleteNutritionInfo())
                .nutrition(nutritionSummary)
                .build();
    }

    /**
     * Convierte Page<Product> a ProductPageResponse
     */
    public ProductPageResponse toPageResponse(Page<Product> productPage) {
        List<ProductPageResponse.ProductSummaryDTO> productSummaries = productPage.getContent()
                .stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());

        return ProductPageResponse.builder()
                .products(productSummaries)
                .page(productPage.getNumber())
                .totalPages(productPage.getTotalPages())
                .totalElements(productPage.getTotalElements())
                .pageSize(productPage.getSize())
                .hasNext(productPage.hasNext())
                .hasPrevious(productPage.hasPrevious())
                .build();
    }

    // ==================== HELPER METHODS ====================

    /**
     * Convierte BigDecimal a Double (para DTOs)
     */
    private Double toDouble(BigDecimal value) {
        return value != null ? value.doubleValue() : null;
    }
}
