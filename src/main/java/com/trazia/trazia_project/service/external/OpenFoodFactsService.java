package com.trazia.trazia_project.service.external;

import com.trazia.trazia_project.dto.openfoodfacts.OpenFoodFactsProductDTO;
import com.trazia.trazia_project.dto.openfoodfacts.OpenFoodFactsResponseDTO;
import com.trazia.trazia_project.dto.openfoodfacts.OpenFoodFactsSearchResultDTO;
import com.trazia.trazia_project.exception.OpenFoodFactsApiException;
import com.trazia.trazia_project.exception.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@Slf4j
public class OpenFoodFactsService {
    
    private final WebClient webClient;
    
    @Value("${openfoodfacts.api.user-agent}")
    private String userAgent;
    
    public OpenFoodFactsService(@Value("${openfoodfacts.api.base-url}") String baseUrl) {
        // Aumentar límite de buffer a 10 MB
        ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(configurer -> configurer
                .defaultCodecs()
                .maxInMemorySize(10 * 1024 * 1024))
            .build();
        
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .exchangeStrategies(strategies)
            .build();
    }
    
    @Cacheable(value = "openFoodFactsProducts", key = "#barcode")
    public OpenFoodFactsProductDTO searchByBarcode(String barcode) {
        log.info("Searching Open Food Facts for barcode: {}", barcode);
        
        try {
            OpenFoodFactsResponseDTO response = webClient.get()
                .uri("/api/v0/product/{barcode}.json", barcode)
                .header("User-Agent", userAgent)
                .retrieve()
                .bodyToMono(OpenFoodFactsResponseDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                    .maxBackoff(Duration.ofSeconds(4))
                    .filter(throwable -> !(throwable instanceof ProductNotFoundException)))
                .timeout(Duration.ofSeconds(30))
                .block();
            
            if (response == null || response.getStatus() == 0 || response.getProduct() == null) {
                log.warn("Product not found in Open Food Facts: {}", barcode);
                throw new ProductNotFoundException(barcode);
            }
            
            log.info("Product found in Open Food Facts: {}", response.getProduct().getProductName());
            return response.getProduct();
            
        } catch (Exception e) {
            if (e instanceof ProductNotFoundException) {
                throw e;
            }
            log.error("Error calling Open Food Facts API for barcode {}: {}", barcode, e.getMessage());
            throw new OpenFoodFactsApiException("Failed to search product", e);
        }
    }
    
    @Cacheable(value = "openFoodFactsSearch", key = "#query + '_' + #pageSize")
    public OpenFoodFactsSearchResultDTO searchByName(String query, int pageSize) {
        log.info("Searching Open Food Facts for query: {}", query);
        
        final int finalPageSize = Math.min(pageSize, 100);
        
        try {
            OpenFoodFactsSearchResultDTO result = webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/cgi/search.pl")
                    .queryParam("search_terms", query)
                    .queryParam("page_size", finalPageSize)
                    .queryParam("json", 1)
                    .build())
                .header("User-Agent", userAgent)
                .retrieve()
                .bodyToMono(OpenFoodFactsSearchResultDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
                .timeout(Duration.ofSeconds(30))
                .block();
            
            log.info("Found {} products for query: {}", 
                result != null ? result.getCount() : 0, query);
            
            return result;
            
        } catch (Exception e) {
            log.error("Error searching Open Food Facts for query {}: {}", query, e.getMessage());
            throw new OpenFoodFactsApiException("Failed to search by name", e);
        }
    }
}



