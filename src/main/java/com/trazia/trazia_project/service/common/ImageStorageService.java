package com.trazia.trazia_project.service.common;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

/**
 * Servicio para gestión de imágenes de productos
 * Utiliza Thumbnailator para redimensionamiento
 * Almacena archivos en filesystem local
 */
@Service
@Slf4j
public class ImageStorageService {

    @Value("${file.upload-dir:uploads/products}")
    private String uploadDir;

    @Value("${file.thumbnail-dir:uploads/thumbnails}")
    private String thumbnailDir;

    @Value("${file.max-size:5242880}") // 5MB
    private long maxFileSize;

    @Value("${thumbnail.width:200}")
    private final int thumbnailWidth = 200;

    @Value("${thumbnail.height:200}")
    private final int thumbnailHeight = 200;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp");

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Path.of(uploadDir));
            Files.createDirectories(Path.of(thumbnailDir));
            log.info("Storage directories created: {} / {}", uploadDir, thumbnailDir);
        } catch (IOException e) {
            log.error("Failed to create directories", e);
            throw new RuntimeException("Could not initialize storage directories", e);
        }
    }

    public String storeImage(MultipartFile file, Long productId) throws IOException {
        log.debug("Storing image for product ID: {}", productId);

        validateFile(file);

        String filename = generateUniqueFilename(file.getOriginalFilename(), productId);
        Path destination = Path.of(uploadDir, filename);

        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        log.info("Image saved: {}", filename);

        try {
            createThumbnail(destination, filename);
        } catch (IOException e) {
            log.error("Failed to create thumbnail for {}", filename, e);
            Files.deleteIfExists(destination);
            throw new IOException("Failed to create thumbnail", e);
        }

        return filename;
    }

    private void createThumbnail(Path originalPath, String filename) throws IOException {
        Path thumbnailPath = Path.of(thumbnailDir, "thumb_" + filename);
        Thumbnails.of(originalPath.toFile())
                  .size(thumbnailWidth, thumbnailHeight)
                  .keepAspectRatio(true)
                  .outputQuality(0.85)
                  .toFile(thumbnailPath.toFile());
        log.debug("Thumbnail created: thumb_{}", filename);
    }

    public byte[] loadImage(String filename) throws IOException {
        Path path = Path.of(uploadDir, filename);
        if (!Files.exists(path)) throw new ImageNotFoundException("Image not found: " + filename);
        return Files.readAllBytes(path);
    }

    public byte[] loadThumbnail(String filename) throws IOException {
        Path path = Path.of(thumbnailDir, "thumb_" + filename);
        if (!Files.exists(path)) throw new ImageNotFoundException("Thumbnail not found: thumb_" + filename);
        return Files.readAllBytes(path);
    }

    public void deleteImage(String filename) throws IOException {
        if (filename == null || filename.isEmpty()) {
            log.warn("Attempted to delete null/empty filename");
            return;
        }

        deleteFile(Path.of(uploadDir, filename), "image");
        deleteFile(Path.of(thumbnailDir, "thumb_" + filename), "thumbnail");
    }

    private void deleteFile(Path path, String type) throws IOException {
        boolean deleted = Files.deleteIfExists(path);
        if (deleted) log.debug("{} deleted: {}", type, path.getFileName());
        else log.warn("{} not found for deletion: {}", type, path.getFileName());
    }

    public boolean imageExists(String filename) {
        return filename != null && !filename.isEmpty() && Files.exists(Path.of(uploadDir, filename));
    }

    public long getImageSize(String filename) throws IOException {
        Path path = Path.of(uploadDir, filename);
        if (!Files.exists(path)) throw new ImageNotFoundException("Image not found: " + filename);
        return Files.size(path);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) throw new IllegalArgumentException("File is empty");
        if (file.getSize() > maxFileSize)
            throw new IllegalArgumentException("File exceeds max size of " + (maxFileSize / 1024 / 1024) + " MB");

        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) throw new IllegalArgumentException("Invalid filename");

        String ext = getFileExtension(filename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext))
            throw new IllegalArgumentException("Invalid file type: " + ext);

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase()))
            throw new IllegalArgumentException("File must be an image (JPEG, PNG, GIF, WebP)");
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex < 0) throw new IllegalArgumentException("Filename has no extension");
        return filename.substring(dotIndex + 1);
    }

    private String generateUniqueFilename(String originalFilename, Long productId) {
        originalFilename = originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
        String ext = getFileExtension(originalFilename);
        return String.format("%d_%s.%s", productId, UUID.randomUUID(), ext);
    }

    public String getContentType(String filename) {
        return switch (getFileExtension(filename).toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }

    public static class ImageNotFoundException extends IOException {
        public ImageNotFoundException(String message) { super(message); }
    }
}