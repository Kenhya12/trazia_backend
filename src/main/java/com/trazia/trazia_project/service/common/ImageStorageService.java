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

    @Value("${file.max-size:5242880}") // 5MB por defecto
    private long maxFileSize;

    @Value("${thumbnail.width:200}")
    private int thumbnailWidth;

    @Value("${thumbnail.height:200}")
    private int thumbnailHeight;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp");

    /**
     * Crea los directorios necesarios al iniciar el servicio
     */
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Path.of(uploadDir));
            Files.createDirectories(Path.of(thumbnailDir));
            log.info("Image storage directories created: {} and {}", uploadDir, thumbnailDir);
        } catch (IOException e) {
            log.error("Failed to create storage directories", e);
            throw new RuntimeException("Could not initialize storage directories", e);
        }
    }

    /**
     * Almacena una imagen y genera su thumbnail
     * 
     * @param file      Archivo MultipartFile subido
     * @param productId ID del producto para nombre único
     * @return Nombre del archivo guardado
     * @throws IOException              Si hay error en el almacenamiento
     * @throws IllegalArgumentException Si el archivo no es válido
     */
    public String storeImage(MultipartFile file, Long productId) throws IOException {
        log.debug("Storing image for product ID: {}", productId);

        // Validar archivo
        validateFileSizeAndType(file);

        // Generar nombre único
        String uniqueFilename = generateUniqueFilename(file.getOriginalFilename(), productId);

        // Guardar imagen original
        Path destinationFile = Path.of(uploadDir, uniqueFilename);
        Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
        log.info("Image saved: {}", uniqueFilename);

        // Crear thumbnail
        try {
            createThumbnail(destinationFile, uniqueFilename);
        } catch (IOException e) {
            log.error("Failed to create thumbnail for: {}", uniqueFilename, e);
            // Eliminar imagen original si falla el thumbnail
            Files.deleteIfExists(destinationFile);
            throw new IOException("Failed to create thumbnail", e);
        }

        return uniqueFilename;
    }

    /**
     * Crea un thumbnail de la imagen usando Thumbnailator
     * 
     * @param originalPath Path de la imagen original
     * @param filename     Nombre del archivo
     * @throws IOException Si hay error al crear thumbnail
     */
    private void createThumbnail(Path originalPath, String filename) throws IOException {
        Path thumbnailPath = Path.of(thumbnailDir, "thumb_" + filename);

        Thumbnails.of(originalPath.toFile())
                .size(thumbnailWidth, thumbnailHeight)
                .keepAspectRatio(true)
                .outputQuality(0.85)
                .toFile(thumbnailPath.toFile());

        log.debug("Thumbnail created: thumb_{}", filename);
    }

    /**
     * Carga una imagen desde el filesystem
     * 
     * @param filename Nombre del archivo
     * @return Bytes de la imagen
     * @throws IOException Si el archivo no existe
     */
    public byte[] loadImage(String filename) throws IOException {
        log.debug("Loading image: {}", filename);

        Path filePath = Path.of(uploadDir, filename);

        if (!Files.exists(filePath)) {
            log.warn("Image not found: {}", filename);
            throw new ImageNotFoundException("Image not found: " + filename);
        }

        return Files.readAllBytes(filePath);
    }

    /**
     * Carga un thumbnail desde el filesystem
     * 
     * @param filename Nombre del archivo original
     * @return Bytes del thumbnail
     * @throws IOException Si el thumbnail no existe
     */
    public byte[] loadThumbnail(String filename) throws IOException {
        log.debug("Loading thumbnail: thumb_{}", filename);

        Path thumbnailPath = Path.of(thumbnailDir, "thumb_" + filename);

        if (!Files.exists(thumbnailPath)) {
            log.warn("Thumbnail not found: thumb_{}", filename);
            throw new ImageNotFoundException("Thumbnail not found: thumb_" + filename);
        }

        return Files.readAllBytes(thumbnailPath);
    }

    /**
     * Elimina una imagen y su thumbnail
     * 
     * @param filename Nombre del archivo
     * @throws IOException Si hay error al eliminar
     */
    public void deleteImage(String filename) throws IOException {
        if (filename == null || filename.isEmpty()) {
            log.warn("Attempted to delete null or empty filename");
            return;
        }

        log.info("Deleting image: {}", filename);

        // Eliminar imagen original
        Path imagePath = Path.of(uploadDir, filename);
        boolean imageDeleted = Files.deleteIfExists(imagePath);

        if (imageDeleted) {
            log.debug("Image deleted: {}", filename);
        } else {
            log.warn("Image not found for deletion: {}", filename);
        }

        // Eliminar thumbnail
        Path thumbnailPath = Path.of(thumbnailDir, "thumb_" + filename);
        boolean thumbnailDeleted = Files.deleteIfExists(thumbnailPath);

        if (thumbnailDeleted) {
            log.debug("Thumbnail deleted: thumb_{}", filename);
        } else {
            log.warn("Thumbnail not found for deletion: thumb_{}", filename);
        }
    }

    /**
     * Verifica si existe una imagen
     * 
     * @param filename Nombre del archivo
     * @return true si existe, false si no
     */
    public boolean imageExists(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        Path filePath = Path.of(uploadDir, filename);
        return Files.exists(filePath);
    }

    /**
     * Obtiene el tamaño de una imagen en bytes
     * 
     * @param filename Nombre del archivo
     * @return Tamaño en bytes
     * @throws IOException Si el archivo no existe
     */
    public long getImageSize(String filename) throws IOException {
        Path filePath = Path.of(uploadDir, filename);
        if (!Files.exists(filePath)) {
            throw new ImageNotFoundException("Image not found: " + filename);
        }
        return Files.size(filePath);
    }

    /**
     * Valida el archivo subido
     * 
     * @param file Archivo a validar
     * @throws IllegalArgumentException Si el archivo no es válido
     */
    private void validateFileSizeAndType(MultipartFile file) {
        // Validar que no esté vacío
        if (file.isEmpty()) {
            log.warn("Upload attempt with empty file");
            throw new IllegalArgumentException("File is empty");
        }

        // Validar tamaño
        if (file.getSize() > maxFileSize) {
            log.warn("File size {} exceeds maximum {}", file.getSize(), maxFileSize);
            throw new IllegalArgumentException(
                    String.format("File size exceeds maximum allowed size of %d MB", maxFileSize / 1024 / 1024));
        }

        // Validar extensión
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Invalid filename");
        }

        String extension = getFileExtension(filename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            log.warn("Invalid file extension: {}", extension);
            throw new IllegalArgumentException(
                    "Invalid file type. Allowed types: " + String.join(", ", ALLOWED_EXTENSIONS));
        }

        // Validar MIME type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            log.warn("Invalid content type: {}", contentType);
            throw new IllegalArgumentException(
                    "Invalid file type. File must be an image (JPEG, PNG, GIF, WebP)");
        }
    }

    /**
     * Extrae la extensión del archivo
     * 
     * @param filename Nombre del archivo
     * @return Extensión sin el punto
     * @throws IllegalArgumentException Si no tiene extensión
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("Invalid filename: no extension found");
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * Genera un nombre único para el archivo
     * 
     * @param originalFilename Nombre original del archivo
     * @param productId        ID del producto
     * @return Nombre único generado
     */
    private String generateUniqueFilename(String originalFilename, Long productId) {
        // Sanitize filename before extracting extension
        originalFilename = originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
        String extension = getFileExtension(originalFilename);
        return String.format("%d_%s.%s",
                productId,
                UUID.randomUUID().toString(),
                extension);
    }

    /**
     * Obtiene el tipo MIME basado en la extensión
     * 
     * @param filename Nombre del archivo
     * @return Tipo MIME
     */
    public String getContentType(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }

    public static class ImageNotFoundException extends IOException {
        public ImageNotFoundException(String message) {
            super(message);
        }
    }
}
