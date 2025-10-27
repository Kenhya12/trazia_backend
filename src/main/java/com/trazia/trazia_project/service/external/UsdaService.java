package com.trazia.trazia_project.service.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.trazia.trazia_project.exception.external.UsdaApiException;

import com.trazia.trazia_project.dto.external.usda.UsdaFoodDTO;
import com.trazia.trazia_project.dto.external.usda.UsdaSearchResponseDTO;

import java.net.URI;
import java.util.List;
import java.util.Objects;

/**
 * Servicio para interactuar con la API de USDA FoodData Central.
 */
@Slf4j
@Service
public class UsdaService {

    @Value("${usda.api.key}")
    private String apiKey;

    @Value("${usda.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public UsdaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Busca alimentos en USDA por texto de búsqueda.
     * Se cachea por query para optimizar rendimiento.
     *
     * @param query Texto de búsqueda
     * @return Lista de alimentos encontrados
     */
    @Cacheable(value = "usdaFoodSearchCache", key = "#query")
    public List<UsdaFoodDTO> searchFoods(String query) {
        if (query == null || query.isBlank()) {
            log.warn("Query for USDA search is empty or null");
            return List.of();
        }

        try {
            log.info("Searching USDA FoodData Central for: {}", query);

            URI uri = UriComponentsBuilder
                    .fromUriString(apiUrl + "/foods/search")
                    .queryParam("api_key", apiKey)
                    .queryParam("query", query)
                    .queryParam("pageSize", 25)
                    .queryParam("dataType", "Survey (FNDDS),Foundation,SR Legacy")
                    .build()
                    .toUri();

            UsdaSearchResponseDTO response = restTemplate.getForObject(uri, UsdaSearchResponseDTO.class);

            if (response != null && response.getFoods() != null) {
                log.info("Found {} foods in USDA database", response.getFoods().size());
                return response.getFoods();
            }

            log.warn("No foods found in USDA for query: {}", query);
            return List.of();

        } catch (Exception e) {
            log.error("Error searching USDA FoodData Central: {}", e.getMessage(), e);
            throw new UsdaApiException("Failed to search USDA database", e);
        }
    }

    /**
     * Obtiene un alimento por su ID en USDA.
     *
     * @param fdcId ID del alimento
     * @return DTO del alimento
     */
    public UsdaFoodDTO getFoodById(Long fdcId) {
        if (fdcId == null || fdcId <= 0) {
            log.warn("Invalid USDA FDC ID: {}", fdcId);
            throw new IllegalArgumentException("FDC ID must be positive and non-null");
        }

        try {
            log.info("Fetching USDA food with ID: {}", fdcId);

            URI uri = UriComponentsBuilder
                    .fromUriString(apiUrl + "/food/" + fdcId)
                    .queryParam("api_key", apiKey)
                    .build()
                    .toUri();

            UsdaFoodDTO food = restTemplate.getForObject(uri, UsdaFoodDTO.class);
            return Objects.requireNonNullElse(food, new UsdaFoodDTO());

        } catch (Exception e) {
            log.error("Error fetching USDA food by ID: {}", e.getMessage(), e);
            throw new UsdaApiException("Failed to fetch USDA food", e);
        }
    }
}
