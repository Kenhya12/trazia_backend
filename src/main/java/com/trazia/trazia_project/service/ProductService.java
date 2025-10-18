package com.trazia.trazia_project.service;

import com.trazia.trazia_project.dto.product.*;
import com.trazia.trazia_project.entity.User;
import com.trazia.trazia_project.entity.product.Product;
import com.trazia.trazia_project.entity.product.ProductCategory;
import com.trazia.trazia_project.exception.DuplicateProductException;
import com.trazia.trazia_project.exception.ProductNotFoundException;
import com.trazia.trazia_project.exception.ResourceNotFoundException;
import com.trazia.trazia_project.mapper.ProductMapper;
import com.trazia.trazia_project.repository.ProductRepository;
import com.trazia.trazia_project.repository.UserRepository;
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
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;
    private final MessageSource messageSource;
    private final ImageStorageService imageStorageService;  // ✅ SOLO UNA VEZ

    // ==================== CRUD OPERATIONS ====================

    @Transactional
    public ProductResponse createProduct(ProductRequest request, Long userId) {
        log.info("Creating product with name: {} for user ID: {}", request.getName(), userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        getMessage("user.notFound", userId)));

        boolean exists = productRepository.existsByUserIdAndName(userId, request.getName());
        if (exists) {
            throw new DuplicateProductException(
                    getMessage("product.duplicate", request.getName()));
        }

        Product product = productMapper.toEntity(request, user);

        if (product.getNutriments() != null) {
            product.getNutriments().setCalories(product.getNutriments().getCalories());
            product.getNutriments().setProtein(product.getNutriments().getProtein());
            product.getNutriments().setCarbohydrates(product.getNutriments().getCarbohydrates());
            product.getNutriments().setFat(product.getNutriments().getFat());
            product.getNutriments().setSaturatedFat(product.getNutriments().getSaturatedFat());
        }

        Product saved = productRepository.save(product);
        log.info("Product created successfully with ID: {}", saved.getId());

        return productMapper.toResponse(saved);
    }

    public ProductResponse getProductById(Long productId) {
        log.info("Fetching product by ID: {}", productId);

        Product product = productRepository.findById(productId)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new ProductNotFoundException(
                        getMessage("product.notFound", productId)));

        return productMapper.toResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, UpdateProductRequest request, Long userId) {
        Product product = productRepository.findByIdAndUserId(productId, userId)
                .orElseThrow(() -> new ProductNotFoundException(
                        getMessage("product.notFound", productId)));

        if (request.getName() != null && !request.getName().equals(product.getName())) {
            boolean exists = productRepository.existsByUserIdAndName(userId, request.getName());
            if (exists) {
                throw new DuplicateProductException(
                        getMessage("product.duplicate", request.getName()));
            }
        }

        productMapper.updateEntity(product, request);

        if (product.getNutriments() != null) {
            product.getNutriments().setCalories(product.getNutriments().getCalories());
            product.getNutriments().setProtein(product.getNutriments().getProtein());
            product.getNutriments().setCarbohydrates(product.getNutriments().getCarbohydrates());
            product.getNutriments().setFat(product.getNutriments().getFat());
            product.getNutriments().setSaturatedFat(product.getNutriments().getSaturatedFat());
        }

        Product updated = productRepository.save(product);
        log.info("Product updated successfully: ID {}", productId);

        return productMapper.toResponse(updated);
    }

    @Transactional
    public void softDeleteProduct(Long productId, Long userId) {
        log.info("Soft deleting product ID: {} by user ID: {}", productId, userId);

        Product product = productRepository.findByIdAndUserId(productId, userId)
                .orElseThrow(() -> new ProductNotFoundException(
                        getMessage("product.notFound", productId)));

        product.markAsDeleted();
        productRepository.save(product);
    }

    @Transactional
    public void hardDeleteProduct(Long productId, Long userId) {
        log.info("Hard deleting product ID: {} by user ID: {}", productId, userId);

        Product product = productRepository.findByIdAndUserId(productId, userId)
                .orElseThrow(() -> new ProductNotFoundException(
                        getMessage("product.notFound", productId)));

        productRepository.delete(product);
    }

    @Transactional
    public ProductResponse restoreProduct(Long productId, Long userId) {
        Product product = productRepository.findByIdAndUserId(productId, userId)
                .filter(Product::getDeleted)
                .orElseThrow(() -> new ProductNotFoundException(
                        getMessage("product.notFound", productId)));

        product.restore();
        Product restored = productRepository.save(product);
        log.info("Product restored successfully: ID {}", productId);

        return productMapper.toResponse(restored);
    }

    // ==================== QUERY OPERATIONS ====================

    public ProductPageResponse getUserProducts(Long userId, Pageable pageable) {
        log.info("Fetching products for user ID: {}", userId);
        Page<Product> products = productRepository.findByUserIdAndDeletedFalse(userId, pageable);
        return productMapper.toPageResponse(products);
    }

    public ProductPageResponse searchProducts(String query, Long userId, Pageable pageable) {
        log.info("Searching products with query: {} for user ID: {}", query, userId);
        Page<Product> products = productRepository.searchByNameOrBrand(query, userId, pageable);
        return productMapper.toPageResponse(products);
    }

    public ProductPageResponse getProductsByCategory(ProductCategory category, Long userId, Pageable pageable) {
        log.info("Fetching products by category: {} for user ID: {}", category, userId);
        Page<Product> products = productRepository.findByUserIdAndCategoryAndDeletedFalse(
                userId, category, pageable);
        return productMapper.toPageResponse(products);
    }

    // ==================== IMAGE OPERATIONS ====================

    @Transactional
    public ProductResponse uploadProductImage(Long productId, MultipartFile file, Long userId) throws IOException {
        log.info("Uploading image for product ID: {} by user ID: {}", productId, userId);

        Product product = productRepository.findByIdAndUserId(productId, userId)
                .orElseThrow(() -> new ProductNotFoundException(
                        getMessage("product.notFound", productId)));

        if (product.hasImage()) {
            try {
                imageStorageService.deleteImage(product.getImagePath());
                log.info("Old image deleted: {}", product.getImagePath());
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
        log.debug("Fetching image for product ID: {}", productId);

        Product product = productRepository.findById(productId)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new ProductNotFoundException(
                        getMessage("product.notFound", productId)));

        if (!product.hasImage()) {
            throw new IOException("Product has no image");
        }

        return imageStorageService.loadImage(product.getImagePath());
    }

    @Transactional(readOnly = true)
    public byte[] getProductThumbnail(Long productId) throws IOException {
        log.debug("Fetching thumbnail for product ID: {}", productId);

        Product product = productRepository.findById(productId)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new ProductNotFoundException(
                        getMessage("product.notFound", productId)));

        if (!product.hasImage()) {
            throw new IOException("Product has no image");
        }

        return imageStorageService.loadThumbnail(product.getImagePath());
    }

    @Transactional
    public void deleteProductImage(Long productId, Long userId) throws IOException {
        log.info("Deleting image for product ID: {} by user ID: {}", productId, userId);

        Product product = productRepository.findByIdAndUserId(productId, userId)
                .orElseThrow(() -> new ProductNotFoundException(
                        getMessage("product.notFound", productId)));

        if (product.hasImage()) {
            imageStorageService.deleteImage(product.getImagePath());
            product.removeImage();
            productRepository.save(product);
            log.info("Image deleted successfully for product ID: {}", productId);
        }
    }

    // ==================== HELPER METHODS ====================

    private String getMessage(String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, locale);
    }
}
