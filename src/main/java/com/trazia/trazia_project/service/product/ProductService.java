package com.trazia.trazia_project.service.product;

import com.trazia.trazia_project.dto.product.*;
import com.trazia.trazia_project.entity.product.Product;
import com.trazia.trazia_project.entity.product.ProductCategory;
import com.trazia.trazia_project.entity.user.User;
import com.trazia.trazia_project.exception.product.DuplicateProductException;
import com.trazia.trazia_project.exception.product.ProductNotFoundException;
import com.trazia.trazia_project.exception.recipe.ResourceNotFoundException;
import com.trazia.trazia_project.mapper.ProductMapper;
import com.trazia.trazia_project.repository.product.ProductRepository;
import com.trazia.trazia_project.repository.user.UserRepository;
import com.trazia.trazia_project.service.common.ImageStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;
    private final MessageSource messageSource;
    private final ImageStorageService imageStorageService;

    // ==================== CRUD OPERATIONS ====================

    @Transactional
    public ProductResponse createProduct(ProductRequest request, Long userId) {
        log.info("Creating product '{}' for user ID: {}", request.getName(), userId);

        User user = getUserOrThrow(userId);
        checkDuplicateProduct(userId, request.getName());

        Product product = productMapper.toEntity(request, user);
        validateNutriments(product);

        Product saved = productRepository.save(product);
        log.info("Product created successfully with ID: {}", saved.getId());

        return productMapper.toResponse(saved);
    }

    public ProductResponse getProductById(Long productId) {
        Product product = getProductOrThrow(productId);
        return productMapper.toResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, UpdateProductRequest request, Long userId) {
        Product product = getProductOrThrow(productId, userId);

        if (request.getName() != null && !request.getName().equals(product.getName())) {
            checkDuplicateProduct(userId, request.getName());
        }

        productMapper.updateEntity(product, request);
        validateNutriments(product);

        Product updated = productRepository.save(product);
        log.info("Product updated successfully: ID {}", productId);

        return productMapper.toResponse(updated);
    }

    @Transactional
    public void softDeleteProduct(Long productId, Long userId) {
        Product product = getProductOrThrow(productId, userId);
        product.markAsDeleted();
        productRepository.save(product);
        log.info("Soft deleted product ID: {} by user ID: {}", productId, userId);
    }

    @Transactional
    public void hardDeleteProduct(Long productId, Long userId) {
        Product product = getProductOrThrow(productId, userId);
        productRepository.delete(product);
        log.info("Hard deleted product ID: {} by user ID: {}", productId, userId);
    }

    @Transactional
    public ProductResponse restoreProduct(Long productId, Long userId) {
        Product product = getProductOrThrow(productId, userId);
        if (!product.getDeleted())
            return productMapper.toResponse(product);

        product.restore();
        Product restored = productRepository.save(product);
        log.info("Product restored successfully: ID {}", productId);
        return productMapper.toResponse(restored);
    }

    // ==================== QUERY OPERATIONS ====================

    public ProductPageResponse getUserProducts(Long userId, Pageable pageable) {
        Page<Product> products = productRepository.findByUserIdAndDeletedFalse(userId, pageable);
        return productMapper.toPageResponse(products);
    }

    public ProductPageResponse searchProducts(String query, Long userId, Pageable pageable) {
        Page<Product> products = productRepository.searchByNameOrBrand(query, userId, pageable);
        return productMapper.toPageResponse(products);
    }

    public ProductPageResponse getProductsByCategory(ProductCategory category, Long userId, Pageable pageable) {
        Page<Product> products = productRepository.findByUserIdAndCategoryAndDeletedFalse(userId, category, pageable);
        return productMapper.toPageResponse(products);
    }

    public List<ProductResponse> listProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ==================== IMAGE OPERATIONS ====================

    @Transactional
    public ProductResponse uploadProductImage(Long productId, MultipartFile file, Long userId) throws IOException {
        Product product = getProductOrThrow(productId, userId);

        if (product.hasImage()) {
            try {
                imageStorageService.deleteImage(product.getImagePath());
                log.info("Deleted old image: {}", product.getImagePath());
            } catch (IOException e) {
                log.warn("Failed to delete old image: {}", e.getMessage());
            }
        }

        String filename = imageStorageService.storeImage(file, productId);
        product.updateImage(filename);
        Product savedProduct = productRepository.save(product);
        log.info("Image uploaded successfully: {}", filename);

        return productMapper.toResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public byte[] getProductImage(Long productId) throws IOException {
        Product product = getProductOrThrow(productId);
        if (!product.hasImage())
            throw new IOException("Product has no image");
        return imageStorageService.loadImage(product.getImagePath());
    }

    @Transactional(readOnly = true)
    public byte[] getProductThumbnail(Long productId) throws IOException {
        Product product = getProductOrThrow(productId);
        if (!product.hasImage())
            throw new IOException("Product has no image");
        return imageStorageService.loadThumbnail(product.getImagePath());
    }

    @Transactional
    public void deleteProductImage(Long productId, Long userId) throws IOException {
        Product product = getProductOrThrow(productId, userId);
        if (product.hasImage()) {
            imageStorageService.deleteImage(product.getImagePath());
            product.removeImage();
            productRepository.save(product);
            log.info("Deleted image for product ID: {}", productId);
        }
    }

    // ==================== HELPER METHODS ====================

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("user.notFound", userId)));
    }

    private Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new ProductNotFoundException(getMessage("product.notFound", productId)));
    }

    private Product getProductOrThrow(Long productId, Long userId) {
        return productRepository.findByIdAndUserId(productId, userId)
                .orElseThrow(() -> new ProductNotFoundException(getMessage("product.notFound", productId)));
    }

    private void checkDuplicateProduct(Long userId, String name) {
        if (productRepository.existsByUserIdAndName(userId, name)) {
            throw new DuplicateProductException(getMessage("product.duplicate", name));
        }
    }

    private void validateNutriments(Product product) {
        if (product.getNutriments() == null)
            return;
        product.getNutriments().setCalories(product.getNutriments().getCalories());
        product.getNutriments().setProtein(product.getNutriments().getProtein());
        product.getNutriments().setCarbohydrates(product.getNutriments().getCarbohydrates());
        product.getNutriments().setFat(product.getNutriments().getFat());
        product.getNutriments().setSaturatedFat(product.getNutriments().getSaturatedFat());
    }

    private String getMessage(String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, locale);
    }
}