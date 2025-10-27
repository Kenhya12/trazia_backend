package com.trazia.trazia_project.entity.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.trazia.trazia_project.entity.company.LabelingRegion;
import com.trazia.trazia_project.entity.recipe.Recipe;
import com.trazia.trazia_project.entity.recipe.RecipeIngredient;
import com.trazia.trazia_project.entity.user.User;

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

    /** Usuario creador del producto */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    /**
     * Lista de ingredientes de recetas que usan este producto.
     * Relación inversa para saber en qué recetas está presente este producto.
     */
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

    /** Nombre del producto */
    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 150, message = "Product name must be between 3 and 150 characters")
    @Column(nullable = false, length = 150)
    private String name;

    /** Descripción opcional */
    @Size(max = 1000)
    @Column(length = 1000)
    private String description;

    /** Marca opcional */
    @Size(max = 100)
    @Column(length = 100)
    private String brand;

    /**
     * Alérgenos del producto.
     * @TODO: Normalizar a entidad propia en el futuro para evitar duplicados y facilitar búsquedas.
     */
    @ElementCollection
    @CollectionTable(name = "product_allergens", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "allergen", length = 100)
    @Builder.Default
    private List<String> allergens = new ArrayList<>();
    // TODO: Consider normalizing allergens to a separate entity to avoid duplicates and simplify searches.

    /** Categoría */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductCategory category;

    /** Imágenes */
    @Column(name = "image_path", length = 255)
    private String imagePath;

    @Column(name = "thumbnail_path", length = 255)
    private String thumbnailPath;

    @Transient
    private String imageUrl;

    /**
     * Información nutricional del producto.
     */
    @Embedded
    private ProductNutriments nutriments;

    /** Tamaño de porción en gramos */
    @Min(0)
    @Column(name = "serving_size_grams")
    private Integer servingSizeGrams;

    /** Costo por unidad (kg) */
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "cost_per_unit")
    private BigDecimal costPerUnit;

    /** Descripción del tamaño de porción */
    @Size(max = 100)
    @Column(name = "serving_description", length = 100)
    private String servingDescription;

    /** Región de etiquetado */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "labeling_region", length = 10, nullable = false)
    private LabelingRegion labelingRegion = LabelingRegion.EU;

    /** Eliminación lógica */
    @Builder.Default
    @Column(nullable = false)
    private Boolean deleted = false;

    /** Timestamps */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ---------- MÉTODOS ----------

    /**
     * Marca el producto como eliminado lógicamente.
     */
    public void markAsDeleted() {
        this.deleted = true;
        // El timestamp se actualizará automáticamente por @UpdateTimestamp
    }

    /**
     * Restaura el producto eliminando la marca de eliminado.
     */
    public void restore() {
        this.deleted = false;
        // El timestamp se actualizará automáticamente por @UpdateTimestamp
    }

    /**
     * Verifica si el producto pertenece a un usuario dado.
     * @param userId ID del usuario
     * @return true si pertenece al usuario, false de lo contrario
     */
    public boolean belongsToUser(Long userId) {
        return this.user != null && this.user.getId().equals(userId);
    }

    /**
     * Obtiene los nutrimentos convertidos a tamaño de porción si aplica.
     * @return Optional con ProductNutriments convertido o vacío si no hay nutrimentos
     */
    public Optional<ProductNutriments> getNutrimentsPerServing() {
        return Optional.ofNullable(nutriments)
                .map(n -> (servingSizeGrams != null && servingSizeGrams > 0) ? n.convertToServingSize(servingSizeGrams) : n);
    }

    /**
     * Verifica si el producto tiene información nutricional completa.
     * @return true si tiene calorías, proteínas, carbohidratos y grasas definidos
     */
    public boolean hasCompleteNutritionInfo() {
        return nutriments != null &&
                nutriments.getCalories() != null &&
                nutriments.getProtein() != null &&
                nutriments.getCarbohydrates() != null &&
                nutriments.getFat() != null;
    }

    /**
     * Verifica si el producto tiene una imagen asociada.
     * @return true si tiene imagen, false de lo contrario
     */
    public boolean hasImage() {
        return imagePath != null && !imagePath.isBlank();
    }

    /**
     * Actualiza la ruta de la imagen y su thumbnail.
     * @param newImagePath nueva ruta de la imagen
     */
    public void updateImage(String newImagePath) {
        this.imagePath = newImagePath;
        this.thumbnailPath = "thumb_" + newImagePath;
        // El timestamp se actualizará automáticamente por @UpdateTimestamp
    }

    /**
     * Elimina la imagen y el thumbnail asociados al producto.
     */
    public void removeImage() {
        this.imagePath = null;
        this.thumbnailPath = null;
        this.imageUrl = null;
        // El timestamp se actualizará automáticamente por @UpdateTimestamp
    }

    /**
     * Obtiene la URL pública de la imagen del producto.
     * Si no se ha definido manualmente, la genera al vuelo a partir del id y baseUrl.
     * @param baseUrl URL base del servidor
     * @return URL pública de la imagen, o null si no hay imagen
     */
    public String getImageUrl(String baseUrl) {
        if (!hasImage() || baseUrl == null || id == null) {
            return null;
        }
        return baseUrl + "/api/products/" + this.id + "/image";
    }

    /**
     * Verifica si el producto está siendo usado en alguna receta activa.
     * @return true si está en uso, false si no
     */
    public boolean isUsedInRecipes() {
        return !recipeIngredients.isEmpty();
    }

    /**
     * Obtiene el número de recetas que usan este producto.
     * @return cantidad de recetas
     */
    public int getRecipeUsageCount() {
        return recipeIngredients.size();
    }

    /**
     * Verifica si el producto puede ser eliminado.
     * Solo si no está en uso o todas las recetas que lo usan están marcadas como eliminadas.
     * @return true si puede eliminarse, false de lo contrario
     */
    public boolean canBeDeleted() {
        return recipeIngredients.isEmpty() || recipeIngredients.stream()
                .map(RecipeIngredient::getRecipe)
                .allMatch(Recipe::getDeleted);
    }
}
