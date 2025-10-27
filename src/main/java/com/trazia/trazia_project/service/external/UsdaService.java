package com.trazia.trazia_project.service.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.trazia.trazia_project.dto.external.usda.UsdaFoodDTO;
import com.trazia.trazia_project.dto.external.usda.UsdaSearchResponseDTO;

import java.net.URI;
import java.util.List;

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

    @Cacheable(value = "usdaSearchCache", key = "#query")
    public List<UsdaFoodDTO> searchFoods(String query) {
        try {
            log.info("Searching USDA FoodData Central for: {}", query);

            // ✅ FORMA MODERNA - SIN fromHttpUrl()
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
            log.error("Error searching USDA FoodData Central: {}", e.getMessage());
            throw new RuntimeException("Failed to search USDA database", e);
        }
    }

    public UsdaFoodDTO getFoodById(Long fdcId) {
        try {
            log.info("Fetching USDA food with ID: {}", fdcId);

            // ✅ FORMA MODERNA - SIN fromHttpUrl()
            URI uri = UriComponentsBuilder
                    .fromUriString(apiUrl + "/food/" + fdcId)
                    .queryParam("api_key", apiKey)
                    .build()
                    .toUri();

            return restTemplate.getForObject(uri, UsdaFoodDTO.class);

        } catch (Exception e) {
            log.error("Error fetching USDA food by ID: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch USDA food", e);
        }
    }
}
