package com.trazia.trazia_project.service;

import com.trazia.trazia_project.entity.User;

import com.trazia.trazia_project.dto.product.NutrimentsDTO;
import com.trazia.trazia_project.dto.product.ProductDTO;
import com.trazia.trazia_project.dto.recipe.*;
import com.trazia.trazia_project.entity.product.Product;
import com.trazia.trazia_project.entity.recipe.Recipe;
import com.trazia.trazia_project.entity.recipe.RecipeIngredient;
import com.trazia.trazia_project.exception.ResourceNotFoundException;
import com.trazia.trazia_project.mapper.ProductMapper;
import com.trazia.trazia_project.repository.ProductRepository;
import com.trazia.trazia_project.repository.RecipeIngredientRepository;
import com.trazia.trazia_project.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeService {

        private final RecipeRepository recipeRepository;
        private final RecipeIngredientRepository recipeIngredientRepository;
        private final ProductRepository productRepository;
        private final ProductMapper productMapper;
        private final NutritionConversionService nutritionConversionService;

        @Transactional
        public RecipeResponse createRecipe(RecipeRequest request, Long userId) {
                log.info("Creating recipe '{}' for user {}", request.getName(), userId);

                Recipe recipe = Recipe.builder()
                                .name(request.getName())
                                .description(request.getDescription())
                                .yieldWeightGrams(request.getYieldWeightGrams())
                                .user(User.builder().id(userId).build())
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                recipe = recipeRepository.save(recipe);

                List<RecipeIngredient> ingredients = createIngredientsFromRequest(recipe, request.getIngredients());
                recipe.setIngredients(ingredients);

                return buildRecipeResponse(recipe);
        }

        @Transactional(readOnly = true)
        public RecipeResponse getRecipeById(Long recipeId, Long userId) {
                Recipe recipe = recipeRepository.findByIdAndUserId(recipeId, userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Recipe not found with id: " + recipeId));

                return buildRecipeResponse(recipe);
        }

        @Transactional(readOnly = true)
        public RecipePageResponse getAllRecipes(Long userId, Pageable pageable) {
                Page<Recipe> recipePage = recipeRepository.findByUserId(userId, pageable);

                List<RecipeSummaryResponse> summaries = recipePage.getContent().stream()
                                .map(this::buildRecipeSummaryResponse)
                                .collect(Collectors.toList());

                return RecipePageResponse.builder()
                                .recipes(summaries)
                                .currentPage(recipePage.getNumber())
                                .totalPages(recipePage.getTotalPages())
                                .totalRecipes(recipePage.getTotalElements())
                                .pageSize(recipePage.getSize())
                                .first(recipePage.isFirst())
                                .last(recipePage.isLast())
                                .hasNext(recipePage.hasNext())
                                .hasPrevious(recipePage.hasPrevious())
                                .build();
        }

        @Transactional
        public RecipeResponse updateRecipe(Long recipeId, RecipeRequest request, Long userId) {
                log.info("Updating recipe {} for user {}", recipeId, userId);

                Recipe recipe = recipeRepository.findByIdAndUserId(recipeId, userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Recipe not found with id: " + recipeId));

                recipe.setName(request.getName());
                recipe.setDescription(request.getDescription());
                recipe.setYieldWeightGrams(request.getYieldWeightGrams());
                recipe.setUpdatedAt(LocalDateTime.now());

                recipeIngredientRepository.deleteByRecipeId(recipeId);

                List<RecipeIngredient> newIngredients = createIngredientsFromRequest(recipe, request.getIngredients());
                recipe.setIngredients(newIngredients);

                recipeRepository.save(recipe);

                return buildRecipeResponse(recipe);
        }

        @Transactional
        public void deleteRecipe(Long recipeId, Long userId) {
                Recipe recipe = recipeRepository.findByIdAndUserId(recipeId, userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Recipe not found with id: " + recipeId));

                log.info("Deleting recipe {} for user {}", recipeId, userId);
                recipeRepository.delete(recipe);
        }

        private List<RecipeIngredient> createIngredientsFromRequest(Recipe recipe,
                        List<RecipeIngredientRequest> ingredientRequests) {
                List<RecipeIngredient> ingredients = new ArrayList<>();

                for (int i = 0; i < ingredientRequests.size(); i++) {
                        RecipeIngredientRequest req = ingredientRequests.get(i);

                        Product product = productRepository.findById(req.getProductId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Product not found with id: " + req.getProductId()));

                        Integer displayOrderInt = req.getDisplayOrder() != null ? req.getDisplayOrder().intValue() : i;

                        RecipeIngredient ingredient = RecipeIngredient.builder()
                                        .recipe(recipe)
                                        .product(product)
                                        .quantityGrams(req.getQuantityGrams())
                                        .displayOrder(displayOrderInt)
                                        .build();

                        ingredients.add(recipeIngredientRepository.save(ingredient));
                }

                return ingredients;
        }

        private RecipeResponse buildRecipeResponse(Recipe recipe) {
                double totalIngredientsWeight = calculateTotalIngredientsWeight(recipe);
                double totalCost = calculateTotalCost(recipe);

                NutrimentsDTO totalNutrients = calculateTotalNutrients(recipe);
                NutrimentsDTO nutritionPer100g = nutritionConversionService.calculatePer100g(
                                totalNutrients, recipe.getYieldWeightGrams());

                double yieldWeight = recipe.getYieldWeightGrams() != null ? recipe.getYieldWeightGrams().doubleValue()
                                : 0.0;
                double costPerGram = yieldWeight != 0.0 ? totalCost / yieldWeight : 0.0;
                double costPer100g = costPerGram * 100.0;

                double yieldLossPercentage = calculateYieldLossPercentage(totalIngredientsWeight,
                                yieldWeight);

                List<RecipeIngredientResponse> ingredientResponses = recipe.getIngredients().stream()
                                .sorted(Comparator.comparing(RecipeIngredient::getDisplayOrder))
                                .map(ingredient -> buildIngredientResponse(ingredient, totalIngredientsWeight,
                                                totalCost))
                                .collect(Collectors.toList());

                return RecipeResponse.builder()
                                .id(recipe.getId())
                                .name(recipe.getName())
                                .description(recipe.getDescription())
                                .yieldWeightGrams(recipe.getYieldWeightGrams() != null
                                                ? recipe.getYieldWeightGrams().doubleValue()
                                                : 0.0)
                                .ingredients(ingredientResponses)
                                .totalCost(totalCost)
                                .costPerGram(costPerGram)
                                .costPer100g(costPer100g)
                                .totalIngredientsWeight(totalIngredientsWeight)
                                .yieldLossPercentage(yieldLossPercentage)
                                .calculatedNutrition(nutritionPer100g)
                                .createdAt(recipe.getCreatedAt())
                                .updatedAt(recipe.getUpdatedAt())
                                .userId(recipe.getUser() != null ? recipe.getUser().getId() : null)
                                .build();
        }

        private double calculateTotalIngredientsWeight(Recipe recipe) {
                return recipe.getIngredients().stream()
                                .mapToDouble(RecipeIngredient::getQuantityGrams)
                                .sum();
        }

        private double calculateIngredientCost(RecipeIngredient ingredient) {
                Product product = ingredient.getProduct();

                if (product.getCostPerUnit() == null) {
                        return 0.0;
                }

                double quantityKg = ingredient.getQuantityGrams() / 1000.0;
                return quantityKg * product.getCostPerUnit();
        }

        private double calculateTotalCost(Recipe recipe) {
                return recipe.getIngredients().stream()
                                .mapToDouble(this::calculateIngredientCost)
                                .sum();
        }

        private double calculateYieldLossPercentage(double totalIngredientsWeight, double yieldWeightGrams) {
                if (totalIngredientsWeight == 0) {
                        return 0.0;
                }
                double loss = totalIngredientsWeight - yieldWeightGrams;
                return (loss / totalIngredientsWeight) * 100.0;
        }

        private RecipeIngredientResponse buildIngredientResponse(RecipeIngredient ingredient,
                        double totalIngredientsWeight, double totalCost) {

                // CORREGIDO: Usar toProductDTO en lugar de toResponse
                ProductDTO productDTO = productMapper.toProductDTO(ingredient.getProduct());
                double ingredientCost = calculateIngredientCost(ingredient);
                double percentageOfTotal = totalIngredientsWeight != 0
                                ? (ingredient.getQuantityGrams().doubleValue() / totalIngredientsWeight) * 100.0
                                : 0.0;

                return RecipeIngredientResponse.builder()
                                .id(ingredient.getId())
                                .product(productDTO) // ✅ Ahora usa ProductDTO con nutriments
                                .quantityGrams(ingredient.getQuantityGrams().doubleValue())
                                .displayOrder(ingredient.getDisplayOrder())
                                .ingredientCost(ingredientCost)
                                .percentageOfTotal(percentageOfTotal)
                                .build();
        }

        private NutrimentsDTO calculateTotalNutrients(Recipe recipe) {
                NutrimentsDTO totalNutrients = new NutrimentsDTO();

                for (RecipeIngredient ingredient : recipe.getIngredients()) {
                        Product product = ingredient.getProduct();

                        // Obtener ProductNutriments del producto (ENTIDAD)
                        com.trazia.trazia_project.entity.product.ProductNutriments productNutriments = product
                                        .getNutriments();
                        if (productNutriments == null) {
                                continue;
                        }

                        // Convertir a NutrimentsDTO
                        NutrimentsDTO nutrients = productMapper.toNutrimentsDTO(productNutriments);

                        // Obtener servingSize como Double
                        Double servingSize = product.getServingSizeGrams() != null
                                        ? product.getServingSizeGrams().doubleValue()
                                        : null;

                        // Usar el valor Double de quantityGrams
                        Double quantityGrams = ingredient.getQuantityGrams() != null
                                        ? ingredient.getQuantityGrams().doubleValue()
                                        : 0.0;

                        NutrimentsDTO usedNutrients = nutritionConversionService.normalizeNutrients(
                                        nutrients, servingSize, quantityGrams);

                        totalNutrients = nutritionConversionService.sumNutrients(totalNutrients, usedNutrients);
                }
                return totalNutrients;
        }

        private RecipeSummaryResponse buildRecipeSummaryResponse(Recipe recipe) {
                double totalCost = calculateTotalCost(recipe);
                double yieldWeight = recipe.getYieldWeightGrams() != null ? recipe.getYieldWeightGrams().doubleValue()
                                : 0.0;
                double costPer100g = yieldWeight != 0.0 ? (totalCost / yieldWeight) * 100.0 : 0.0;
                int ingredientCount = recipe.getIngredients() != null ? recipe.getIngredients().size() : 0;

                return RecipeSummaryResponse.builder()
                                .id(recipe.getId())
                                .name(recipe.getName())
                                .description(recipe.getDescription())
                                .yieldWeightGrams(recipe.getYieldWeightGrams())
                                .totalCost(totalCost)
                                .costPer100g(costPer100g)
                                .ingredientCount(ingredientCount)
                                .createdAt(recipe.getCreatedAt())
                                .updatedAt(recipe.getUpdatedAt())
                                .build();
        }
}
