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

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

        // ==================== BÚSQUEDA BÁSICA ====================
        Optional<Product> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);

        Page<Product> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);

        // ==================== BÚSQUEDA POR NOMBRE ====================
        Page<Product> findByUserIdAndDeletedFalseAndNameContainingIgnoreCaseOrBrandContainingIgnoreCase(
                        Long userId, String name, String brand, Pageable pageable);

        // ==================== BÚSQUEDA POR CATEGORÍA ====================
        Page<Product> findByUserIdAndCategoryAndDeletedFalse(Long userId, ProductCategory category, Pageable pageable);

        Page<Product> findByUserIdAndCategoryAndNameContainingIgnoreCaseAndDeletedFalse(
                        Long userId, ProductCategory category, String name, Pageable pageable);

        // ==================== VALIDACIÓN DE EXISTENCIA ====================
        boolean existsByUserIdAndNameIgnoreCaseAndDeletedFalse(Long userId, String name);

        boolean existsByUserIdAndNameIgnoreCaseAndIdNotAndDeletedFalse(Long userId, String name, Long excludeId);

        // ==================== CONTADORES ====================
        long countByUserIdAndDeletedFalse(Long userId);

        long countByUserIdAndCategoryAndDeletedFalse(Long userId, ProductCategory category);

        // ==================== PAPELERA (SOFT DELETE) ====================
        Page<Product> findByUserIdAndDeletedTrue(Long userId, Pageable pageable);

        // ==================== EXPORTACIÓN Y LISTAS COMPLETAS ====================
        List<Product> findByUserIdAndDeletedFalse(Long userId);

        List<Product> findByUserIdAndDeletedFalseAndNutriments_CaloriesIsNotNullAndNutriments_ProteinIsNotNullAndNutriments_CarbohydratesIsNotNullAndNutriments_FatIsNotNull(
                        Long userId);

        // ==================== MÉTODOS NUEVOS PARA ERRORES ====================
        boolean existsByUserIdAndName(Long userId, String name);

        Optional<Product> findByIdAndUserId(Long id, Long userId);

        @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND p.user.id = :userId")
        Page<Product> searchByNameOrBrand(@Param("searchTerm") String searchTerm, @Param("userId") Long userId, Pageable pageable);
}