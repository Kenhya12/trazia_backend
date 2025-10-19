package com.trazia.trazia_project.service;

import com.trazia.trazia_project.dto.product.NutrimentsDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
    void shouldCalculateTotalCostAndNutritionPer100g() {
        // Preparar producto y nutrientes simulados
        Product product = new Product();
        product.setCostPerUnit(10.0); // €/kg

        NutrimentsDTO nutriments = NutrimentsDTO.builder()
                .protein(20.0)
                .carbohydrates(30.0)
                .fat(10.0)
                .build();

        when(productMapper.toNutrimentsDTO(product.getNutriments())).thenReturn(nutriments);
        when(nutritionConversionService.normalizeNutrients(nutriments, null, 100.0))
                .thenReturn(nutriments);
        when(nutritionConversionService.sumNutrients(nutriments, nutriments))
                .thenReturn(nutriments);
        when(nutritionConversionService.calculatePer100g(nutriments, 200.0))
                .thenReturn(nutriments);

        // Crear receta e ingredientes
        RecipeIngredient ingredient = RecipeIngredient.builder()
                .product(product)
                .quantityGrams(100.0)
                .build();

        Recipe recipe = Recipe.builder()
                .yieldWeightGrams(200.0)
                .ingredients(List.of(ingredient))
                .build();

        // Ejecutar método a testear
        var response = recipeService.buildRecipeResponse(recipe);

        // Validaciones
        assertEquals(1.0, response.getTotalCost(), 0.001); // 0.1 kg × 10 €/kg
        assertEquals(20.0, response.getCalculatedNutrition().getProtein());
        assertEquals(30.0, response.getCalculatedNutrition().getCarbohydrates());
        assertEquals(10.0, response.getCalculatedNutrition().getFat());
    }
}
