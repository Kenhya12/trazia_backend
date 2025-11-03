package com.trazia.trazia_project.service.retention;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Getter
@Service
public class RetentionService {

    @Data
    public static class RetentionFactor {
        // Nombre o código del nutriente (ej. "Vitamin C" o "401")
        private String nutrient;
        // Tipo de procesamiento (ej. "Boiling", "Baking", "Frying")
        private String processingType;
        // Factor de retención como fracción decimal (ej. 0.85 para 85%)
        private BigDecimal retentionFactor;
    }

    private List<RetentionFactor> retentionFactors;

    @Autowired
    private ResourceLoader resourceLoader;

    public RetentionService() {
        // Constructor vacío, no cargar nada aquí
    }

    @PostConstruct
    private void init() {
        loadRetentionFactors();
    }

    private void loadRetentionFactors() {
        String filePath = "classpath:retention/RetentionFactorConfig.json";
        ObjectMapper mapper = new ObjectMapper();
        try {
            Resource resource = resourceLoader.getResource(filePath);
            if (!resource.exists()) {
                throw new IllegalArgumentException("Retention config file not found: " + filePath);
            }
            try (InputStream is = resource.getInputStream()) {
                retentionFactors = mapper.readValue(is, new TypeReference<List<RetentionFactor>>() {});
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading retention factors", e);
        }
    }

    /**
     * Aplica retención sobre un nutriente considerando su cantidad inicial
     * y el tipo de procesamiento
     */
    public BigDecimal applyRetention(String nutrient, String processingType, BigDecimal initialAmount, BigDecimal yield) {
        RetentionFactor factor = retentionFactors.stream()
                .filter(f -> f.getNutrient().equalsIgnoreCase(nutrient)
                        && f.getProcessingType().equalsIgnoreCase(processingType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Nutrient not found: " + nutrient + " for processing: " + processingType));

        // Ajuste por factor de retención y yield final (pérdida de agua)
        return initialAmount.multiply(factor.getRetentionFactor()).multiply(yield);
    }
}
