package com.trazia.trazia_project.repository.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.trazia.trazia_project.entity.product.Product;
import com.trazia.trazia_project.entity.product.ProductCategory;

/**
 * Repositorio de Productos con queries optimizadas
 * Todas las operaciones filtran por usuario y excluyen productos eliminados
 * (soft delete)
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

        // ==================== BÚSQUEDA BÁSICA ====================

        /**
         * Buscar producto por ID y usuario (validación de propiedad)
         * Excluye productos eliminados
         */
        @Query("SELECT p FROM Product p WHERE p.id = :id AND p.user.id = :userId AND p.deleted = false")
        Optional<Product> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

        /**
         * Buscar todos los productos activos de un usuario (paginado)
         */
        @Query("SELECT p FROM Product p WHERE p.user.id = :userId AND p.deleted = false")
        Page<Product> findByUserIdAndDeletedFalse(@Param("userId") Long userId, Pageable pageable);

        // ==================== BÚSQUEDA POR NOMBRE ====================

        /**
         * Buscar productos por nombre o marca (búsqueda parcial, case-insensitive)
         */
        @Query("SELECT p FROM Product p WHERE p.user.id = :userId AND p.deleted = false " +
                        "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')))")
        Page<Product> searchByNameOrBrand(@Param("query") String query,
                        @Param("userId") Long userId,
                        Pageable pageable);

        // ==================== BÚSQUEDA POR CATEGORÍA ====================

        /**
         * Buscar productos por categoría
         */
        @Query("SELECT p FROM Product p WHERE p.user.id = :userId " +
                        "AND p.category = :category " +
                        "AND p.deleted = false")
        Page<Product> findByUserIdAndCategoryAndDeletedFalse(@Param("userId") Long userId,
                        @Param("category") ProductCategory category,
                        Pageable pageable);

        /**
         * Buscar productos por nombre Y categoría (filtro combinado)
         */
        @Query("SELECT p FROM Product p WHERE p.user.id = :userId " +
                        "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
                        "AND p.category = :category " +
                        "AND p.deleted = false")
        Page<Product> findByUserIdAndNameAndCategory(@Param("userId") Long userId,
                        @Param("name") String name,
                        @Param("category") ProductCategory category,
                        Pageable pageable);

        // ==================== VALIDACIÓN DE EXISTENCIA ====================

        /**
         * Verificar si existe un producto con ese nombre para el usuario
         */
        @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
                        "FROM Product p WHERE p.user.id = :userId " +
                        "AND LOWER(p.name) = LOWER(:name) " +
                        "AND p.deleted = false")
        boolean existsByUserIdAndName(@Param("userId") Long userId, @Param("name") String name);

        /**
         * Verificar si existe otro producto con ese nombre (excluye un ID específico)
         * Útil para validación en edición
         */
        @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
                        "FROM Product p WHERE p.user.id = :userId " +
                        "AND LOWER(p.name) = LOWER(:name) " +
                        "AND p.id != :excludeId " +
                        "AND p.deleted = false")
        boolean existsByUserIdAndNameExcludingId(@Param("userId") Long userId,
                        @Param("name") String name,
                        @Param("excludeId") Long excludeId);

        // ==================== CONTADORES ====================

        /**
         * Contar productos totales del usuario (no eliminados)
         */
        @Query("SELECT COUNT(p) FROM Product p WHERE p.user.id = :userId AND p.deleted = false")
        long countByUserId(@Param("userId") Long userId);

        /**
         * Contar productos por categoría del usuario
         */
        @Query("SELECT COUNT(p) FROM Product p WHERE p.user.id = :userId " +
                        "AND p.category = :category " +
                        "AND p.deleted = false")
        long countByUserIdAndCategory(@Param("userId") Long userId,
                        @Param("category") ProductCategory category);

        // ==================== PAPELERA (SOFT DELETE) ====================

        /**
         * Obtener productos eliminados del usuario (papelera)
         */
        @Query("SELECT p FROM Product p WHERE p.user.id = :userId AND p.deleted = true")
        Page<Product> findDeletedByUserId(@Param("userId") Long userId, Pageable pageable);

        // ==================== EXPORTACIÓN Y LISTAS COMPLETAS ====================

        /**
         * Obtener todos los productos del usuario sin paginación
         * Útil para exportaciones o cálculos
         */
        @Query("SELECT p FROM Product p WHERE p.user.id = :userId AND p.deleted = false")
        List<Product> findAllByUserId(@Param("userId") Long userId);

        /**
         * Obtener productos con información nutricional completa
         * Útil para calculadora de recetas
         */
        @Query("SELECT p FROM Product p WHERE p.user.id = :userId " +
                        "AND p.deleted = false " +
                        "AND p.nutriments.calories IS NOT NULL " +
                        "AND p.nutriments.protein IS NOT NULL " +
                        "AND p.nutriments.carbohydrates IS NOT NULL " +
                        "AND p.nutriments.fat IS NOT NULL")
        List<Product> findByUserIdWithCompleteNutrition(@Param("userId") Long userId);
}
