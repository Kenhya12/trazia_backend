package com.trazia.trazia_project.entity.recipe;

import com.trazia.trazia_project.entity.product.Product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * Entidad que representa un ingrediente dentro de una receta
 * Vincula una receta con un producto (insumo) y especifica la cantidad usada
 */
@Entity
@Table(name = "recipe_ingredients",
    indexes = {
        @Index(name = "idx_recipe_ingredient_recipe_id", columnList = "recipe_id"),
        @Index(name = "idx_recipe_ingredient_product_id", columnList = "product_id")
    })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Receta a la que pertenece este ingrediente
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id", nullable = false)
    @ToString.Exclude
    private Recipe recipe;

    /**
     * Producto base (insumo) utilizado como ingrediente
     * Este producto contiene la información nutricional y de costo
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    private Product product;

    /**
     * Cantidad del ingrediente en gramos
     */
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    @Column(name = "quantity_grams", nullable = false)
    private Integer quantityGrams;

    /**
     * Nota opcional sobre el ingrediente (ej: "picado fino", "opcional")
     */
    @Column(length = 200)
    private String note;

    /**
     * Orden de aparición en la lista de ingredientes
     */
    @Column(name = "display_order")
    private Integer displayOrder;

    // Métodos de negocio

    /**
     * Verifica si este ingrediente pertenece a una receta específica
     */
    public boolean belongsToRecipe(Long recipeId) {
        return this.recipe != null && this.recipe.getId().equals(recipeId);
    }

    /**
     * Verifica si este ingrediente usa un producto específico
     */
    public boolean usesProduct(Long productId) {
        return this.product != null && this.product.getId().equals(productId);
    }
}
