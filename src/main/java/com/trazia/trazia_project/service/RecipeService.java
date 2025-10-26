package com.trazia.trazia_project.service;

import com.trazia.trazia_project.entity.User;
import com.trazia.trazia_project.dto.product.ProductDTO;
import com.trazia.trazia_project.dto.recipe.*;
import com.trazia.trazia_project.entity.product.Product;
import com.trazia.trazia_project.entity.recipe.Recipe;
import com.trazia.trazia_project.entity.recipe.RecipeIngredient;
import com.trazia.trazia_project.exception.ResourceNotFoundException;
import com.trazia.trazia_project.mapper.ProductMapper;
import com.trazia.trazia_project.model.NutrimentsDTO;
import com.trazia.trazia_project.repository.ProductRepository;
import com.trazia.trazia_project.repository.RecipeIngredientRepository;
import com.trazia.trazia_project.repository.RecipeRepository;
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
 * Servicio principal para manejar recetas: creación, actualización,
 * cálculo nutricional (HU 5.1) y construcción de responses.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeService {

        private final RecipeRepository recipeRepository;
        private final RecipeIngredientRepository recipeIngredientRepository;
        private final ProductRepository productRepository;
        private final ProductMapper productMapper;
        private final NutritionConversionService nutritionConversionService;

        // ===========================
        // NUEVO MÉTODO PARA HU 5.2
        // ===========================
        /**
         * Genera la información necesaria para imprimir la etiqueta de una receta.
         * @param recipeId id de la receta
         * @param userId id del usuario propietario
         * @return DTO con los datos de impresión de etiqueta
         */
        @Transactional(readOnly = true)
        public LabelPrintDTO generateLabel(Long recipeId, Long userId) {
                return new LabelPrintDTO();
        }

        // ===========================
        // PUBLIC CRUD METHODS
        // ===========================

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

        @Transactional(readOnly = true)
        public RecipeResponse getRecipeById(Long recipeId, Long userId) {
                Recipe recipe = recipeRepository.findByIdAndUserId(recipeId, userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Recipe not found with id: " + recipeId));

                // ensure nutriments are calculated
                calculatePerServing(recipe);
                return buildRecipeResponse(recipe);
        }

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

        @Transactional
        public void deleteRecipe(Long recipeId, Long userId) {
                Recipe recipe = recipeRepository.findByIdAndUserId(recipeId, userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Recipe not found with id: " + recipeId));
                log.info("Deleting recipe {} for user {}", recipeId, userId);
                recipeRepository.delete(recipe);
        }

        /**
         * Calcula y almacena en la entidad Recipe los nutrimentos por 100g.
         * (Comentario en español: calcula los nutrientes totales de la receta,
         * los normaliza por 100 g y los guarda en recipe.nutrimentsPor100g)
         */
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

                // Validación para nunca asignar null
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
         * Devuelve los % del Valor Diario para los nutrimentos recibidos.
         * Se asegura de que nunca retorne null, incluso si los nutrimentos son nulos o
         * incompletos.
         */
        public NutrimentsDTO calculateDailyValue(NutrimentsDTO nutriments) {
                if (nutriments == null) {
                        log.warn("calculateDailyValue() recibió un nutriments nulo, devolviendo valores vacíos.");
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

                // Comprobación fuerte contra propiedades null en nutriments
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
                                log.warn("El servicio de conversión devolvió null, generando NutrimentsDTO vacío.");
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
                        return result;
                } catch (Exception e) {
                        log.error("Error al calcular el valor diario de nutrientes: {}", e.getMessage(), e);
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

        /**
         * Devuelve una cadena con los ingredientes formateados, ordenados por
         * displayOrder (descendente)
         * Ej: "Harina (200 g), Azúcar (50 g)"
         */
        public String formatIngredientsList(Recipe recipe) {
                if (recipe == null || recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
                        return "No hay ingredientes disponibles.";
                }

                // Usar lista mutable para evitar errores al ordenar
                List<RecipeIngredient> ingredients = new ArrayList<>(recipe.getIngredients());

                // Ordenar ingredientes por nombre y cantidad de forma null-safe
                ingredients.sort(
                                Comparator.comparing(
                                                (RecipeIngredient ingredient) -> ingredient.getProduct() != null
                                                                ? ingredient.getProduct().getName() != null
                                                                                ? ingredient.getProduct().getName()
                                                                                : ""
                                                                : "",
                                                Comparator.nullsLast(String::compareTo)).thenComparing(
                                                                (RecipeIngredient ingredient) -> ingredient
                                                                                .getQuantityGrams() != null
                                                                                                ? ingredient.getQuantityGrams()
                                                                                                : BigDecimal.ZERO,
                                                                Comparator.nullsLast(BigDecimal::compareTo)));

                StringBuilder formattedList = new StringBuilder();

                for (RecipeIngredient ingredient : ingredients) {
                        String name = ingredient.getProduct() != null && ingredient.getProduct().getName() != null
                                        ? ingredient.getProduct().getName()
                                        : "Ingrediente desconocido";

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
                                        .quantityGrams(r.getQuantityGrams() == null ? BigDecimal.ZERO
                                                        : r.getQuantityGrams())
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
                        nutritionPer100g = NutrimentsDTO.builder()
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

                double yieldWeight = recipe.getYieldWeightGrams() != null ? recipe.getYieldWeightGrams().doubleValue()
                                : 0.0;
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
                                .mapToDouble(i -> i.getQuantityGrams() != null ? i.getQuantityGrams().doubleValue()
                                                : 0.0)
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
         * Suma los nutrimentos de todos los ingredientes (proporcional a su cantidad)
         * Devuelve un NutrimentsDTO con totales absolutos (antes de normalizar por
         * 100g)
         */
        private NutrimentsDTO calculateTotalNutrients(Recipe recipe) {
                NutrimentsDTO total = NutrimentsDTO.builder()
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

                        // assume product.getNutriments() returns an object with getters in numeric
                        // types (BigDecimal/double)
                        // adapt mappings according to your ProductNutriments structure
                        // using productMapper to convert product nutriments to NutrimentsDTO helps keep
                        // mapping consistent
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
                        nutrition = NutrimentsDTO.builder()
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

                return RecipeSummaryResponse.builder()
                                .id(recipe.getId())
                                .name(recipe.getName())
                                .yieldWeightGrams(recipe.getYieldWeightGrams() != null
                                                ? recipe.getYieldWeightGrams().doubleValue()
                                                : 0.0)
                                .calculatedCalories(nutrition != null ? nutrition.getCalories() : 0.0)
                                .build();
        }
}
