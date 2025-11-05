package com.trazia.trazia_project.service.recipe.impl;

import org.springframework.lang.NonNull;

import com.trazia.trazia_project.dto.product.ProductDTO;
import com.trazia.trazia_project.dto.recipe.*;
import com.trazia.trazia_project.dto.recipe.RecipeIngredientRequest;
import com.trazia.trazia_project.dto.product.LabelPrintDTO;
import com.trazia.trazia_project.entity.batch.FinalProductLot;
import com.trazia.trazia_project.entity.product.Product;
import com.trazia.trazia_project.entity.product.ProductCategory;
import com.trazia.trazia_project.entity.recipe.Recipe;
import com.trazia.trazia_project.entity.recipe.RecipeIngredient;
import com.trazia.trazia_project.entity.user.User;
import com.trazia.trazia_project.exception.recipe.ResourceNotFoundException;
import com.trazia.trazia_project.mapper.ProductMapper;
import com.trazia.trazia_project.model.NutrimentsDTO;
import com.trazia.trazia_project.repository.product.ProductRepository;
import com.trazia.trazia_project.repository.recipe.RecipeIngredientRepository;
import com.trazia.trazia_project.repository.recipe.RecipeRepository;
import com.trazia.trazia_project.service.common.NutritionConversionService;
import com.trazia.trazia_project.service.recipe.RecipeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Implementation of RecipeService for managing recipes: creation, updates,
 * nutritional calculations (HU 5.1) and response construction.
 */
@Slf4j
@RequiredArgsConstructor
@Service
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
        Objects.requireNonNull(userId, "User ID cannot be null");
        log.info("Creating recipe '{}' for user {}", request.getName(), userId);
        Recipe recipe = Objects.requireNonNull(buildRecipeEntity(request, userId), "Recipe cannot be null");
        recipe = recipeRepository.save(recipe);
        List<RecipeIngredient> ingredients = createIngredientsFromRequest(recipe, request.getIngredients(), userId);
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
        recipe.setYieldWeightGrams(request.getYieldWeightGrams() != null
                ? request.getYieldWeightGrams()
                : BigDecimal.ZERO);
        recipe.setUpdatedAt(LocalDateTime.now());

        // replace ingredients
        recipeIngredientRepository.deleteByRecipeId(recipeId);
        List<RecipeIngredient> newIngredients = createIngredientsFromRequest(recipe, request.getIngredients(), userId);
        recipe.setIngredients(newIngredients);

        calculatePerServing(recipe);
        recipeRepository.save(recipe);
        return buildRecipeResponse(recipe);
    }

    @Override
    @Transactional
    public void deleteRecipe(Long recipeId, Long userId) {
        Recipe recipe = Objects.requireNonNull(
                recipeRepository.findByIdAndUserId(recipeId, userId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Recipe not found with id: " + recipeId)),
                "Recipe cannot be null");
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
        if (recipe.getYieldWeightGrams().compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Yield weight cannot be zero");
        }

        NutrimentsDTO total = calculateTotalNutrients(recipe);
        // BigDecimal used for all calculations, only convert to double here for service
        // compatibility
        NutrimentsDTO per100g = nutritionConversionService.calculatePer100g(
                total,
                recipe.getYieldWeightGrams() != null ? recipe.getYieldWeightGrams() : BigDecimal.ZERO);
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
     * Returns a formatted string with ingredients, ordered by quantity descending
     * and includes allergens.
     * T 5.3.2
     */
    @Override
    public String formatIngredientsList(Recipe recipe) {
        if (recipe == null || recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
            return "No ingredients available.";
        }

        List<RecipeIngredient> ingredients = new ArrayList<>(recipe.getIngredients());

        // Sort ingredients by quantity descending (uses BigDecimal for accuracy)
        ingredients.sort(Comparator.comparing(
                (RecipeIngredient i) -> i.getQuantityGrams() != null ? i.getQuantityGrams() : BigDecimal.ZERO)
                .reversed());

        StringBuilder formattedList = new StringBuilder();
        for (RecipeIngredient ingredient : ingredients) {
            String name = ingredient.getProduct() != null && ingredient.getProduct().getName() != null
                    ? ingredient.getProduct().getName()
                    : "Unknown ingredient";

            BigDecimal quantity = ingredient.getQuantityGrams() != null ? ingredient.getQuantityGrams()
                    : BigDecimal.ZERO;

            // Allergen handling: include list of allergens in parentheses if present
            String allergens = "";
            if (ingredient.getProduct() != null && ingredient.getProduct().getAllergens() != null
                    && !ingredient.getProduct().getAllergens().isEmpty()) {
                allergens = " (Allergens: " + String.join(", ", ingredient.getProduct().getAllergens()) + ")";
            }

            formattedList.append(name)
                    .append(" - ")
                    .append(quantity)
                    .append("g")
                    .append(allergens)
                    .append("\n");
        }

        return formattedList.toString().trim();
    }

    /**
     * Generates the information needed to print a recipe label.
     * 
     * @param recipeId recipe id
     * @param userId   owner user id
     * @return DTO with label printing data
     */
    @Transactional(readOnly = true)
    public LabelPrintDTO generateLabel(Long recipeId, Long userId) {
        // Find user's recipe
        Recipe recipe = recipeRepository.findByIdAndUserId(recipeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recipe not found with id: " + recipeId));

        // Ensure nutrients are calculated
        calculatePerServing(recipe);

        // Create DTO for label and fill common info
        LabelPrintDTO label = buildLabelPrintDTO(recipe, false);
        return label;
    }

    /**
     * Generates the information needed to print a recipe label, including allergen
     * detection.
     *
     * @param recipeId recipe id
     * @param userId   owner user id
     * @return DTO with label printing data
     */
    @Override
    @Transactional(readOnly = true)
    public LabelPrintDTO generatePrintLabel(Long recipeId, Long userId) {
        // Obtener la receta del usuario
        Recipe recipe = recipeRepository.findByIdAndUserId(recipeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recipe not found with id: " + recipeId));

        // Asegurar que los nutrientes estén calculados
        calculatePerServing(recipe);

        // Crear DTO para la etiqueta, incluyendo alérgenos y lote
        LabelPrintDTO label = buildLabelPrintDTO(recipe, true);
        return label;
    }

    // ===========================
    // PRIVATE HELPERS
    // ===========================

    private Recipe buildRecipeEntity(@NonNull RecipeRequest request, @NonNull Long userId) {
        return Recipe.builder()
                .name(request.getName())
                .description(request.getDescription())
                .yieldWeightGrams(
                        request.getYieldWeightGrams() != null ? request.getYieldWeightGrams() : BigDecimal.ZERO)
                .user(User.builder().id(userId).build())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private List<RecipeIngredient> createIngredientsFromRequest(@NonNull Recipe recipe, List<RecipeIngredientRequest> reqs, Long userId) {
        if (reqs == null || reqs.isEmpty())
            return List.of();

        List<RecipeIngredient> list = new ArrayList<>();
        for (int i = 0; i < reqs.size(); i++) {
            RecipeIngredientRequest r = reqs.get(i);
            
            // ✅ ACEPTAR ingredientes por productId O por name
            Product product;
            if (r.getProductId() != null) {
                // Usar productId si está disponible
                product = productRepository.findById(r.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Product not found with id: " + r.getProductId()));
            } else if (r.getName() != null && !r.getName().trim().isEmpty()) {
                // ✅ ACEPTAR ingredientes por nombre
                product = findOrCreateProductByName(r.getName().trim(), userId);
            } else {
                throw new IllegalArgumentException("Ingredient must have either productId or name");
            }

            Integer displayOrder = r.getDisplayOrder() != null ? r.getDisplayOrder() : i;
            RecipeIngredient ingredient = RecipeIngredient.builder()
                    .recipe(Objects.requireNonNull(recipe, "Recipe cannot be null"))
                    .product(Objects.requireNonNull(product, "Product cannot be null"))
                    .quantityGrams(r.getQuantityGrams() != null ? r.getQuantityGrams() : BigDecimal.ZERO)
                    .displayOrder(displayOrder)
                    .build();

            // Guardar y asegurar que no sea nulo
            RecipeIngredient saved = Objects.requireNonNull(
                    recipeIngredientRepository.save(ingredient),
                    "Saved RecipeIngredient cannot be null");
            list.add(saved);
        }
        return list;
    }

    /**
     * Busca producto por nombre o crea uno nuevo si no existe
     */
    private Product findOrCreateProductByName(String productName, Long userId) {
        // Buscar producto existente por nombre (case insensitive)
        Optional<Product> existingProduct = productRepository.findByNameIgnoreCase(productName);
        
        if (existingProduct.isPresent()) {
            return existingProduct.get();
        }
        
        // Crear nuevo producto con todos los campos obligatorios
        Product newProduct = Product.builder()
                .name(productName)
                .costPerUnit(BigDecimal.valueOf(0.01)) // ✅ Mayor que 0.0
                .category(ProductCategory.OTHER) // ✅ Categoría no nula
                .description("Ingrediente de receta: " + productName) // ✅ Descripción
                .servingSizeGrams(100) // ✅ Integer, no Double
                .servingDescription("Porción de 100g") // ✅ Descripción de porción
                .user(User.builder().id(userId).build()) // ✅ USER_ID obligatorio
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return productRepository.save(newProduct);
    }

    private RecipeResponse buildRecipeResponse(Recipe recipe) {
        BigDecimal totalIngredientsWeight = calculateTotalIngredientsWeight(recipe);
        BigDecimal totalCost = calculateTotalCost(recipe);

        // Convert ProductNutriments to NutrimentsDTO safely for API response
        NutrimentsDTO nutritionPer100g = null;
        if (recipe.getNutrimentsPor100g() != null) {
            nutritionPer100g = productMapper.toNutrimentsDTO(recipe.getNutrimentsPor100g());
        }

        // If still null, provide default zero DTO
        if (nutritionPer100g == null) {
            nutritionPer100g = buildEmptyNutriments();
        }

        BigDecimal yieldWeight = recipe.getYieldWeightGrams() != null ? recipe.getYieldWeightGrams() : BigDecimal.ZERO;
        BigDecimal costPerGram = BigDecimal.ZERO;
        BigDecimal costPer100g = BigDecimal.ZERO;
        if (yieldWeight.compareTo(BigDecimal.ZERO) != 0) {
            costPerGram = totalCost.divide(yieldWeight, 6, RoundingMode.HALF_UP);
            costPer100g = costPerGram.multiply(BigDecimal.valueOf(100)).setScale(6, RoundingMode.HALF_UP);
        }

        List<RecipeIngredientResponse> ingredientResponses = recipe.getIngredients().stream()
                .sorted(Comparator.comparing(RecipeIngredient::getDisplayOrder))
                .map(i -> buildIngredientResponse(i, totalIngredientsWeight, totalCost))
                .collect(Collectors.toList());

        return RecipeResponse.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .description(recipe.getDescription())
                .yieldWeightGrams(yieldWeight != null ? yieldWeight : BigDecimal.ZERO)
                .ingredients(ingredientResponses)
                .totalCost(totalCost != null ? totalCost : BigDecimal.ZERO)
                .costPer100g(costPer100g != null ? costPer100g : BigDecimal.ZERO)
                .calculatedNutrition(recipe.getNutrimentsPor100g())
                .createdAt(recipe.getCreatedAt())
                .updatedAt(recipe.getUpdatedAt())
                .build();
    }

    // ===========================
    // PRIVATE CALCULATIONS
    // ===========================

    private BigDecimal calculateTotalIngredientsWeight(Recipe recipe) {
        if (recipe.getIngredients() == null)
            return BigDecimal.ZERO;
        return recipe.getIngredients().stream()
                .map(i -> i.getQuantityGrams() != null ? i.getQuantityGrams() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateIngredientCost(RecipeIngredient ingredient) {
        Product product = ingredient.getProduct();
        if (product == null || product.getCostPerUnit() == null)
            return BigDecimal.ZERO;

        BigDecimal quantityKg = ingredient.getQuantityGrams() != null
                ? ingredient.getQuantityGrams().divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal cost = product.getCostPerUnit().multiply(quantityKg);
        return cost.setScale(6, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalCost(Recipe recipe) {
        if (recipe.getIngredients() == null)
            return BigDecimal.ZERO;
        return recipe.getIngredients().stream()
                .map(this::calculateIngredientCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(6, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateYieldLossPercentage(BigDecimal totalIngredientsWeight, BigDecimal yieldWeightGrams) {
        if (totalIngredientsWeight == null || totalIngredientsWeight.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;
        BigDecimal loss = totalIngredientsWeight
                .subtract(yieldWeightGrams != null ? yieldWeightGrams : BigDecimal.ZERO);
        BigDecimal percentage = loss.divide(totalIngredientsWeight, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
        return percentage;
    }

    private RecipeIngredientResponse buildIngredientResponse(RecipeIngredient ingredient,
            BigDecimal totalIngredientsWeight, BigDecimal totalCost) {
        ProductDTO productDTO = productMapper.toProductDTO(ingredient.getProduct());
        BigDecimal ingredientCost = calculateIngredientCost(ingredient);
        
        // Calcular porcentaje
        Double percentageOfTotal = 0.0;
        if (totalIngredientsWeight != null && totalIngredientsWeight.compareTo(BigDecimal.ZERO) != 0 
            && ingredient.getQuantityGrams() != null) {
            BigDecimal percentage = ingredient.getQuantityGrams().multiply(BigDecimal.valueOf(100))
                    .divide(totalIngredientsWeight, 6, RoundingMode.HALF_UP);
            percentageOfTotal = percentage.doubleValue();
        }

        return RecipeIngredientResponse.builder()
                .id(ingredient.getId())
                .product(productDTO)
                .quantityGrams(ingredient.getQuantityGrams() != null ? ingredient.getQuantityGrams().doubleValue() : 0.0)
                .percentageOfTotal(percentageOfTotal)
                .cost(ingredientCost != null ? ingredientCost.doubleValue() : 0.0)
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
        BigDecimal totalWeight = calculateTotalIngredientsWeight(recipe);
        if (totalWeight.compareTo(BigDecimal.ZERO) == 0)
            return total;

        for (RecipeIngredient ri : recipe.getIngredients()) {
            BigDecimal qty = ri.getQuantityGrams() != null ? ri.getQuantityGrams() : BigDecimal.ZERO;
            BigDecimal proportion = totalWeight.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                    : qty.divide(totalWeight, 6, RoundingMode.HALF_UP);
            Product prod = ri.getProduct();
            if (prod == null || prod.getNutriments() == null)
                continue;

            NutrimentsDTO prodN = productMapper.toNutrimentsDTO(prod.getNutriments());
            total.setCalories(total.getCalories() + prodN.getCalories() * proportion.doubleValue());
            total.setProtein(total.getProtein() + prodN.getProtein() * proportion.doubleValue());
            total.setCarbohydrates(total.getCarbohydrates() + prodN.getCarbohydrates() * proportion.doubleValue());
            total.setSugars(total.getSugars() + prodN.getSugars() * proportion.doubleValue());
            total.setFat(total.getFat() + prodN.getFat() * proportion.doubleValue());
            total.setSaturatedFat(total.getSaturatedFat() + prodN.getSaturatedFat() * proportion.doubleValue());
            total.setFiber(total.getFiber() + prodN.getFiber() * proportion.doubleValue());
            total.setSodium(total.getSodium() + prodN.getSodium() * proportion.doubleValue());
            total.setSalt(total.getSalt() + prodN.getSalt() * proportion.doubleValue());
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

        BigDecimal totalCost = calculateTotalCost(recipe);
        BigDecimal costPer100g = BigDecimal.ZERO;
        if (recipe.getYieldWeightGrams() != null && recipe.getYieldWeightGrams().compareTo(BigDecimal.ZERO) != 0) {
            costPer100g = totalCost.divide(recipe.getYieldWeightGrams(), 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return RecipeSummaryResponse.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .yieldWeightGrams(recipe.getYieldWeightGrams() != null ? recipe.getYieldWeightGrams().doubleValue() : 0.0)
                .totalCost(totalCost != null ? totalCost.doubleValue() : 0.0)
                .costPer100g(costPer100g != null ? costPer100g.doubleValue() : 0.0)
                .ingredientCount(recipe.getIngredients() != null ? recipe.getIngredients().size() : 0)
                .calculatedCalories(nutrition != null ? nutrition.getCalories() : 0.0)
                .createdAt(recipe.getCreatedAt())
                .updatedAt(recipe.getUpdatedAt())
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

    /**
     * Helper to fill LabelPrintDTO with consistent BigDecimal usage and proper
     * types.
     * If includeAllergensAndLot is true, fills allergens, usage instructions,
     * batch, expiry.
     */
    private LabelPrintDTO buildLabelPrintDTO(Recipe recipe, boolean includeAllergensAndLot) {
        LabelPrintDTO label = new LabelPrintDTO();

        // Información básica
        label.setRecipeName(recipe.getName());
        label.setRecipeDescription(recipe.getDescription());
        label.setYieldWeightGrams(
                recipe.getYieldWeightGrams() != null ? recipe.getYieldWeightGrams() : BigDecimal.ZERO);
        label.setLegalDisclaimer("Best consumed before indicated date. Keep in cool, dry place.");

        // Información nutricional
        if (recipe.getNutrimentsPor100g() != null) {
            NutrimentsDTO nutriments = productMapper.toNutrimentsDTO(recipe.getNutrimentsPor100g());
            label.setEnergyPer100g(toBigDecimal(nutriments.getCalories()));
            label.setProteinPer100g(toBigDecimal(nutriments.getProtein()));
            label.setFatPer100g(toBigDecimal(nutriments.getFat()));
            label.setCarbsPer100g(toBigDecimal(nutriments.getCarbohydrates()));
            label.setSugarsPer100g(toBigDecimal(nutriments.getSugars()));
            label.setFiberPer100g(toBigDecimal(nutriments.getFiber()));
            label.setSaturatedFatPer100g(toBigDecimal(nutriments.getSaturatedFat()));
            label.setSaltPer100g(toBigDecimal(nutriments.getSalt()));
            label.setSodiumPer100g(toBigDecimal(nutriments.getSodium()));
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

        // Ingredientes
        label.setIngredientsList(Arrays.asList(formatIngredientsList(recipe).split("\n")));

        if (includeAllergensAndLot) {
            // Alérgenos
            List<String> allergens = recipe.getIngredients().stream()
                    .filter(ri -> ri.getProduct() != null && ri.getProduct().getAllergens() != null)
                    .flatMap(ri -> ri.getProduct().getAllergens().stream())
                    .distinct()
                    .collect(Collectors.toList());
            label.setAllergens(allergens);

            // Uso
            label.setUsageInstructions(recipe.getUsageInstructionsSafe());

            // Lote final
            List<FinalProductLot> lots = recipe.getFinalProductLotsSafe();
            if (!lots.isEmpty()) {
                FinalProductLot lot = lots.get(0);
                label.setBatchNumber(lot.getBatchNumber() != null ? lot.getBatchNumber() : "");
                label.setExpiryDate(lot.getExpiryDate());
            } else {
                label.setBatchNumber("");
                label.setExpiryDate(null);
            }
        }

        return label;
    }

    private BigDecimal toBigDecimal(Double value) {
        return value != null ? BigDecimal.valueOf(value) : BigDecimal.ZERO;
    }
}