package com.trazia.trazia_project.controller.recipe;

import com.trazia.trazia_project.dto.recipe.*;
import com.trazia.trazia_project.entity.user.User;
import com.trazia.trazia_project.service.recipe.RecipeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private static final Logger logger = LoggerFactory.getLogger(RecipeController.class);

    // Crear receta
    @PostMapping
    public ResponseEntity<RecipeResponse> createRecipe(
            @Valid @RequestBody RecipeRequest request,
            @AuthenticationPrincipal User principalUser) {

        RecipeResponse createdRecipe = recipeService.createRecipe(request, principalUser.getId());
        return ResponseEntity.ok(createdRecipe);
    }

    // Obtener receta por ID
    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getRecipeById(
            @PathVariable Long id,
            @AuthenticationPrincipal User principalUser) {

        RecipeResponse recipe = recipeService.getRecipeById(id, principalUser.getId());
        return ResponseEntity.ok(recipe);
    }

    // Listar recetas paginadas
    @GetMapping
    public ResponseEntity<RecipePageResponse> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User principalUser) {

        RecipePageResponse pageResponse = recipeService.getAllRecipes(principalUser.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(pageResponse);
    }

    // Actualizar receta
    @PutMapping("/{id}")
    public ResponseEntity<RecipeResponse> updateRecipe(
            @PathVariable Long id,
            @Valid @RequestBody RecipeRequest request,
            @AuthenticationPrincipal User principalUser) {

        RecipeResponse updated = recipeService.updateRecipe(id, request, principalUser.getId());
        return ResponseEntity.ok(updated);
    }

    // Eliminar receta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(
            @PathVariable Long id,
            @AuthenticationPrincipal User principalUser) {

        recipeService.deleteRecipe(id, principalUser.getId());
        return ResponseEntity.noContent().build();
    }

    // Generar etiqueta nutricional para impresión
    @GetMapping("/{id}/label")
    public ResponseEntity<LabelPrintDTO> getRecipeLabel(
            @PathVariable Long id,
            @AuthenticationPrincipal User principalUser) {

        try {
            LabelPrintDTO label = recipeService.generateLabel(id, principalUser.getId());
            return ResponseEntity.ok(label);
        } catch (com.trazia.trazia_project.exception.recipe.ResourceNotFoundException e) {
            logger.warn("Recipe not found for label generation: id={}, userId={}", id, principalUser.getId());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error generating label for recipe id={}, userId={}", id, principalUser.getId(), e);
            return ResponseEntity.status(500).build();
        }
    }
}