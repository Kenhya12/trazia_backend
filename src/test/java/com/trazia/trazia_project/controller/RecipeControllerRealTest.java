package com.trazia.trazia_project.controller;

import com.trazia.trazia_project.dto.recipe.LabelPrintDTO;
import com.trazia.trazia_project.exception.ResourceNotFoundException;
import com.trazia.trazia_project.service.RecipeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test que EJECUTA el código REAL del RecipeController
 */
class RecipeControllerRealTest {

    @Test
    void testRecipeControllerRealCodeExecution() {
        System.out.println("=== 🧪 EJECUCIÓN REAL DEL CONTROLLER ===");
        
        // 1. Setup REAL
        RecipeService service = mock(RecipeService.class);
        RecipeController controller = new RecipeController(service); // Controller REAL
        
        // 2. Mock data
        LabelPrintDTO mockLabel = LabelPrintDTO.builder()
                .productName("Pizza Real Test")
                .energyPer100g(BigDecimal.valueOf(250.0))
                .vegetarian(true)
                .nutriScore("A")
                .build();
        
        when(service.generateLabel(1L, 1L)).thenReturn(mockLabel);
        
        // 3. EJECUTAR CÓDIGO REAL DEL CONTROLLER - SIN FALLBACK
        ResponseEntity<LabelPrintDTO> response = controller.getRecipeLabel(1L, 1L);
        
        // 4. Verificaciones
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());
        
        LabelPrintDTO body = response.getBody();
        assertNotNull(body);
        assertEquals("Pizza Real Test", body.getProductName());
        assertEquals(BigDecimal.valueOf(250.0), body.getEnergyPer100g());
        
        System.out.println("✅ CÓDIGO REAL DEL CONTROLLER EJECUTADO");
    }

    @Test
    void testRecipeControllerRealWithException() {
        System.out.println("=== 🧪 CONTROLLER REAL CON EXCEPCIÓN ===");
        
        RecipeService service = mock(RecipeService.class);
        RecipeController controller = new RecipeController(service);
        
        // Configurar excepción REAL
        when(service.generateLabel(999L, 1L))
                .thenThrow(new ResourceNotFoundException("Receta no existe"));
        
        // Ejecutar código REAL que debe manejar la excepción
        ResponseEntity<LabelPrintDTO> response = controller.getRecipeLabel(999L, 1L);
        
        // Verificar que el controller maneja la excepción correctamente
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.hasBody());
        
        System.out.println("✅ CONTROLLER MANEJA EXCEPCIÓN CORRECTAMENTE");
    }

    @Test
    void testRecipeControllerRealWithServerError() {
        System.out.println("=== 🧪 CONTROLLER REAL CON ERROR SERVIDOR ===");
        
        RecipeService service = mock(RecipeService.class);
        RecipeController controller = new RecipeController(service);
        
        // Configurar error REAL
        when(service.generateLabel(500L, 1L))
                .thenThrow(new RuntimeException("Error de base de datos"));
        
        // Ejecutar código REAL
        ResponseEntity<LabelPrintDTO> response = controller.getRecipeLabel(500L, 1L);
        
        // Verificar manejo de error
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.hasBody());
        
        System.out.println("✅ CONTROLLER MANEJA ERROR DE SERVIDOR");
    }
}