package com.trazia.trazia_project.entity.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.trazia.trazia_project.entity.LabelingRegion;
import com.trazia.trazia_project.entity.User;
import com.trazia.trazia_project.entity.recipe.Recipe;
import com.trazia.trazia_project.entity.recipe.RecipeIngredient;

/**
 * Entidad para productos personalizados del usuario
 * Almacena información nutricional por 100g (estándar EU)
 * Soporta conversión a serving size para mercado US
 */
@Entity
@Table(name = "products", uniqueConstraints = @UniqueConstraint(name = "uk_user_product_name", columnNames = {
        "user_id", "name" }), indexes = {
                @Index(name = "idx_user_id", columnList = "user_id"),
                @Index(name = "idx_category", columnList = "category"),
                @Index(name = "idx_created_at", columnList = "created_at"),
                @Index(name = "idx_deleted", columnList = "deleted")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario creador del producto
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    /**
     * Lista de ingredientes de recetas que usan este producto
     * Relación bidireccional para rastrear el uso del producto en recetas
     */
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

    /**
     * Nombre del producto
     */
    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 150, message = "Product name must be between 3 and 150 characters")
    @Column(nullable = false, length = 150)
    private String name;

    /**
     * Descripción opcional del producto
     */
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(length = 1000)
    private String description;

    /**
     * Marca opcional del producto
     */
    @Size(max = 100, message = "Brand name cannot exceed 100 characters")
    @Column(length = 100)
    private String brand;

    /**
     * Alérgenos del producto (opcional, lista de strings)
     */
    @ElementCollection
    @CollectionTable(name = "product_allergens", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "allergen", length = 100)
    private List<String> allergens = new ArrayList<>();

    /**
     * Categoría del producto
     */
    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductCategory category;

    /**
     * NUEVOS CAMPOS PARA IMÁGENES
     */

    /**
     * Nombre del archivo de imagen almacenado
     * Ejemplo: "123_uuid.jpg"
     */
    @Column(name = "image_path", length = 255)
    private String imagePath;

    /**
     * Nombre del archivo thumbnail
     * Ejemplo: "thumb_123_uuid.jpg"
     */
    @Column(name = "thumbnail_path", length = 255)
    private String thumbnailPath;

    /**
     * URL pública de la imagen (generada dinámicamente)
     * No se persiste en BD, se calcula en runtime
     */
    @Transient
    private String imageUrl;

    /**
     * Información nutricional por 100g
     */
    @Embedded
    private ProductNutriments nutriments;

    /**
     * Tamaño de porción en gramos (opcional)
     * Usado para conversión de nutrimentos
     */
    @Column(name = "serving_size_grams")
    private Integer servingSizeGrams;

/**
 * Costo por unidad (por kg) del producto
 * Usado para calcular el costo total de recetas
 */
@Column(name = "cost_per_unit")
private Double costPerUnit;

    /**
     * Descripción opcional del tamaño de porción (ej: "1 taza", "1 rebanada")
     */
    @Size(max = 100, message = "Serving description cannot exceed 100 characters")
    @Column(name = "serving_description", length = 100)
    private String servingDescription;

    /**
     * Región de etiquetado nutricional
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "labeling_region", length = 10, nullable = false)
    private LabelingRegion labelingRegion = LabelingRegion.EU;

    /**
    * Indicador de eliminación lógica
    */
    @Builder.Default
    @Column(nullable = false)
    private Boolean deleted = false;

    /**
     * Timestamps de creación y actualización
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamps de creación y actualización
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (deleted == null) {
            deleted = false;
        }
        if (labelingRegion == null) {
            labelingRegion = LabelingRegion.EU;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void markAsDeleted() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void restore() {
        this.deleted = false;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean belongsToUser(Long userId) {
        return this.user != null && this.user.getId().equals(userId);
    }

    public ProductNutriments getNutrimentsPerServing() {
        if (nutriments == null) {
            return null;
        }
        if (servingSizeGrams != null && servingSizeGrams > 0) {
            return nutriments.convertToServingSize(servingSizeGrams);
        }
        return nutriments;
    }

    public boolean hasCompleteNutritionInfo() {
        if (nutriments == null) {
            return false;
        }
        return nutriments.getCalories() != null &&
                nutriments.getProtein() != null &&
                nutriments.getCarbohydrates() != null &&
                nutriments.getFat() != null;
    }

    /**
     * NUEVOS MÉTODOS PARA IMÁGENES
     */

    /**
     * Verifica si el producto tiene imagen
     */
    public boolean hasImage() {
        return imagePath != null && !imagePath.isEmpty();
    }

    /**
     * Actualiza la ruta de la imagen
     */
    public void updateImage(String newImagePath) {
        this.imagePath = newImagePath;
        this.thumbnailPath = "thumb_" + newImagePath;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Elimina la imagen del producto
     */
    public void removeImage() {
        this.imagePath = null;
        this.thumbnailPath = null;
        this.imageUrl = null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Genera la URL pública de la imagen
     */
    public void generateImageUrl(String baseUrl) {
        if (hasImage()) {
            this.imageUrl = baseUrl + "/api/products/" + this.id + "/image";
        }
    }

    /**
     * Verifica si el producto está siendo usado en alguna receta activa
     */
    public boolean isUsedInRecipes() {
        return recipeIngredients != null && !recipeIngredients.isEmpty();
    }

    /**
     * Cuenta cuántas recetas usan este producto
     */
    public int getRecipeUsageCount() {
        return recipeIngredients != null ? recipeIngredients.size() : 0;
    }

    /**
     * Verifica si se puede eliminar el producto
     * (solo si no está en uso o las recetas están marcadas como eliminadas)
     */
    public boolean canBeDeleted() {
        if (recipeIngredients == null || recipeIngredients.isEmpty()) {
            return true;
        }

        return recipeIngredients.stream()
                .map(RecipeIngredient::getRecipe)
                .allMatch(recipe -> recipe.getDeleted());
    }

    /**
     * Verifica si se puede eliminar el producto de forma segura
     * (solo si no está en uso en recetas activas)
     */
    public boolean canBeDeletedSafely() {
        if (recipeIngredients == null || recipeIngredients.isEmpty()) {
            return true;
        }

        // Permitir eliminación solo si todas las recetas que lo usan están eliminadas
        return recipeIngredients.stream()
                .map(RecipeIngredient::getRecipe)
                .allMatch(Recipe::getDeleted);
    }
}
