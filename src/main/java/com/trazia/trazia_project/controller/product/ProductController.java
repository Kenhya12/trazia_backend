package com.trazia.trazia_project.controller.product;

import org.springframework.http.MediaType;
import com.trazia.trazia_project.dto.product.*;
import com.trazia.trazia_project.entity.User;
import com.trazia.trazia_project.entity.product.ProductCategory;
import com.trazia.trazia_project.service.product.ProductService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {

        private final ProductService productService;

        // Constructor para inyección de dependencias
        public ProductController(ProductService productService) {
                this.productService = productService;
        }

        @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<List<ProductResponse>> listProducts() {
                // log.info("Listing all products (non-paginated)");
                List<ProductResponse> products = productService.listProducts();
                return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(products);
        }

        @PostMapping
        public ResponseEntity<ProductResponse> createProduct(
                        @Valid @RequestBody ProductRequest request,
                        @AuthenticationPrincipal User user) {
                log.info("Creating product with name: {} for user ID: {}", request.getName(), user.getId());
                ProductResponse response = productService.createProduct(request, user.getId());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @GetMapping(path = "/page", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<ProductPageResponse> getAllProducts(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size,
                        @RequestParam(defaultValue = "createdAt") String sortBy,
                        @RequestParam(defaultValue = "DESC") String direction,
                        @AuthenticationPrincipal User user) {
                log.info("Fetching products for user ID: {}", user.getId());
                Sort.Direction sortDirection = Sort.Direction.fromString(direction);
                Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
                ProductPageResponse response = productService.getUserProducts(user.getId(), pageable);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/{id}")
        public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
                log.info("Fetching product with ID: {}", id);
                ProductResponse response = productService.getProductById(id);
                if (response == null) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
                }
                return ResponseEntity.ok(response);
        }

        @PutMapping("/{id}")
        public ResponseEntity<ProductResponse> updateProduct(
                        @PathVariable Long id,
                        @Valid @RequestBody UpdateProductRequest request,
                        @AuthenticationPrincipal User user) {
                log.info("Updating product ID: {} by user ID: {}", id, user.getId());
                ProductResponse response = productService.updateProduct(id, request, user.getId());
                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteProduct(
                        @PathVariable Long id,
                        @AuthenticationPrincipal User user) {
                log.info("Soft deleting product ID: {} by user ID: {}", id, user.getId());
                productService.softDeleteProduct(id, user.getId());
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/search")
        public ResponseEntity<ProductPageResponse> searchProducts(
                        @RequestParam String query,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size,
                        @AuthenticationPrincipal User user) {
                log.info("Searching products with query: {} for user ID: {}", query, user.getId());
                Pageable pageable = PageRequest.of(page, size);
                ProductPageResponse response = productService.searchProducts(query, user.getId(), pageable);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/category/{category}")
        public ResponseEntity<ProductPageResponse> getProductsByCategory(
                        @PathVariable ProductCategory category,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size,
                        @AuthenticationPrincipal User user) {
                log.info("Fetching products by category: {} for user ID: {}", category, user.getId());
                Pageable pageable = PageRequest.of(page, size);
                ProductPageResponse response = productService.getProductsByCategory(category, user.getId(), pageable);
                return ResponseEntity.ok(response);
        }

        @PatchMapping("/{id}/restore")
        public ResponseEntity<ProductResponse> restoreProduct(
                        @PathVariable Long id,
                        @AuthenticationPrincipal User user) {
                log.info("Restoring product ID: {} by user ID: {}", id, user.getId());
                ProductResponse response = productService.restoreProduct(id, user.getId());
                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/{id}/hard")
        public ResponseEntity<Void> hardDeleteProduct(
                        @PathVariable Long id,
                        @AuthenticationPrincipal User user) {
                log.info("Hard deleting product ID: {} by user ID: {}", id, user.getId());
                productService.hardDeleteProduct(id, user.getId());
                return ResponseEntity.noContent().build();
        }

}