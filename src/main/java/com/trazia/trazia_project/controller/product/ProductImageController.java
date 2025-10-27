package com.trazia.trazia_project.controller.product;

import com.trazia.trazia_project.dto.product.ProductResponse;
import com.trazia.trazia_project.entity.user.User;
import com.trazia.trazia_project.service.product.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductImageController {

    private final ProductService productService;

    /**
     * POST /api/products/{id}/image
     * Subir imagen (solo propietario)
     */
    @PostMapping("/{id}/image")
    public ResponseEntity<?> uploadProductImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file,
            @AuthenticationPrincipal User user) {
        
        log.info("Upload image request for product {} by user {}", id, user.getId());
        
        try {
            ProductResponse response = productService.uploadProductImage(id, file, user.getId());
            return ResponseEntity.ok(Map.of(
                "message", "Image uploaded successfully",
                "product", response
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid image upload: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            log.error("Failed to upload image for product {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload image: " + e.getMessage()));
        }
    }

    /**
     * GET /api/products/{id}/image
     * Obtener imagen (público)
     */
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long id) {
        log.debug("Get image request for product {}", id);
        
        try {
            byte[] imageBytes = productService.getProductImage(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(imageBytes.length);
            headers.setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic());
            
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.warn("Image not found for product {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/products/{id}/image/thumbnail
     * Obtener thumbnail (público)
     */
    @GetMapping("/{id}/image/thumbnail")
    public ResponseEntity<byte[]> getProductThumbnail(@PathVariable Long id) {
        log.debug("Get thumbnail request for product {}", id);
        
        try {
            byte[] thumbnailBytes = productService.getProductThumbnail(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(thumbnailBytes.length);
            headers.setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic());
            
            return new ResponseEntity<>(thumbnailBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.warn("Thumbnail not found for product {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/products/{id}/image
     * Eliminar imagen (solo propietario)
     */
    @DeleteMapping("/{id}/image")
    public ResponseEntity<?> deleteProductImage(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        
        log.info("Delete image request for product {} by user {}", id, user.getId());
        
        try {
            productService.deleteProductImage(id, user.getId());
            return ResponseEntity.ok(Map.of("message", "Image deleted successfully"));
        } catch (IOException e) {
            log.error("Failed to delete image for product {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete image: " + e.getMessage()));
        }
    }
}
