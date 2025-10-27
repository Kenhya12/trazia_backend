package com.trazia.trazia_project.mapper;

import com.trazia.trazia_project.dto.product.*;
import com.trazia.trazia_project.entity.product.Product;
import com.trazia.trazia_project.entity.product.ProductNutriments;
import com.trazia.trazia_project.entity.user.User;
import com.trazia.trazia_project.model.NutrimentsDTO;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    // Convertir ENTIDAD ProductNutriments a NutrimentsDTO (para respuestas)
    public NutrimentsDTO toNutrimentsDTO(ProductNutriments entity) {
        if (entity == null) return null;
        NutrimentsDTO dto = new NutrimentsDTO();
        dto.setCalories(entity.getCalories() != null ? entity.getCalories().doubleValue() : null);
        dto.setEnergyKcal(entity.getCalories() != null ? entity.getCalories().doubleValue() : null);
        dto.setProtein(entity.getProtein() != null ? entity.getProtein().doubleValue() : null);
        dto.setCarbohydrates(entity.getCarbohydrates() != null ? entity.getCarbohydrates().doubleValue() : null);
        dto.setFat(entity.getFat() != null ? entity.getFat().doubleValue() : null);
        dto.setSaturatedFat(entity.getSaturatedFat() != null ? entity.getSaturatedFat().doubleValue() : null);
        dto.setFiber(entity.getFiber() != null ? entity.getFiber().doubleValue() : null);
        dto.setSugars(entity.getSugars() != null ? entity.getSugars().doubleValue() : null);
        dto.setSodium(entity.getSodium() != null ? entity.getSodium().doubleValue() : null);
        return dto;
    }

    // Método para convertir NutrimentsRequest a ENTIDAD ProductNutriments
    public ProductNutriments toEntityProductNutriments(NutrimentsRequest request) {
        if (request == null) return null;
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

    // Método para convertir NutrimentsDTO a ENTIDAD ProductNutriments
    public ProductNutriments toEntityProductNutriments(NutrimentsDTO dto) {
        if (dto == null){
            return null;
        }
        return ProductNutriments.builder()
                .calories(dto.getEnergyKcal() != null ? BigDecimal.valueOf(dto.getEnergyKcal()) : null)
                .protein(dto.getProtein() != null ? BigDecimal.valueOf(dto.getProtein()) : null)
                .carbohydrates(dto.getCarbohydrates() != null ? BigDecimal.valueOf(dto.getCarbohydrates()) : null)
                .sugars(dto.getSugars() != null ? BigDecimal.valueOf(dto.getSugars()) : null)
                .fat(dto.getFat() != null ? BigDecimal.valueOf(dto.getFat()) : null)
                .saturatedFat(dto.getSaturatedFat() != null ? BigDecimal.valueOf(dto.getSaturatedFat()) : null)
                .fiber(dto.getFiber() != null ? BigDecimal.valueOf(dto.getFiber()) : null)
                .sodium(dto.getSodium() != null ? BigDecimal.valueOf(dto.getSodium()) : null)
                .build();
}

    public Product toEntity(ProductRequest dto, User user) {
        if (dto == null || user == null) return null;
        Product product = Product.builder()
                .user(user)
                .name(dto.getName())
                .description(dto.getDescription())
                .brand(dto.getBrand())
                .category(dto.getCategory())
                .servingSizeGrams(dto.getServingSizeGrams())
                .servingDescription(dto.getServingDescription())
                .labelingRegion(dto.getLabelingRegion() != null ? dto.getLabelingRegion() : com.trazia.trazia_project.entity.company.LabelingRegion.EU)
                .build();
        if (dto.getNutriments() != null) {
            product.setNutriments(toEntityProductNutriments(dto.getNutriments()));
        }
        return product;
    }

    public ProductResponse toResponse(Product product) {
        if (product == null) return null;
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .brand(product.getBrand())
                .category(product.getCategory())
                .nutriments(toNutrimentsDTO(product.getNutriments()))
                .servingSizeGrams(product.getServingSizeGrams())
                .servingDescription(product.getServingDescription())
                .labelingRegion(product.getLabelingRegion())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public ProductDTO toProductDTO(Product product) {
        if (product == null) return null;
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .description(product.getDescription())
                .category(product.getCategory())
                .imagePath(product.getImagePath())
                .thumbnailPath(product.getThumbnailPath())
                .imageUrl(product.getImageUrl())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .nutriments(toNutrimentsDTO(product.getNutriments()))
                .build();
    }

    public void updateEntity(Product product, UpdateProductRequest updateDto) {
        if (product == null || updateDto == null) return;
        if (updateDto.getName() != null) product.setName(updateDto.getName());
        if (updateDto.getDescription() != null) product.setDescription(updateDto.getDescription());
        if (updateDto.getBrand() != null) product.setBrand(updateDto.getBrand());
        if (updateDto.getCategory() != null) product.setCategory(updateDto.getCategory());
        if (updateDto.getNutriments() != null) product.setNutriments(toEntityProductNutriments(updateDto.getNutriments()));
        if (updateDto.getServingSizeGrams() != null) product.setServingSizeGrams(updateDto.getServingSizeGrams());
        if (updateDto.getServingDescription() != null) product.setServingDescription(updateDto.getServingDescription());
        if (updateDto.getLabelingRegion() != null) product.setLabelingRegion(updateDto.getLabelingRegion());
    }

    public ProductPageResponse.ProductSummaryDTO toProductSummaryDTO(Product product) {
        if (product == null) return null;
        ProductPageResponse.NutritionSummary nutritionSummary = null;
        if (product.getNutriments() != null) {
            nutritionSummary = ProductPageResponse.NutritionSummary.builder()
                    .calories(product.getNutriments().getCalories() != null ? product.getNutriments().getCalories().doubleValue() : null)
                    .protein(product.getNutriments().getProtein() != null ? product.getNutriments().getProtein().doubleValue() : null)
                    .carbohydrates(product.getNutriments().getCarbohydrates() != null ? product.getNutriments().getCarbohydrates().doubleValue() : null)
                    .fat(product.getNutriments().getFat() != null ? product.getNutriments().getFat().doubleValue() : null)
                    .build();
        }
        return ProductPageResponse.ProductSummaryDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .category(product.getCategory())
                .imageUrl(product.getImageUrl())
                .hasCompleteNutrition(product.hasCompleteNutritionInfo())
                .nutrition(nutritionSummary)
                .build();
    }

    public ProductPageResponse toPageResponse(Page<Product> productPage) {
        if (productPage == null) return null;
        List<ProductPageResponse.ProductSummaryDTO> summaries = productPage.getContent().stream()
                .map(this::toProductSummaryDTO)
                .collect(Collectors.toList());
        ProductPageResponse.PageMetadata pageMetadata = ProductPageResponse.PageMetadata.builder()
                .currentPage(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .hasNext(productPage.hasNext())
                .hasPrevious(productPage.hasPrevious())
                .build();
        return ProductPageResponse.builder()
                .products(summaries)
                .pagination(pageMetadata)
                .page(productPage.getNumber())
                .totalPages(productPage.getTotalPages())
                .totalElements(productPage.getTotalElements())
                .pageSize(productPage.getSize())
                .hasNext(productPage.hasNext())
                .hasPrevious(productPage.hasPrevious())
                .build();
    }
}
