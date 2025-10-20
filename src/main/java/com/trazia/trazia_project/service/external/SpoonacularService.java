package com.trazia.trazia_project.service.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Servicio para integración con Spoonacular API
 * Proporciona análisis dietético y nutricional de recetas
 */
@Service
public class SpoonacularService {

    private final WebClient webClient;
    private final String apiKey;

    // URL del endpoint de análisis de recetas
    private static final String API_URL = "https://api.spoonacular.com/recipes/analyze";

    public SpoonacularService(@Value("${spoonacular.api.key}") String apiKey) {
        this.webClient = WebClient.builder().baseUrl(API_URL).build();
        this.apiKey = apiKey;
    }

    /**
     * TODO: Implementar análisis dietético con Spoonacular
     * 
     * Este método llamará a Spoonacular para obtener etiquetas dietéticas y Health Score.
     * Requiere:
     * - Crear DTO DietaryAnalysis en com.trazia.trazia_project.dto.recipe
     * - Implementar SpoonacularRequest y SpoonacularResponse DTOs
     * - Mapear la respuesta de la API a nuestros DTOs internos
     * 
     * @param ingredientLines Lista de ingredientes en formato de texto (ej: "100g de harina")
     * @return DTO de análisis dietético enriquecido
     */
    // public DietaryAnalysis getDietaryAnalysis(List<String> ingredientLines) {
    //     // Implementación pendiente
    // }
}
