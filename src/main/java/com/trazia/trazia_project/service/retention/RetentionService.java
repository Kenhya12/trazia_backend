package com.trazia.trazia_project.service.retention;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Data;

@Getter
public class RetentionService {

    @Data
    public static class RetentionFactor {
        // Nutrient name or code (e.g. "Vitamin C" or "401")
        private String nutrient;
        // Processing type (e.g. "Boiling", "Baking", "Frying")
        private String processingType;
        // Retention factor as a decimal fraction (e.g. 0.85 for 85%)
        private double retentionFactor;
    }

    private List<RetentionFactor> retentionFactors;

    public RetentionService(String retentionConfigFile) {
        loadRetentionFactors(retentionConfigFile);
    }

    private void loadRetentionFactors(String retentionConfigFile) {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(retentionConfigFile)) {
            if (is == null) {
                throw new IllegalArgumentException("Retention config file not found: " + retentionConfigFile);
            }
            retentionFactors = mapper.readValue(is, new TypeReference<List<RetentionFactor>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("Error reading retention factors", e);
        }
    }

    /**
     * Aplica retención sobre un nutriente considerando su cantidad inicial
     * y el tipo de procesamiento
     */
    public double applyRetention(String nutrient, String processingType, double initialAmount, double yield) {
        RetentionFactor factor = retentionFactors.stream()
                .filter(f -> f.getNutrient().equalsIgnoreCase(nutrient)
                        && f.getProcessingType().equalsIgnoreCase(processingType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Nutrient not found: " + nutrient + " for processing: " + processingType));

        // Ajuste por factor de retención y yield final (pérdida de agua)
        return initialAmount * factor.getRetentionFactor() * yield;
    }
}
