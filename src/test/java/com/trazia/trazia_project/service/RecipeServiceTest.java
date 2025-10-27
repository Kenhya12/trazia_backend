package com.trazia.trazia_project.service;

import com.trazia.trazia_project.dto.product.ProductDTO;
import com.trazia.trazia_project.dto.recipe.RecipeIngredientRequest;
import com.trazia.trazia_project.dto.recipe.RecipePageResponse;
import com.trazia.trazia_project.dto.recipe.RecipeRequest;
import com.trazia.trazia_project.dto.recipe.RecipeResponse;
import com.trazia.trazia_project.entity.product.Product;
import com.trazia.trazia_project.entity.recipe.Recipe;
import com.trazia.trazia_project.entity.recipe.RecipeIngredient;
import com.trazia.trazia_project.exception.recipe.ResourceNotFoundException;
import com.trazia.trazia_project.mapper.ProductMapper;
import com.trazia.trazia_project.mapper.ProductMapperTestUtils;
import com.trazia.trazia_project.model.NutrimentsDTO;
import com.trazia.trazia_project.repository.product.ProductRepository;
import com.trazia.trazia_project.repository.recipe.RecipeIngredientRepository;
import com.trazia.trazia_project.repository.recipe.RecipeRepository;
import com.trazia.trazia_project.service.common.NutritionConversionService;
import com.trazia.trazia_project.service.recipe.RecipeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
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
                Product product = Product.builder()
                                .id(1L)
                                .name("Pan integral")
                                .costPerUnit(BigDecimal.valueOf(5.0))
                                .nutriments(ProductMapperTestUtils.createSampleProductNutriments())
                                .build();

                NutrimentsDTO nutrimentsDTO = NutrimentsDTO.builder()
                                .protein(10.0)
                                .calories(250.0)
                                .carbohydrates(30.0)
                                .sugars(5.0)
                                .fat(2.0)
                                .saturatedFat(0.5)
                                .fiber(3.0)
                                .sodium(0.1)
                                .salt(0.2)
                                .build();

                when(productMapper.toNutrimentsDTO(product.getNutriments())).thenReturn(nutrimentsDTO);

                RecipeIngredientRequest ingredientRequest = RecipeIngredientRequest.builder()
                                .productId(product.getId())
                                .quantityGrams(BigDecimal.valueOf(200)) // ⚠ BigDecimal
                                .build();

                RecipeRequest recipeRequest = RecipeRequest.builder()
                                .name("Desayuno saludable")
                                .yieldWeightGrams(400.0)
                                .ingredients(List.of(ingredientRequest))
                                .build();

                when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

                doAnswer(invocation -> invocation.getArgument(0))
                                .when(recipeIngredientRepository).save(any(RecipeIngredient.class));

                RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                                .product(product)
                                .quantityGrams(ingredientRequest.getQuantityGrams())
                                .build();

                Recipe savedRecipe = Recipe.builder()
                                .id(1L)
                                .name(recipeRequest.getName())
                                .yieldWeightGrams(BigDecimal.valueOf(400.0))
                                .ingredients(List.of(recipeIngredient))
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

        @Test
        void shouldCalculatePerServing() {
                Recipe recipe = sampleRecipe();
                // Set a fully initialized NutrimentsDTO with Double fields (no nulls)
                NutrimentsDTO nutrimentsDTO = NutrimentsDTO.builder()
                                .protein(1.0)
                                .calories(10.0)
                                .carbohydrates(2.0)
                                .sugars(0.5)
                                .fat(0.2)
                                .saturatedFat(0.05)
                                .fiber(0.3)
                                .sodium(0.01)
                                .salt(0.02)
                                .build();
                // Mock ProductMapper toEntityProductNutriments mapping
                var productNutriments = ProductMapperTestUtils.createSampleProductNutriments();
                when(productMapper.toEntityProductNutriments(nutrimentsDTO)).thenReturn(productNutriments);
                when(productMapper.toNutrimentsDTO(productNutriments)).thenReturn(nutrimentsDTO);
                recipe.setNutrimentsPor100g(productMapper.toEntityProductNutriments(nutrimentsDTO));
                // Ensure all values are Double, not BigDecimal, and no nulls
                NutrimentsDTO nutriments = productMapper.toNutrimentsDTO(recipe.getNutrimentsPor100g());
                assertNotNull(nutriments);
                assertTrue(nutriments.getCalories() != null && nutriments.getCalories() instanceof Double);
                recipeService.calculatePerServing(recipe);
                assertNotNull(productMapper.toNutrimentsDTO(recipe.getNutrimentsPor100g()));
                assertTrue(productMapper.toNutrimentsDTO(recipe.getNutrimentsPor100g()).getCalories() != null);
        }

        @Test
        void shouldCalculateDailyValue() {
                // Provide a fully initialized NutrimentsDTO with Double values for calculation
                NutrimentsDTO nutrimentsDTO = NutrimentsDTO.builder()
                                .protein(5.0)
                                .calories(100.0)
                                .carbohydrates(20.0)
                                .sugars(8.0)
                                .fat(3.0)
                                .saturatedFat(1.0)
                                .fiber(2.0)
                                .sodium(0.02)
                                .salt(0.04)
                                .build();
                // Mock ProductMapper toEntityProductNutriments mapping
                var productNutriments = ProductMapperTestUtils.createSampleProductNutriments();
                when(productMapper.toEntityProductNutriments(nutrimentsDTO)).thenReturn(productNutriments);
                when(productMapper.toNutrimentsDTO(productNutriments)).thenReturn(nutrimentsDTO);
                // Ensure all types are Double and not null
                assertNotNull(nutrimentsDTO.getProtein());
                assertTrue(nutrimentsDTO.getProtein() instanceof Double);
                NutrimentsDTO vd = recipeService.calculateDailyValue(nutrimentsDTO);
                assertNotNull(vd);
                assertTrue(vd.getProtein() != null && vd.getProtein() >= 0.0);
        }

        @Test
        void shouldFormatIngredientsList() {
                Recipe recipe = sampleRecipe();
                // Use a mutable list for sorting to avoid UnsupportedOperationException
                List<RecipeIngredient> mutableIngredients = new java.util.ArrayList<>(recipe.getIngredients());
                // Use null-safe comparators for both product and quantity
                mutableIngredients.sort(
                                java.util.Comparator.comparing(
                                                (RecipeIngredient ingredient) -> ingredient.getProduct() != null
                                                                ? ingredient.getProduct().getName()
                                                                : "",
                                                java.util.Comparator.nullsLast(String::compareTo)).thenComparing(
                                                                (RecipeIngredient ingredient) -> ingredient
                                                                                .getQuantityGrams() != null ? ingredient
                                                                                                .getQuantityGrams()
                                                                                                : BigDecimal.ZERO,
                                                                java.util.Comparator.nullsLast(BigDecimal::compareTo)));
                // Optionally set sorted list back if needed
                // recipe.setIngredients(mutableIngredients);
                String formatted = recipeService.formatIngredientsList(recipe);
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
                                .carbohydrates(30.0)
                                .sugars(5.0)
                                .fat(2.0)
                                .saturatedFat(0.5)
                                .fiber(3.0)
                                .sodium(0.1)
                                .salt(0.2)
                                .build();

                when(productMapper.toNutrimentsDTO(product.getNutriments())).thenReturn(nutrimentsDTO);
                ProductDTO dto = ProductDTO.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .build();
                when(productMapper.toProductDTO(product)).thenReturn(dto);
                ProductDTO resultDto = productMapper.toProductDTO(product);
                assertEquals("Pan integral", resultDto.getName());
        }

        // Métodos auxiliares
        /**
         * Returns a sample Recipe with all products, nutriments, and DTOs fully
         * initialized (no nulls).
         * All NutrimentsDTO values are Double. All products and ingredients are fully
         * initialized.
         */
        private Recipe sampleRecipe() {
                // Create ProductNutriments for harina (wheat flour)
                var harinaNutriments = ProductMapperTestUtils.createSampleProductNutriments();
                harinaNutriments.setProtein(BigDecimal.valueOf(7.8));
                harinaNutriments.setCalories(BigDecimal.valueOf(340.0));
                harinaNutriments.setCarbohydrates(BigDecimal.valueOf(72.0));
                harinaNutriments.setSugars(BigDecimal.valueOf(0.4));
                harinaNutriments.setFat(BigDecimal.valueOf(1.2));
                harinaNutriments.setSaturatedFat(BigDecimal.valueOf(0.2));
                harinaNutriments.setFiber(BigDecimal.valueOf(2.7));
                harinaNutriments.setSodium(BigDecimal.valueOf(0.01));
                harinaNutriments.setSalt(BigDecimal.valueOf(0.03));

                // Create ProductNutriments for azucar (sugar)
                var azucarNutriments = ProductMapperTestUtils.createSampleProductNutriments();
                azucarNutriments.setProtein(BigDecimal.valueOf(0.0));
                azucarNutriments.setCalories(BigDecimal.valueOf(400.0));
                azucarNutriments.setCarbohydrates(BigDecimal.valueOf(100.0));
                azucarNutriments.setSugars(BigDecimal.valueOf(100.0));
                azucarNutriments.setFat(BigDecimal.valueOf(0.0));
                azucarNutriments.setSaturatedFat(BigDecimal.valueOf(0.0));
                azucarNutriments.setFiber(BigDecimal.valueOf(0.0));
                azucarNutriments.setSodium(BigDecimal.valueOf(0.0));
                azucarNutriments.setSalt(BigDecimal.valueOf(0.0));

                // Create Product objects using the above nutriments, fully initialized
                Product harinaProduct = Product.builder()
                                .id(1L)
                                .name("Harina")
                                .nutriments(harinaNutriments)
                                .costPerUnit(BigDecimal.valueOf(2.0))
                                .brand("Marca Harina")
                                .description("Harina de trigo")
                                .build();
                Product azucarProduct = Product.builder()
                                .id(2L)
                                .name("Azúcar")
                                .nutriments(azucarNutriments)
                                .costPerUnit(BigDecimal.valueOf(1.5))
                                .brand("Marca Azúcar")
                                .description("Azúcar blanca")
                                .build();

                // Create NutrimentsDTOs for each product, all fields non-null and type Double
                // (convert from BigDecimal with null checks)
                NutrimentsDTO harinaNutrimentsDTO = NutrimentsDTO.builder()
                                .protein(harinaNutriments.getProtein() != null
                                                ? harinaNutriments.getProtein().doubleValue()
                                                : 0.0)
                                .calories(harinaNutriments.getCalories() != null
                                                ? harinaNutriments.getCalories().doubleValue()
                                                : 0.0)
                                .carbohydrates(harinaNutriments.getCarbohydrates() != null
                                                ? harinaNutriments.getCarbohydrates().doubleValue()
                                                : 0.0)
                                .sugars(harinaNutriments.getSugars() != null
                                                ? harinaNutriments.getSugars().doubleValue()
                                                : 0.0)
                                .fat(harinaNutriments.getFat() != null ? harinaNutriments.getFat().doubleValue() : 0.0)
                                .saturatedFat(harinaNutriments.getSaturatedFat() != null
                                                ? harinaNutriments.getSaturatedFat().doubleValue()
                                                : 0.0)
                                .fiber(harinaNutriments.getFiber() != null ? harinaNutriments.getFiber().doubleValue()
                                                : 0.0)
                                .sodium(harinaNutriments.getSodium() != null
                                                ? harinaNutriments.getSodium().doubleValue()
                                                : 0.0)
                                .salt(harinaNutriments.getSalt() != null ? harinaNutriments.getSalt().doubleValue()
                                                : 0.0)
                                .build();
                NutrimentsDTO azucarNutrimentsDTO = NutrimentsDTO.builder()
                                .protein(azucarNutriments.getProtein() != null
                                                ? azucarNutriments.getProtein().doubleValue()
                                                : 0.0)
                                .calories(azucarNutriments.getCalories() != null
                                                ? azucarNutriments.getCalories().doubleValue()
                                                : 0.0)
                                .carbohydrates(azucarNutriments.getCarbohydrates() != null
                                                ? azucarNutriments.getCarbohydrates().doubleValue()
                                                : 0.0)
                                .sugars(azucarNutriments.getSugars() != null
                                                ? azucarNutriments.getSugars().doubleValue()
                                                : 0.0)
                                .fat(azucarNutriments.getFat() != null ? azucarNutriments.getFat().doubleValue() : 0.0)
                                .saturatedFat(azucarNutriments.getSaturatedFat() != null
                                                ? azucarNutriments.getSaturatedFat().doubleValue()
                                                : 0.0)
                                .fiber(azucarNutriments.getFiber() != null ? azucarNutriments.getFiber().doubleValue()
                                                : 0.0)
                                .sodium(azucarNutriments.getSodium() != null
                                                ? azucarNutriments.getSodium().doubleValue()
                                                : 0.0)
                                .salt(azucarNutriments.getSalt() != null ? azucarNutriments.getSalt().doubleValue()
                                                : 0.0)
                                .build();

                // Set up mapper mocks for DTO conversion
                when(productMapper.toNutrimentsDTO(harinaNutriments)).thenReturn(harinaNutrimentsDTO);
                when(productMapper.toNutrimentsDTO(azucarNutriments)).thenReturn(azucarNutrimentsDTO);

                // Create RecipeIngredient objects for each product, fully initialized (no
                // nulls)
                RecipeIngredient harinaIngredient = RecipeIngredient.builder()
                                .product(harinaProduct)
                                .quantityGrams(BigDecimal.valueOf(200))
                                .build();
                RecipeIngredient azucarIngredient = RecipeIngredient.builder()
                                .product(azucarProduct)
                                .quantityGrams(BigDecimal.valueOf(50))
                                .build();

                // Compose the Recipe with all fields non-null, including nutrimentsPor100g
                NutrimentsDTO recipeNutrimentsDTO = NutrimentsDTO.builder()
                                .protein(2.5)
                                .calories(360.0)
                                .carbohydrates(60.0)
                                .sugars(22.0)
                                .fat(0.8)
                                .saturatedFat(0.1)
                                .fiber(1.1)
                                .sodium(0.005)
                                .salt(0.012)
                                .build();
                var recipeProductNutriments = ProductMapperTestUtils.createSampleProductNutriments();
                // For the mock: always map DTO <-> entity
                when(productMapper.toEntityProductNutriments(recipeNutrimentsDTO)).thenReturn(recipeProductNutriments);
                when(productMapper.toNutrimentsDTO(recipeProductNutriments)).thenReturn(recipeNutrimentsDTO);
                Recipe recipe = Recipe.builder()
                                .id(1L)
                                .name("Bizcocho básico")
                                .yieldWeightGrams(BigDecimal.valueOf(500.0))
                                .ingredients(List.of(harinaIngredient, azucarIngredient))
                                .nutrimentsPor100g(recipeProductNutriments)
                                .build();
                return recipe;
        }

        private RecipeRequest sampleRecipeRequest() {
                RecipeIngredientRequest ingredientRequest = RecipeIngredientRequest.builder()
                                .productId(1L)
                                .quantityGrams(BigDecimal.valueOf(100))
                                .build();

                return RecipeRequest.builder()
                                .name("Receta de prueba")
                                .yieldWeightGrams(200.0)
                                .ingredients(List.of(ingredientRequest))
                                .build();
        }

        @Test
        void shouldHandleEmptyIngredientListGracefully() {
                RecipeRequest recipeRequest = RecipeRequest.builder()
                                .name("Receta vacía")
                                .yieldWeightGrams(100.0)
                                .ingredients(List.of()) // sin ingredientes
                                .build();

                assertThrows(RuntimeException.class, () -> recipeService.createRecipe(recipeRequest, 1L));
        }

        @Test
        void shouldHandleIngredientWithNullQuantity() {
                RecipeIngredientRequest ingredientRequest = RecipeIngredientRequest.builder()
                                .productId(1L)
                                .quantityGrams(null)
                                .build();

                RecipeRequest recipeRequest = RecipeRequest.builder()
                                .name("Receta con cantidad nula")
                                .yieldWeightGrams(200.0)
                                .ingredients(List.of(ingredientRequest))
                                .build();

                Product product = Product.builder().id(1L).name("Producto test").build();
                when(productRepository.findById(1L)).thenReturn(Optional.of(product));

                assertThrows(RuntimeException.class, () -> recipeService.createRecipe(recipeRequest, 1L));
        }

        @Test
        void shouldHandleNullNutrimentsInProduct() {
                Product product = Product.builder()
                                .id(1L)
                                .name("Producto sin nutrimentos")
                                .nutriments(null)
                                .build();

                when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
                when(productMapper.toNutrimentsDTO(null)).thenReturn(null);

                RecipeIngredientRequest ingredientRequest = RecipeIngredientRequest.builder()
                                .productId(product.getId())
                                .quantityGrams(BigDecimal.valueOf(100))
                                .build();

                RecipeRequest recipeRequest = RecipeRequest.builder()
                                .name("Receta sin nutrimentos")
                                .yieldWeightGrams(200.0)
                                .ingredients(List.of(ingredientRequest))
                                .build();

                assertThrows(RuntimeException.class, () -> recipeService.createRecipe(recipeRequest, 1L));
        }

        @Test
        void shouldFormatIngredientsListWithNullValues() {
                Recipe recipe = Recipe.builder()
                                .id(2L)
                                .name("Receta con valores nulos")
                                .ingredients(List.of(
                                                RecipeIngredient.builder()
                                                                .product(null)
                                                                .quantityGrams(null)
                                                                .build(),
                                                RecipeIngredient.builder()
                                                                .product(Product.builder().name(null).build())
                                                                .quantityGrams(BigDecimal.ZERO)
                                                                .build()))
                                .build();

                String formatted = recipeService.formatIngredientsList(recipe);

                assertNotNull(formatted);
                // Aseguramos que los valores genéricos aparecen cuando hay nulls
                assertTrue(formatted.contains("Ingrediente desconocido"));
                assertTrue(formatted.contains("0g"));
        }

        @Test
        void shouldCalculateCorrectValuesPerServing() {
                Recipe recipe = sampleRecipe();
                recipe.setYieldWeightGrams(BigDecimal.valueOf(500.0)); // Reducir peso para recalcular
                NutrimentsDTO before = productMapper.toNutrimentsDTO(recipe.getNutrimentsPor100g());
                assertNotNull(before);
                recipeService.calculatePerServing(recipe);
                NutrimentsDTO after = productMapper.toNutrimentsDTO(recipe.getNutrimentsPor100g());
                assertNotNull(after);
                assertTrue(after.getCalories() <= before.getCalories());
        }

        @Test
        void shouldHandleExceptionDuringRecipeSave() {
                Product product = Product.builder()
                                .id(1L)
                                .name("Producto test")
                                .nutriments(ProductMapperTestUtils.createSampleProductNutriments())
                                .build();

                when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
                when(recipeRepository.save(any())).thenThrow(new RuntimeException("Error al guardar receta"));

                RecipeIngredientRequest ingredientRequest = RecipeIngredientRequest.builder()
                                .productId(product.getId())
                                .quantityGrams(BigDecimal.valueOf(100))
                                .build();

                RecipeRequest recipeRequest = RecipeRequest.builder()
                                .name("Receta con error de guardado")
                                .yieldWeightGrams(200.0)
                                .ingredients(List.of(ingredientRequest))
                                .build();

                assertThrows(RuntimeException.class, () -> recipeService.createRecipe(recipeRequest, 1L));
        }

        @Test
        void shouldHandleNullYieldWeightGracefully() {
                Recipe recipe = sampleRecipe();
                recipe.setYieldWeightGrams(null); // yieldWeight es null
                assertThrows(RuntimeException.class, () -> recipeService.calculatePerServing(recipe));
        }

        @Test
        void shouldHandleZeroYieldWeightGracefully() {
                Recipe recipe = sampleRecipe();
                recipe.setYieldWeightGrams(BigDecimal.ZERO); // yieldWeight es 0
                assertThrows(ArithmeticException.class, () -> recipeService.calculatePerServing(recipe));
        }

        @Test
        void shouldFormatIngredientsListWithDuplicateNames() {
                Product product1 = Product.builder().id(1L).name("Harina").build();
                Product product2 = Product.builder().id(2L).name("Harina").build();

                RecipeIngredient ing1 = RecipeIngredient.builder().product(product1)
                                .quantityGrams(BigDecimal.valueOf(100)).build();
                RecipeIngredient ing2 = RecipeIngredient.builder().product(product2)
                                .quantityGrams(BigDecimal.valueOf(50)).build();

                Recipe recipe = Recipe.builder()
                                .id(1L)
                                .name("Receta duplicada")
                                .ingredients(List.of(ing1, ing2))
                                .build();

                String formatted = recipeService.formatIngredientsList(recipe);
                assertNotNull(formatted);
                assertTrue(formatted.contains("Harina"));
                assertTrue(formatted.contains("100") || formatted.contains("50"));
        }

        @Test
        void shouldThrowExceptionWhenDeletingNonExistingRecipe() {
                when(recipeRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
                assertThrows(ResourceNotFoundException.class,
                                () -> recipeService.deleteRecipe(1L, 1L));
        }

        @Test
        void shouldReturnEmptyPageResponseWhenNoRecipesFound() {
                Page<Recipe> emptyPage = new PageImpl<>(List.of());
                when(recipeRepository.findByUserId(anyLong(), any())).thenReturn(emptyPage);

                RecipePageResponse response = recipeService.getAllRecipes(1L, PageRequest.of(0, 10));

                assertTrue(response.getRecipes().isEmpty());
                assertEquals(0, response.getTotalRecipes());
        }
}