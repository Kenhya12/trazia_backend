package com.trazia.trazia_project.service.impl;

import com.trazia.trazia_project.dto.product.ProductDTO;
import com.trazia.trazia_project.dto.recipe.*;
import com.trazia.trazia_project.entity.User;
import com.trazia.trazia_project.entity.product.Product;
import com.trazia.trazia_project.entity.recipe.Recipe;
import com.trazia.trazia_project.entity.recipe.RecipeIngredient;
import com.trazia.trazia_project.exception.ResourceNotFoundException;
import com.trazia.trazia_project.mapper.ProductMapper;
import com.trazia.trazia_project.model.NutrimentsDTO;
import com.trazia.trazia_project.repository.ProductRepository;
import com.trazia.trazia_project.repository.RecipeIngredientRepository;
import com.trazia.trazia_project.repository.RecipeRepository;
import com.trazia.trazia_project.service.NutritionConversionService;
import com.trazia.trazia_project.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of RecipeService for managing recipes: creation, updates,
 * nutritional calculations (HU 5.1) and response construction.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final NutritionConversionService nutritionConversionService;

    // ===========================
    // PUBLIC CRUD METHODS
    // ===========================

    @Override
    @Transactional
    public RecipeResponse createRecipe(RecipeRequest request, Long userId) {
        log.info("Creating recipe '{}' for user {}", request.getName(), userId);
        Recipe recipe = buildRecipeEntity(request, userId);
        recipe = recipeRepository.save(recipe);
        List<RecipeIngredient> ingredients = createIngredientsFromRequest(recipe, request.getIngredients());
        recipe.setIngredients(ingredients);

        // HU 5.1: compute and store nutriments per 100g for later use
        calculatePerServing(recipe);
        return buildRecipeResponse(recipe);
    }

    @Override
    @Transactional(readOnly = true)
    public RecipeResponse getRecipeById(Long recipeId, Long userId) {
        Recipe recipe = recipeRepository.findByIdAndUserId(recipeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recipe not found with id: " + recipeId));

        // ensure nutriments are calculated
        calculatePerServing(recipe);
        return buildRecipeResponse(recipe);
    }

    @Override
    @Transactional(readOnly = true)
    public RecipePageResponse getAllRecipes(Long userId, Pageable pageable) {
        Page<Recipe> page = recipeRepository.findByUserId(userId, pageable);
        List<RecipeSummaryResponse> summaries = page.getContent().stream()
                .map(this::buildRecipeSummaryResponse)
                .collect(Collectors.toList());

        return RecipePageResponse.builder()
                .recipes(summaries)
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalRecipes(page.getTotalElements())
                .pageSize(page.getSize())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    @Override
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

        // replace ingredients
        recipeIngredientRepository.deleteByRecipeId(recipeId);
        List<RecipeIngredient> newIngredients = createIngredientsFromRequest(recipe, request.getIngredients());
        recipe.setIngredients(newIngredients);

        calculatePerServing(recipe);
        recipeRepository.save(recipe);
        return buildRecipeResponse(recipe);
    }

    @Override
    @Transactional
    public void deleteRecipe(Long recipeId, Long userId) {
        Recipe recipe = recipeRepository.findByIdAndUserId(recipeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recipe not found with id: " + recipeId));
        log.info("Deleting recipe {} for user {}", recipeId, userId);
        recipeRepository.delete(recipe);
    }

    /**
     * Calculates and stores in Recipe entity the nutrients per 100g.
     */
    @Override
    public void calculatePerServing(Recipe recipe) {
        if (recipe.getYieldWeightGrams() == null) {
            throw new RuntimeException("Yield weight cannot be null");
        }

        if (recipe.getYieldWeightGrams() == 0.0) {
            throw new ArithmeticException("Yield weight cannot be zero");
        }

        NutrimentsDTO total = calculateTotalNutrients(recipe);
        NutrimentsDTO per100g = nutritionConversionService.calculatePer100g(total,
                recipe.getYieldWeightGrams());

        // Validation to never assign null
        if (per100g == null) {
            per100g = NutrimentsDTO.builder()
                    .calories(0.0)
                    .protein(0.0)
                    .carbohydrates(0.0)
                    .sugars(0.0)
                    .fat(0.0)
                    .saturatedFat(0.0)
                    .fiber(0.0)
                    .sodium(0.0)
                    .salt(0.0)
                    .build();
        }

        // Only set the ProductNutriments entity, not the DTO
        recipe.setNutrimentsPor100g(productMapper.toEntityProductNutriments(per100g));
    }

    /**
     * Returns % of Daily Value for received nutrients.
     * Ensures it never returns null, even if nutrients are null or incomplete.
     */
    @Override
    public NutrimentsDTO calculateDailyValue(NutrimentsDTO nutriments) {
        if (nutriments == null) {
            log.warn("calculateDailyValue() received null nutrients, returning empty values.");
            return buildEmptyNutriments();
        }

        // Strong check against null properties in nutriments
        nutriments.setCalories(Optional.ofNullable(nutriments.getCalories()).orElse(0.0));
        nutriments.setProtein(Optional.ofNullable(nutriments.getProtein()).orElse(0.0));
        nutriments.setCarbohydrates(Optional.ofNullable(nutriments.getCarbohydrates()).orElse(0.0));
        nutriments.setSugars(Optional.ofNullable(nutriments.getSugars()).orElse(0.0));
        nutriments.setFat(Optional.ofNullable(nutriments.getFat()).orElse(0.0));
        nutriments.setSaturatedFat(Optional.ofNullable(nutriments.getSaturatedFat()).orElse(0.0));
        nutriments.setFiber(Optional.ofNullable(nutriments.getFiber()).orElse(0.0));
        nutriments.setSodium(Optional.ofNullable(nutriments.getSodium()).orElse(0.0));
        nutriments.setSalt(Optional.ofNullable(nutriments.getSalt()).orElse(0.0));

        try {
            NutrimentsDTO result = nutritionConversionService.calculateDailyValue(nutriments);
            if (result == null) {
                log.warn("Conversion service returned null, generating empty NutrimentsDTO.");
                return buildEmptyNutriments();
            }
            return result;
        } catch (Exception e) {
            log.error("Error calculating daily value of nutrients: {}", e.getMessage(), e);
            return buildEmptyNutriments();
        }
    }

    /**
     * Returns a formatted string with ingredients, ordered by displayOrder
     * (descending)
     * Ex: "Flour (200 g), Sugar (50 g)"
     */
    @Override
    public String formatIngredientsList(Recipe recipe) {
        if (recipe == null || recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
            return "No ingredients available.";
        }

        // Use mutable list to avoid errors when sorting
        List<RecipeIngredient> ingredients = new ArrayList<>(recipe.getIngredients());

        // Sort ingredients by name and quantity null-safe
        ingredients.sort(
                Comparator.comparing(
                        (RecipeIngredient ingredient) -> ingredient.getProduct() != null
                                ? ingredient.getProduct().getName() != null
                                        ? ingredient.getProduct().getName()
                                        : ""
                                : "",
                        Comparator.nullsLast(String::compareTo))
                        .thenComparing(
                                (RecipeIngredient ingredient) -> ingredient
                                        .getQuantityGrams() != null
                                                ? ingredient.getQuantityGrams()
                                                : BigDecimal.ZERO,
                                Comparator.nullsLast(BigDecimal::compareTo)));

        StringBuilder formattedList = new StringBuilder();
        for (RecipeIngredient ingredient : ingredients) {
            String name = ingredient.getProduct() != null && ingredient.getProduct().getName() != null
                    ? ingredient.getProduct().getName()
                    : "Unknown ingredient";
            BigDecimal quantity = ingredient.getQuantityGrams() != null
                    ? ingredient.getQuantityGrams()
                    : BigDecimal.ZERO;
            formattedList.append(name)
                    .append(" - ")
                    .append(quantity)
                    .append("g\n");
        }
        return formattedList.toString().trim();
    }

    /**
     * Generates the information needed to print a recipe label.
     * @param recipeId recipe id
     * @param userId owner user id
     * @return DTO with label printing data
     */
    @Override
    @Transactional(readOnly = true)
    public LabelPrintDTO generateLabel(Long recipeId, Long userId) {
        // Find user's recipe
        Recipe recipe = recipeRepository.findByIdAndUserId(recipeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recipe not found with id: " + recipeId));

        // Ensure nutrients are calculated
        calculatePerServing(recipe);

        // Create DTO for label
        LabelPrintDTO label = new LabelPrintDTO();

        // Basic information
        label.setRecipeName(recipe.getName());
        label.setRecipeDescription(recipe.getDescription());
        // Null and type safe assignment for yield weight
        label.setYieldWeightGrams(
            recipe.getYieldWeightGrams() != null 
                ? BigDecimal.valueOf(recipe.getYieldWeightGrams()) 
                : BigDecimal.ZERO
        );

        // Legal data
        label.setLegalDisclaimer("Best consumed before indicated date. Keep in cool, dry place.");

        // Nutritional information
        if (recipe.getNutrimentsPor100g() != null) {
            NutrimentsDTO nutriments = productMapper.toNutrimentsDTO(recipe.getNutrimentsPor100g());
            label.setEnergyPer100g(nutriments.getCalories() != null ? BigDecimal.valueOf(nutriments.getCalories()) : BigDecimal.ZERO);
            label.setProteinPer100g(nutriments.getProtein() != null ? BigDecimal.valueOf(nutriments.getProtein()) : BigDecimal.ZERO);
            label.setFatPer100g(nutriments.getFat() != null ? BigDecimal.valueOf(nutriments.getFat()) : BigDecimal.ZERO);
            label.setCarbsPer100g(nutriments.getCarbohydrates() != null ? BigDecimal.valueOf(nutriments.getCarbohydrates()) : BigDecimal.ZERO);
            label.setSugarsPer100g(nutriments.getSugars() != null ? BigDecimal.valueOf(nutriments.getSugars()) : BigDecimal.ZERO);
            label.setFiberPer100g(nutriments.getFiber() != null ? BigDecimal.valueOf(nutriments.getFiber()) : BigDecimal.ZERO);
            label.setSaturatedFatPer100g(nutriments.getSaturatedFat() != null ? BigDecimal.valueOf(nutriments.getSaturatedFat()) : BigDecimal.ZERO);
            label.setSaltPer100g(nutriments.getSalt() != null ? BigDecimal.valueOf(nutriments.getSalt()) : BigDecimal.ZERO);
            label.setSodiumPer100g(nutriments.getSodium() != null ? BigDecimal.valueOf(nutriments.getSodium()) : BigDecimal.ZERO);
        } else {
            label.setEnergyPer100g(BigDecimal.ZERO);
            label.setProteinPer100g(BigDecimal.ZERO);
            label.setFatPer100g(BigDecimal.ZERO);
            label.setCarbsPer100g(BigDecimal.ZERO);
            label.setSugarsPer100g(BigDecimal.ZERO);
            label.setFiberPer100g(BigDecimal.ZERO);
            label.setSaturatedFatPer100g(BigDecimal.ZERO);
            label.setSaltPer100g(BigDecimal.ZERO);
            label.setSodiumPer100g(BigDecimal.ZERO);
        }

        // Formatted ingredient list
        label.setIngredientsList(formatIngredientsList(recipe));

        return label;
    }

    /**
     * Generates the information needed to print a recipe label (alias for
     * generateLabel).
     * 
     * @param recipeId recipe id
     * @param userId   owner user id
     * @return DTO with label printing data
     */
    @Override
    @Transactional(readOnly = true)
    public LabelPrintDTO generatePrintLabel(Long recipeId, Long userId) {
        return generateLabel(recipeId, userId);
    }

    // ===========================
    // PRIVATE HELPERS
    // ===========================

    private Recipe buildRecipeEntity(RecipeRequest request, Long userId) {
        return Recipe.builder()
                .name(request.getName())
                .description(request.getDescription())
                .yieldWeightGrams(request.getYieldWeightGrams())
                .user(User.builder().id(userId).build())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private List<RecipeIngredient> createIngredientsFromRequest(Recipe recipe, List<RecipeIngredientRequest> reqs) {
        if (reqs == null || reqs.isEmpty())
            return List.of();

        List<RecipeIngredient> list = new ArrayList<>();
        for (int i = 0; i < reqs.size(); i++) {
            RecipeIngredientRequest r = reqs.get(i);
            Product product = productRepository.findById(r.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + r.getProductId()));

            Integer displayOrder = r.getDisplayOrder() != null ? r.getDisplayOrder() : i;
            RecipeIngredient ingredient = RecipeIngredient.builder()
                    .recipe(recipe)
                    .product(product)
                    .quantityGrams(r.getQuantityGrams() == null ? BigDecimal.ZERO : r.getQuantityGrams())
                    .displayOrder(displayOrder)
                    .build();

            RecipeIngredient saved = recipeIngredientRepository.save(ingredient);
            list.add(saved);
        }
        return list;
    }

    private RecipeResponse buildRecipeResponse(Recipe recipe) {
        double totalIngredientsWeight = calculateTotalIngredientsWeight(recipe);
        double totalCost = calculateTotalCost(recipe);

        // Convert ProductNutriments to NutrimentsDTO safely for API response
        NutrimentsDTO nutritionPer100g = null;
        if (recipe.getNutrimentsPor100g() != null) {
            nutritionPer100g = productMapper.toNutrimentsDTO(recipe.getNutrimentsPor100g());
        }

        // If still null, provide default zero DTO
        if (nutritionPer100g == null) {
            nutritionPer100g = buildEmptyNutriments();
        }

        double yieldWeight = recipe.getYieldWeightGrams() != null ? recipe.getYieldWeightGrams().doubleValue() : 0.0;
        double costPerGram = yieldWeight != 0.0 ? totalCost / yieldWeight : 0.0;
        double costPer100g = costPerGram * 100.0;
        double yieldLossPercentage = calculateYieldLossPercentage(totalIngredientsWeight, yieldWeight);

        List<RecipeIngredientResponse> ingredientResponses = recipe.getIngredients().stream()
                .sorted(Comparator.comparing(RecipeIngredient::getDisplayOrder))
                .map(i -> buildIngredientResponse(i, totalIngredientsWeight, totalCost))
                .collect(Collectors.toList());

        return RecipeResponse.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .description(recipe.getDescription())
                .yieldWeightGrams(yieldWeight)
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

    // ===========================
    // PRIVATE CALCULATIONS
    // ===========================

    private double calculateTotalIngredientsWeight(Recipe recipe) {
        if (recipe.getIngredients() == null)
            return 0.0;
        return recipe.getIngredients().stream()
                .mapToDouble(i -> i.getQuantityGrams() != null ? i.getQuantityGrams().doubleValue() : 0.0)
                .sum();
    }

    private double calculateIngredientCost(RecipeIngredient ingredient) {
        Product product = ingredient.getProduct();
        if (product == null || product.getCostPerUnit() == null)
            return 0.0;
        double quantityKg = ingredient.getQuantityGrams().doubleValue() / 1000.0;
        return quantityKg * product.getCostPerUnit();
    }

    private double calculateTotalCost(Recipe recipe) {
        if (recipe.getIngredients() == null)
            return 0.0;
        return recipe.getIngredients().stream()
                .mapToDouble(this::calculateIngredientCost)
                .sum();
    }

    private double calculateYieldLossPercentage(double totalIngredientsWeight, double yieldWeightGrams) {
        if (totalIngredientsWeight == 0)
            return 0.0;
        double loss = totalIngredientsWeight - yieldWeightGrams;
        return (loss / totalIngredientsWeight) * 100.0;
    }

    private RecipeIngredientResponse buildIngredientResponse(RecipeIngredient ingredient,
            double totalIngredientsWeight, double totalCost) {
        ProductDTO productDTO = productMapper.toProductDTO(ingredient.getProduct());
        double ingredientCost = calculateIngredientCost(ingredient);
        double percentageOfTotal = totalIngredientsWeight != 0
                ? (ingredient.getQuantityGrams().doubleValue() / totalIngredientsWeight) * 100.0
                : 0.0;

        return RecipeIngredientResponse.builder()
                .id(ingredient.getId())
                .product(productDTO)
                .quantityGrams(ingredient.getQuantityGrams().doubleValue())
                .percentageOfTotal(percentageOfTotal)
                .cost(ingredientCost)
                .build();
    }

    /**
     * Sums the nutrients of all ingredients (proportional to their quantity)
     * Returns a NutrimentsDTO with absolute totals (before normalizing per 100g)
     */
    private NutrimentsDTO calculateTotalNutrients(Recipe recipe) {
        NutrimentsDTO total = buildEmptyNutriments();

        if (recipe.getIngredients() == null || recipe.getIngredients().isEmpty())
            return total;

        // sum by proportion of each ingredient relative to total ingredient weight
        double totalWeight = calculateTotalIngredientsWeight(recipe);
        if (totalWeight == 0.0)
            return total;

        for (RecipeIngredient ri : recipe.getIngredients()) {
            BigDecimal qty = ri.getQuantityGrams() != null ? ri.getQuantityGrams() : BigDecimal.ZERO;
            double proportion = qty.doubleValue() / totalWeight;
            Product prod = ri.getProduct();
            if (prod == null || prod.getNutriments() == null)
                continue;

            NutrimentsDTO prodN = productMapper.toNutrimentsDTO(prod.getNutriments());
            total.setCalories(total.getCalories() + prodN.getCalories() * proportion);
            total.setProtein(total.getProtein() + prodN.getProtein() * proportion);
            total.setCarbohydrates(total.getCarbohydrates() + prodN.getCarbohydrates() * proportion);
            total.setSugars(total.getSugars() + prodN.getSugars() * proportion);
            total.setFat(total.getFat() + prodN.getFat() * proportion);
            total.setSaturatedFat(total.getSaturatedFat() + prodN.getSaturatedFat() * proportion);
            total.setFiber(total.getFiber() + prodN.getFiber() * proportion);
            total.setSodium(total.getSodium() + prodN.getSodium() * proportion);
            total.setSalt(total.getSalt() + prodN.getSalt() * proportion);
        }

        return total;
    }

    private RecipeSummaryResponse buildRecipeSummaryResponse(Recipe recipe) {
        // build a lightweight summary used in paginated lists
        NutrimentsDTO nutrition = null;
        if (recipe.getNutrimentsPor100g() != null) {
            nutrition = productMapper.toNutrimentsDTO(recipe.getNutrimentsPor100g());
        }

        if (nutrition == null) {
            nutrition = buildEmptyNutriments();
        }

        return RecipeSummaryResponse.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .yieldWeightGrams(recipe.getYieldWeightGrams() != null
                        ? recipe.getYieldWeightGrams().doubleValue()
                        : 0.0)
                .calculatedCalories(nutrition != null ? nutrition.getCalories() : 0.0)
                .build();
    }

    private NutrimentsDTO buildEmptyNutriments() {
        return NutrimentsDTO.builder()
                .calories(0.0)
                .protein(0.0)
                .carbohydrates(0.0)
                .sugars(0.0)
                .fat(0.0)
                .saturatedFat(0.0)
                .fiber(0.0)
                .sodium(0.0)
                .salt(0.0)
                .build();
    }
}
