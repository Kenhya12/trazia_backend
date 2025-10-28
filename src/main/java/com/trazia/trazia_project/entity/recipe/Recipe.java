package com.trazia.trazia_project.entity.recipe;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.trazia.trazia_project.entity.batch.FinalProductLot;
import com.trazia.trazia_project.entity.product.ProductNutriments;
import com.trazia.trazia_project.entity.user.User;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String processingType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @NotBlank(message = "Recipe name is required")
    @Size(min = 3, max = 150)
    @Column(nullable = false, length = 150)
    private String name;

    @Size(max = 2000)
    private String description;

    private String usageInstructions;

    @NotNull
    @Positive
    @Column(name = "yield_weight_grams", nullable = false, precision = 10, scale = 2)
    private BigDecimal yieldWeightGrams;

    @Size(max = 100)
    @Column(name = "yield_description", length = 100)
    private String yieldDescription;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    /** 🔹 Nueva relación — una receta puede tener varios lotes finales */
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FinalProductLot> finalProductLots = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    private Boolean deleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Embedded
    private ProductNutriments nutrimentsPor100g;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (deleted == null)
            deleted = false;
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

    public void addIngredient(RecipeIngredient ingredient) {
        if (ingredient != null) {
            ingredients.add(ingredient);
            ingredient.setRecipe(this);
        }
    }

    public void removeIngredient(RecipeIngredient ingredient) {
        if (ingredient != null) {
            ingredients.remove(ingredient);
            ingredient.setRecipe(null);
        }
    }

    public BigDecimal getTotalIngredientsWeight() {
        if (ingredients == null)
            return BigDecimal.ZERO;
        return ingredients.stream()
                .map(i -> i.getQuantityGrams() != null ? i.getQuantityGrams() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean hasIngredients() {
        return ingredients != null && !ingredients.isEmpty();
    }

    public String getUsageInstructionsSafe() {
        return usageInstructions != null ? usageInstructions : "";
    }

    /**
     * Devuelve un lote final representativo de la receta.
     * Si no hay lotes, devuelve null.
     */
    public FinalProductLot getFinalProductLotSafe() {
        return (finalProductLots != null && !finalProductLots.isEmpty())
                ? finalProductLots.get(0)
                : null;
    }

    public List<FinalProductLot> getFinalProductLotsSafe() {
        return finalProductLots != null ? finalProductLots : new ArrayList<>();
    }
}