package com.trazia.trazia_project.service.external;

import com.trazia.trazia_project.dto.recipe.*;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Slf4j
@Service
public class SpoonacularService {

    private final WebClient webClient;
    private final String apiKey;

    private static final String API_URL = "https://api.spoonacular.com/recipes/analyze";

    public SpoonacularService(@Value("${spoonacular.api.key}") String apiKey) {
        this.webClient = WebClient.builder().baseUrl(API_URL).build();
        this.apiKey = apiKey;
    }

    public DietaryAnalysis getDietaryAnalysis(List<String> ingredientLines) {
        try {
            SpoonacularRequest request = new SpoonacularRequest(ingredientLines);

            SpoonacularResponse response = webClient.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("apiKey", apiKey).build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(request), SpoonacularRequest.class)
                    .retrieve()
                    .bodyToMono(SpoonacularResponse.class)
                    .block();

            if (response == null) {
                log.warn("Respuesta vacía de Spoonacular");
                return new DietaryAnalysis();
            }

            return DietaryAnalysis.builder()
                    .calories(response.getCalories())
                    .fat(response.getFat())
                    .protein(response.getProtein())
                    .carbs(response.getCarbs())
                    .dietLabels(response.getDiets())
                    .healthScore(String.valueOf(response.getHealthScore()))
                    .build();

        } catch (Exception ex) {
            log.error("Error al conectar con Spoonacular API: {}", ex.getMessage(), ex);
            throw new RuntimeException("Fallo al obtener análisis dietético desde Spoonacular", ex);
        }
    }
}