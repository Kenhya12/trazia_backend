package com.trazia.trazia_project.service;

import com.trazia.trazia_project.ProductMapperTestUtils;
import com.trazia.trazia_project.dto.product.NutrimentsDTO;
import com.trazia.trazia_project.dto.product.ProductDTO;
import com.trazia.trazia_project.dto.recipe.RecipeIngredientRequest;
import com.trazia.trazia_project.dto.recipe.RecipeRequest;
import com.trazia.trazia_project.dto.recipe.RecipeResponse;
import com.trazia.trazia_project.entity.product.Product;
import com.trazia.trazia_project.entity.product.ProductNutriments;
import com.trazia.trazia_project.entity.recipe.Recipe;
import com.trazia.trazia_project.entity.recipe.RecipeIngredient;
import com.trazia.trazia_project.mapper.ProductMapper;
import com.trazia.trazia_project.repository.ProductRepository;
import com.trazia.trazia_project.repository.RecipeIngredientRepository;
import com.trazia.trazia_project.repository.RecipeRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.ArgumentMatchers.any;

public class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeIngredientRepository recipeIngredientRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private NutritionConversionService nutritionConversionService;

    @InjectMocks
    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateRecipeSuccessfully() {
        Product product = Product.builder()
                .id(1L)
                .name("Pan integral")
                .costPerUnit(5.0)
                .nutriments(ProductMapperTestUtils.createSampleProductNutriments())
                .build();

        NutrimentsDTO nutrimentsDTO = NutrimentsDTO.builder()
                .protein(10.0)
                .calories(250.0)
                .build();

        when(productMapper.toNutrimentsDTO(product.getNutriments())).thenReturn(nutrimentsDTO);

        RecipeIngredientRequest ingredientRequest = RecipeIngredientRequest.builder()
                .productId(product.getId())
                .quantityGrams(200)
                .build();

        RecipeRequest recipeRequest = RecipeRequest.builder()
                .name("Desayuno saludable")
                .yieldWeightGrams(400.0)
                .ingredients(List.of(ingredientRequest))
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        doAnswer(invocation -> invocation.getArgument(0))
                .when(recipeIngredientRepository).save(any(RecipeIngredient.class));

        Recipe savedRecipe = Recipe.builder()
                .id(1L)
                .name(recipeRequest.getName())
                .yieldWeightGrams(recipeRequest.getYieldWeightGrams())
                .ingredients(List.of(
                        RecipeIngredient.builder()
                                .product(product)
                                .quantityGrams(BigDecimal.valueOf(ingredientRequest.getQuantityGrams()))
                                .build()))
                .build();

        when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);

        ProductDTO productDTO = ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .build();

        when(productMapper.toProductDTO(product)).thenReturn(productDTO);

        RecipeResponse createdRecipe = recipeService.createRecipe(recipeRequest, 1L);

        assertEquals("Desayuno saludable", createdRecipe.getName());
        assertEquals(1, createdRecipe.getIngredients().size());
        assertEquals("Pan integral", createdRecipe.getIngredients().get(0).getProduct().getName());
    }

    // =========================================
    // Tests adicionales para cobertura HU 5.1
    // =========================================

    @Test
    void shouldCalculatePerServing() {
        Recipe recipe = sampleRecipe();
        recipeService.calculatePerServing(recipe); // llamado desde RecipeService
        assertNotNull(recipe.getNutrimentsPor100g()); // asegurarse de que Recipe tiene getter/setter
        assertTrue(recipe.getNutrimentsPor100g().getCalories().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void shouldCalculateDailyValue() {
        Recipe recipe = sampleRecipe();
        ProductNutriments nutriments = recipe.getNutrimentsPor100g();
        ProductNutriments vd = recipeService.calculateDailyValue(nutriments);
        assertNotNull(vd);
        assertTrue(vd.getProtein().compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    void shouldFormatIngredientsList() {
        Recipe recipe = sampleRecipe();
        String formatted = recipeService.formatIngredientsList(recipe); // llamado desde RecipeService
        assertNotNull(formatted);
        assertTrue(formatted.contains("Harina"));
        assertTrue(formatted.contains("Azúcar"));
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        RecipeRequest request = sampleRecipeRequest();
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> recipeService.createRecipe(request, 1L));
    }

    @Test
    void shouldMapProductToDTO() {
        Product product = Product.builder()
                .id(1L)
                .name("Pan integral")
                .nutriments(ProductMapperTestUtils.createSampleProductNutriments())
                .build();

        NutrimentsDTO nutrimentsDTO = NutrimentsDTO.builder()
                .protein(10.0)
                .calories(250.0)
                .build();

        when(productMapper.toNutrimentsDTO(product.getNutriments())).thenReturn(nutrimentsDTO);
        ProductDTO dto = productMapper.toProductDTO(product);
        assertEquals("Pan integral", dto.getName());
    }

    // Métodos auxiliares
    private Recipe sampleRecipe() {
        Product product = Product.builder()
                .id(1L)
                .name("Harina")
                .nutriments(ProductMapperTestUtils.createSampleProductNutriments())
                .build();

        RecipeIngredient harina = RecipeIngredient.builder()
                .product(product)
                .quantityGrams(BigDecimal.valueOf(200))
                .build();

        RecipeIngredient azucar = RecipeIngredient.builder()
                .product(product)
                .quantityGrams(BigDecimal.valueOf(50))
                .build();

        Recipe recipe = new Recipe();
        recipe.setIngredients(List.of(harina, azucar));
        recipe.setNutrimentsPor100g(ProductMapperTestUtils.createSampleProductNutriments()); // agregar para getter
        return recipe;
    }

    private RecipeRequest sampleRecipeRequest() {
        RecipeIngredientRequest ingredientRequest = RecipeIngredientRequest.builder()
                .productId(1L)
                .quantityGrams(100)
                .build();

        return RecipeRequest.builder()
                .name("Receta de prueba")
                .yieldWeightGrams(200.0)
                .ingredients(List.of(ingredientRequest))
                .build();
    }
}