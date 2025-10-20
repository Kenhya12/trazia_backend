package com.trazia.trazia_project.service;

import com.trazia.trazia_project.ProductMapperTestUtils;
import com.trazia.trazia_project.dto.product.NutrimentsDTO;
import com.trazia.trazia_project.dto.product.ProductDTO;
import com.trazia.trazia_project.dto.recipe.RecipeIngredientRequest;
import com.trazia.trazia_project.dto.recipe.RecipeRequest;
import com.trazia.trazia_project.dto.recipe.RecipeResponse;
import com.trazia.trazia_project.entity.product.Product;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

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
                // Preparar producto con nutriments por defecto
                Product product = Product.builder()
                                .id(1L)
                                .name("Pan integral")
                                .costPerUnit(5.0)
                                .nutriments(ProductMapperTestUtils.createSampleProductNutriments())
                                .build();

                // Preparar NutrimentsDTO con valores por defecto
                NutrimentsDTO nutrimentsDTO = NutrimentsDTO.builder()
                                .protein(10.0)
                                .carbohydrates(50.0)
                                .fat(5.0)
                                .sugars(5.0)
                                .saturatedFat(2.0)
                                .fiber(6.0)
                                .sodium(0.5)
                                .salt(1.2)
                                .calories(250.0)
                                .build();

                // Mockear mapper de Product a NutrimentsDTO
                when(productMapper.toNutrimentsDTO(product.getNutriments())).thenReturn(nutrimentsDTO);

                // Ingrediente request
                RecipeIngredientRequest ingredientRequest = RecipeIngredientRequest.builder()
                                .productId(product.getId())
                                .quantityGrams(200)
                                .build();

                // RecipeRequest con lista de ingredientes
                RecipeRequest recipeRequest = RecipeRequest.builder()
                                .name("Desayuno saludable")
                                .yieldWeightGrams(400.0)
                                .ingredients(List.of(ingredientRequest))
                                .build();

                // Mockear repositorios
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
                                                                .quantityGrams(ingredientRequest.getQuantityGrams())
                                                                .build()))
                                .build();

                when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);

                // Mockear ProductMapper para RecipeIngredientResponse
                ProductDTO productDTO = ProductDTO.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .build();

                when(productMapper.toProductDTO(product)).thenReturn(productDTO);

                // Ejecutar método de creación
                RecipeResponse createdRecipe = recipeService.createRecipe(recipeRequest, 1L);

                // Validaciones
                assertEquals("Desayuno saludable", createdRecipe.getName());
                assertEquals(1, createdRecipe.getIngredients().size());
                assertEquals("Pan integral", createdRecipe.getIngredients().get(0).getProduct().getName());
        }
}