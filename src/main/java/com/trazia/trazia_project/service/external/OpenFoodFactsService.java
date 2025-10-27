package com.trazia.trazia_project.service.external;

import com.trazia.trazia_project.dto.external.openfoodfacts.OpenFoodFactsProductDTO;
import com.trazia.trazia_project.dto.external.openfoodfacts.OpenFoodFactsResponseDTO;
import com.trazia.trazia_project.dto.external.openfoodfacts.OpenFoodFactsSearchResultDTO;
import com.trazia.trazia_project.exception.product.OpenFoodFactsApiException;
import com.trazia.trazia_project.exception.product.ProductNotFoundException;

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
    
    @Value("${openfoodfacts.api.timeout-seconds:30}")
    private int apiTimeoutSeconds;

    @Value("${openfoodfacts.api.retry-delay-seconds:2}")
    private int retryDelaySeconds;
    
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
    
    /**
     * Busca un producto en Open Food Facts por código de barras.
     * @param barcode Código de barras del producto.
     * @return DTO con la información del producto.
     * @throws IllegalArgumentException si el código de barras es nulo o vacío.
     * @throws ProductNotFoundException si el producto no se encuentra.
     * @throws OpenFoodFactsApiException si ocurre un error en la llamada a la API.
     */
    @Cacheable(value = "openFoodFactsProducts", key = "#barcode")
    public OpenFoodFactsProductDTO searchByBarcode(String barcode) {
        if (barcode == null || barcode.isBlank()) {
            throw new IllegalArgumentException("Barcode cannot be null or empty");
        }
        long start = System.currentTimeMillis();
        log.info("Searching Open Food Facts for barcode: {}", barcode);
        
        try {
            OpenFoodFactsResponseDTO response = webClient.get()
                .uri("/api/v0/product/{barcode}.json", barcode)
                .header("User-Agent", userAgent)
                .retrieve()
                .bodyToMono(OpenFoodFactsResponseDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(retryDelaySeconds))
                    .maxBackoff(Duration.ofSeconds(4))
                    .filter(throwable -> !(throwable instanceof ProductNotFoundException)))
                .timeout(Duration.ofSeconds(apiTimeoutSeconds))
                .block();
            
            log.info("OpenFoodFacts barcode lookup took {} ms", System.currentTimeMillis() - start);
            
            if (response == null || response.getStatus() == 0 || response.getProduct() == null) {
                log.warn("Product not found in Open Food Facts: {}", barcode);
                throw new ProductNotFoundException(barcode);
            }
            
            log.info("Product found in Open Food Facts: {}", response.getProduct().getProductName());
            return response.getProduct();
            
        } catch (Exception e) {
            if (e instanceof ProductNotFoundException pnf) throw pnf;
            log.error("Error calling Open Food Facts API for barcode {}: {}", barcode, e.getMessage());
            throw new OpenFoodFactsApiException("Failed to search product", e);
        }
    }
    
    /**
     * Busca productos en Open Food Facts por nombre.
     * @param query Término de búsqueda.
     * @param pageSize Tamaño de página máximo 100.
     * @return Resultado de búsqueda con lista de productos.
     * @throws IllegalArgumentException si la consulta es nula o vacía.
     * @throws OpenFoodFactsApiException si ocurre un error en la llamada a la API.
     */
    @Cacheable(value = "openFoodFactsSearch", key = "#query + '_' + #pageSize")
    public OpenFoodFactsSearchResultDTO searchByName(String query, int pageSize) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Search query cannot be null or empty");
        }
        long start = System.currentTimeMillis();
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
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(retryDelaySeconds)))
                .timeout(Duration.ofSeconds(apiTimeoutSeconds))
                .block();
            
            log.info("OpenFoodFacts name search took {} ms", System.currentTimeMillis() - start);
            
            if (result == null) {
                throw new OpenFoodFactsApiException("No response received from Open Food Facts");
            }
            
            log.info("Found {} products for query: {}", 
                result.getCount(), query);
            
            return result;
            
        } catch (Exception e) {
            log.error("Error searching Open Food Facts for query {}: {}", query, e.getMessage());
            throw new OpenFoodFactsApiException("Failed to search by name", e);
        }
    }
}
