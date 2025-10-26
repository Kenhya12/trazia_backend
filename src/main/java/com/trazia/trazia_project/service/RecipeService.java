package com.trazia.trazia_project.service;

import com.trazia.trazia_project.dto.recipe.*;
import com.trazia.trazia_project.entity.recipe.Recipe;
import com.trazia.trazia_project.model.NutrimentsDTO;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing recipes.
 * Defines operations for CRUD, nutritional calculations (HU 5.1),
 * and label generation (HU 5.2).
 */
public interface RecipeService {
    
    /**
     * Creates a new recipe for the specified user.
     * @param request Recipe data including name, description, yield, and ingredients
     * @param userId ID of the user creating the recipe
     * @return Complete recipe response with calculated nutrition
     */
    RecipeResponse createRecipe(RecipeRequest request, Long userId);
    
    /**
     * Retrieves a specific recipe by ID for a user.
     * @param recipeId Recipe ID
     * @param userId Owner user ID
     * @return Recipe response with all details and calculated nutrition
     */
    RecipeResponse getRecipeById(Long recipeId, Long userId);
    
    /**
     * Retrieves all recipes for a user with pagination.
     * @param userId Owner user ID
     * @param pageable Pagination parameters
     * @return Paginated list of recipe summaries
     */
    RecipePageResponse getAllRecipes(Long userId, Pageable pageable);
    
    /**
     * Updates an existing recipe.
     * @param recipeId Recipe ID to update
     * @param request Updated recipe data
     * @param userId Owner user ID
     * @return Updated recipe response with recalculated nutrition
     */
    RecipeResponse updateRecipe(Long recipeId, RecipeRequest request, Long userId);
    
    /**
     * Deletes a recipe.
     * @param recipeId Recipe ID to delete
     * @param userId Owner user ID
     */
    void deleteRecipe(Long recipeId, Long userId);
    
    /**
     * Calculates and stores nutritional information per 100g for a recipe.
     * This method modifies the recipe entity's nutrimentsPor100g field.
     * @param recipe Recipe entity with ingredients
     */
    void calculatePerServing(Recipe recipe);
    
    /**
     * Calculates daily value percentages for given nutrients.
     * @param nutriments Nutrient values to calculate daily percentages
     * @return NutrimentsDTO with daily value percentages
     */
    NutrimentsDTO calculateDailyValue(NutrimentsDTO nutriments);
    
    /**
     * Generates label printing data for a recipe (HU 5.2).
     * Includes nutritional facts, ingredients list, and legal information.
     * @param recipeId Recipe ID
     * @param userId Owner user ID
     * @return Label printing DTO with all required information
     */
    LabelPrintDTO generateLabel(Long recipeId, Long userId);
    
    /**
     * Formats the ingredients list as a string for display/printing.
     * @param recipe Recipe with ingredients
     * @return Formatted string with ingredient names and quantities
     */
    String formatIngredientsList(Recipe recipe);

    /**
     * Generates print label data for a recipe.
     * @param recipeId Recipe ID
     * @param userId Owner user ID
     * @return Label printing DTO with print-ready information
     */
    LabelPrintDTO generatePrintLabel(Long recipeId, Long userId);
}
