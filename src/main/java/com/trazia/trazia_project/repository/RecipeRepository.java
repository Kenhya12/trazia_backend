package com.trazia.trazia_project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.trazia.trazia_project.entity.recipe.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

        /**
         * Encuentra todas las recetas de un usuario (no eliminadas)
         */
        Page<Recipe> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);

        /**
         * Encuentra una receta específica de un usuario (no eliminada)
         */
        Optional<Recipe> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);

        /**
         * Busca recetas por nombre (parcial, insensible a mayúsculas)
         */
        @Query("SELECT r FROM Recipe r WHERE r.user.id = :userId " +
                        "AND r.deleted = false " +
                        "AND LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))")
        Page<Recipe> searchByName(@Param("userId") Long userId,
                        @Param("name") String name,
                        Pageable pageable);

        /**
         * Encuentra recetas que contengan un producto específico
         */
        @Query("SELECT DISTINCT r FROM Recipe r " +
                        "JOIN r.ingredients i " +
                        "WHERE r.user.id = :userId " +
                        "AND r.deleted = false " +
                        "AND i.product.id = :productId")
        List<Recipe> findByUserIdAndProductId(@Param("userId") Long userId,
                        @Param("productId") Long productId);

        /**
         * Cuenta las recetas activas de un usuario
         */
        long countByUserIdAndDeletedFalse(Long userId);

        /**
         * Verifica si existe una receta con el mismo nombre para un usuario
         */
        boolean existsByUserIdAndNameAndDeletedFalse(Long userId, String name);

        /**
         * Verifica si existe otra receta con el mismo nombre (para actualización)
         */
        @Query("SELECT COUNT(r) > 0 FROM Recipe r " +
                        "WHERE r.user.id = :userId " +
                        "AND r.name = :name " +
                        "AND r.id != :recipeId " +
                        "AND r.deleted = false")
        boolean existsByUserIdAndNameAndIdNot(@Param("userId") Long userId,
                        @Param("name") String name,
                        @Param("recipeId") Long recipeId);
}
