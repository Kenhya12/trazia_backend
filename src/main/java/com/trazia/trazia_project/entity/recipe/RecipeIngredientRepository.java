package com.trazia.trazia_project.entity.recipe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {

        /**
         * Encuentra todos los ingredientes de una receta específica
         * Ordenados por displayOrder
         */
        List<RecipeIngredient> findByRecipeIdOrderByDisplayOrderAsc(Long recipeId);

        /**
         * Encuentra un ingrediente específico de una receta
         */
        @Query("SELECT ri FROM RecipeIngredient ri " +
                        "WHERE ri.recipe.id = :recipeId " +
                        "AND ri.product.id = :productId")
        List<RecipeIngredient> findByRecipeIdAndProductId(@Param("recipeId") Long recipeId,
                        @Param("productId") Long productId);

        /**
         * Cuenta cuántos ingredientes tiene una receta
         */
        long countByRecipeId(Long recipeId);

        /**
         * Encuentra todos los ingredientes que usan un producto específico
         */
        List<RecipeIngredient> findByProductId(Long productId);

        /**
         * Verifica si un producto está siendo usado en alguna receta
         */
        boolean existsByProductId(Long productId);

        /**
         * Elimina todos los ingredientes de una receta
         */
        @Modifying
        @Query("DELETE FROM RecipeIngredient ri WHERE ri.recipe.id = :recipeId")
        void deleteByRecipeId(@Param("recipeId") Long recipeId);

        /**
         * Elimina un ingrediente específico de una receta
         */
        @Modifying
        @Query("DELETE FROM RecipeIngredient ri " +
                        "WHERE ri.recipe.id = :recipeId " +
                        "AND ri.product.id = :productId")
        void deleteByRecipeIdAndProductId(@Param("recipeId") Long recipeId,
                        @Param("productId") Long productId);
}
