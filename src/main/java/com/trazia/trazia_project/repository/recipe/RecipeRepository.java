package com.trazia.trazia_project.repository.recipe;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trazia.trazia_project.entity.recipe.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

        Optional<Recipe> findByIdAndUserId(Long id, Long userId);

        Page<Recipe> findByUserId(Long userId, Pageable pageable);

        Page<Recipe> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);

        Optional<Recipe> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);

        // Reemplaza searchByName con método derivado
        Page<Recipe> findByUserIdAndDeletedFalseAndNameContainingIgnoreCase(Long userId, String name,
                        Pageable pageable);

        // Reemplaza findByUserIdAndProductId con método derivado usando join con
        // colección de ingredientes
        List<Recipe> findDistinctByUserIdAndDeletedFalseAndIngredients_Product_Id(Long userId, Long productId);

        long countByUserIdAndDeletedFalse(Long userId);

        boolean existsByUserIdAndNameAndDeletedFalse(Long userId, String name);

        boolean existsByUserIdAndNameAndIdNotAndDeletedFalse(Long userId, String name, Long recipeId);
}