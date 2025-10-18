package com.trazia.trazia_project.controller;

import com.trazia.trazia_project.dto.recipe.*;
import com.trazia.trazia_project.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    // Crear receta
    @PostMapping
    public ResponseEntity<RecipeResponse> createRecipe(
            @Valid @RequestBody RecipeRequest request,
            @AuthenticationPrincipal(expression = "id") Long userId) {

        RecipeResponse createdRecipe = recipeService.createRecipe(request, userId);
        return ResponseEntity.ok(createdRecipe);
    }

    // Obtener receta por ID
    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getRecipeById(
            @PathVariable Long id,
            @AuthenticationPrincipal(expression = "id") Long userId) {

        RecipeResponse recipe = recipeService.getRecipeById(id, userId);
        return ResponseEntity.ok(recipe);
    }

    // Listar recetas paginadas
    @GetMapping
    public ResponseEntity<RecipePageResponse> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal(expression = "id") Long userId) {

        RecipePageResponse pageResponse = recipeService.getAllRecipes(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(pageResponse);
    }

    // Actualizar receta
    @PutMapping("/{id}")
    public ResponseEntity<RecipeResponse> updateRecipe(
            @PathVariable Long id,
            @Valid @RequestBody RecipeRequest request,
            @AuthenticationPrincipal(expression = "id") Long userId) {

        RecipeResponse updated = recipeService.updateRecipe(id, request, userId);
        return ResponseEntity.ok(updated);
    }

    // Eliminar receta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(
            @PathVariable Long id,
            @AuthenticationPrincipal(expression = "id") Long userId) {

        recipeService.deleteRecipe(id, userId);
        return ResponseEntity.noContent().build();
    }
}

