package com.trazia.trazia_project.entity.recipe;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.trazia.trazia_project.entity.batch.FinalProductLot;
import com.trazia.trazia_project.entity.product.ProductNutriments;
import com.trazia.trazia_project.entity.user.User;

/**
 * Entidad para recetas personalizadas del usuario
 * Almacena ingredientes, rendimiento y permite cálculos nutricionales
 */
@Entity
@Table(name = "recipes", uniqueConstraints = @UniqueConstraint(name = "uk_user_recipe_name", columnNames = { "user_id",
        "name" }), indexes = {
                @Index(name = "idx_recipe_user_id", columnList = "user_id"),
                @Index(name = "idx_recipe_created_at", columnList = "created_at"),
                @Index(name = "idx_recipe_deleted", columnList = "deleted")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {
    private String processingType;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario creador de la receta
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @NotBlank(message = "Recipe name is required")
    @Size(min = 3, max = 150, message = "Recipe name must be between 3 and 150 characters")
    @Column(nullable = false, length = 150)
    private String name;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    @Column(length = 2000)
    private String description;

    // NUEVOS CAMPOS
    private String usageInstructions;          // Modo de empleo
    private FinalProductLot finalProductLot;

    /**
     * Peso del rendimiento final de la receta en gramos
     * Ejemplo: Una receta puede usar 500g de ingredientes y rendir 450g de producto
     * final
     */
    @NotNull(message = "Yield weight is required")
    @Positive(message = "Yield weight must be positive")
    @Column(name = "yield_weight_grams", nullable = false, precision = 10, scale = 2)
    private BigDecimal yieldWeightGrams;

    /**
     * Descripción opcional del rendimiento (ej: "8 porciones", "12 galletas")
     */
    @Size(max = 100, message = "Yield description cannot exceed 100 characters")
    @Column(name = "yield_description", length = 100)
    private String yieldDescription;

    /**
     * Lista de ingredientes de la receta
     */
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    private Boolean deleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (deleted == null) {
            deleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Métodos de negocio

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

    /**
     * Añade un ingrediente a la receta
     */
    public void addIngredient(RecipeIngredient ingredient) {
        if (ingredient != null) {
            if (ingredients == null) {
                ingredients = new ArrayList<>();
            }
            ingredients.add(ingredient);
            ingredient.setRecipe(this);
        }
    }

    /**
     * Elimina un ingrediente de la receta
     */
    public void removeIngredient(RecipeIngredient ingredient) {
        if (ingredient != null && ingredients != null) {
            ingredients.remove(ingredient);
            ingredient.setRecipe(null);
        }
    }

    /**
     * Calcula el peso total de ingredientes
     */
    public BigDecimal getTotalIngredientsWeight() {
        if (ingredients == null) {
            return BigDecimal.ZERO;
        }
        return ingredients.stream()
                .map(i -> i.getQuantityGrams() != null ? i.getQuantityGrams() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    };

    /**
     * Verifica si la receta tiene ingredientes
     */
    public boolean hasIngredients() {
        return ingredients != null && !ingredients.isEmpty();
    }

    @Embedded
    private ProductNutriments nutrimentsPor100g;

    public String getUsageInstructionsSafe() {
        return usageInstructions != null ? usageInstructions : "";
    }

    public FinalProductLot getFinalProductLotSafe() {
        return finalProductLot != null ? finalProductLot : null;
    }
}
