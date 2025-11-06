package com.trazia.trazia_project.service.impl;

import com.trazia.trazia_project.dto.product.LabelPrintDTO;
import com.trazia.trazia_project.dto.product.IngredientDTO;
import com.trazia.trazia_project.entity.product.ProductLabel;
import com.trazia.trazia_project.repository.product.ProductLabelRepository;
import com.trazia.trazia_project.service.product.ProductLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductLabelServiceImpl implements ProductLabelService {

    @Autowired
    private ProductLabelRepository productLabelRepository;

    @Override
    public List<LabelPrintDTO> findAllByCurrentUser() {
        System.out.println("📋 Finding all labels for current user");
        // TODO: Filtrar por usuario autenticado
        List<LabelPrintDTO> labels = productLabelRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        System.out.println("✅ Found " + labels.size() + " labels");
        return labels;
    }

    @Override
    public LabelPrintDTO findById(String id) {
        System.out.println("🔍 Finding label by ID: " + id);
        ProductLabel label = productLabelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Label not found with id: " + id));
        LabelPrintDTO dto = convertToDTO(label);
        System.out.println("✅ Label found: " + dto.getProductName());
        return dto;
    }

    @Override
    public LabelPrintDTO create(LabelPrintDTO labelDTO) {
        System.out.println("🔧 Creating label from DTO...");
        
        // Validar campos requeridos
        validateRequiredFields(labelDTO);
        
        ProductLabel label = convertToEntity(labelDTO);
        label.setId(UUID.randomUUID().toString());
        label.setVersion(1);
        label.setCreatedAt(LocalDate.now());
        label.setUpdatedAt(LocalDate.now());
        
        ProductLabel savedLabel = productLabelRepository.save(label);
        System.out.println("✅ Label saved with ID: " + savedLabel.getId());
        
        return convertToDTO(savedLabel);
    }

    @Override
    public LabelPrintDTO update(String id, LabelPrintDTO labelDTO) {
        System.out.println("🔧 Updating label with ID: " + id);
        
        // Validar campos requeridos
        validateRequiredFields(labelDTO);
        
        ProductLabel existingLabel = productLabelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Label not found with id: " + id));
        
        // Actualizar campos básicos
        existingLabel.setProductName(labelDTO.getProductName());
        existingLabel.setCompanyName(labelDTO.getCompanyName());
        existingLabel.setCompanyAddress(labelDTO.getCompanyAddress());
        existingLabel.setCountryOfOrigin(labelDTO.getCountryOfOrigin());
        existingLabel.setBatchNumber(labelDTO.getBatchNumber());
        existingLabel.setExpirationDate(labelDTO.getExpirationDate());
        existingLabel.setNetWeight(labelDTO.getNetWeight());
        existingLabel.setUsageInstructions(labelDTO.getUsageInstructions());
        existingLabel.setLegalDisclaimer(labelDTO.getLegalDisclaimer());
        existingLabel.setLanguage(labelDTO.getLanguage());
        existingLabel.setStatus(labelDTO.getStatus());
        
        // Actualizar información nutricional
        existingLabel.setEnergyKcalPer100g(labelDTO.getEnergyKcalPer100g());
        existingLabel.setEnergyKjPer100g(labelDTO.getEnergyKjPer100g());
        existingLabel.setFatPer100g(labelDTO.getFatPer100g());
        existingLabel.setSaturatedFatPer100g(labelDTO.getSaturatedFatPer100g());
        existingLabel.setCarbsPer100g(labelDTO.getCarbsPer100g());
        existingLabel.setSugarsPer100g(labelDTO.getSugarsPer100g());
        existingLabel.setProteinPer100g(labelDTO.getProteinPer100g());
        existingLabel.setSaltPer100g(labelDTO.getSaltPer100g());
        existingLabel.setFiberPer100g(labelDTO.getFiberPer100g());
        
        // Actualizar ingredientes
        if (labelDTO.getIngredients() != null) {
            List<ProductLabel.Ingredient> ingredients = labelDTO.getIngredients().stream()
                .map(ingDTO -> new ProductLabel.Ingredient(
                    ingDTO.getName(),
                    ingDTO.getQuantity(),
                    ingDTO.getIsAllergen() != null ? ingDTO.getIsAllergen() : false
                ))
                .collect(Collectors.toList());
            existingLabel.setIngredients(ingredients);
            
            // Calcular allergens automáticamente desde ingredients
            List<String> allergens = labelDTO.getIngredients().stream()
                .filter(ing -> ing.getIsAllergen() != null && ing.getIsAllergen())
                .map(IngredientDTO::getName)
                .collect(Collectors.toList());
            existingLabel.setAllergens(allergens);
        }
        
        existingLabel.setVersion(existingLabel.getVersion() + 1);
        existingLabel.setUpdatedAt(LocalDate.now());
        
        ProductLabel updatedLabel = productLabelRepository.save(existingLabel);
        System.out.println("✅ Label updated successfully");
        
        return convertToDTO(updatedLabel);
    }

    @Override
    public void delete(String id) {
        System.out.println("🗑️ Deleting label with ID: " + id);
        ProductLabel label = productLabelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Label not found with id: " + id));
        productLabelRepository.delete(label);
        System.out.println("✅ Label deleted successfully");
    }

    @Override
    public byte[] generatePdf(String id) {
        System.out.println("📄 Generating PDF for label ID: " + id);
        // TODO: Implementar generación de PDF
        // Por ahora retornar array vacío para testing
        return new byte[0];
    }

    private LabelPrintDTO convertToDTO(ProductLabel label) {
        LabelPrintDTO dto = new LabelPrintDTO();
        
        // Campos básicos
        dto.setProductName(label.getProductName());
        dto.setVersion(label.getVersion());
        dto.setStatus(label.getStatus());
        dto.setLanguage(label.getLanguage());
        dto.setCountryOfOrigin(label.getCountryOfOrigin());
        dto.setBatchNumber(label.getBatchNumber());
        dto.setNetWeight(label.getNetWeight());
        dto.setExpirationDate(label.getExpirationDate());
        dto.setCompanyName(label.getCompanyName());
        dto.setCompanyAddress(label.getCompanyAddress());
        dto.setUsageInstructions(label.getUsageInstructions());
        dto.setLegalDisclaimer(label.getLegalDisclaimer());
        
        // Ingredientes
        if (label.getIngredients() != null) {
            List<IngredientDTO> ingredientDTOs = label.getIngredients().stream()
                .map(ing -> new IngredientDTO(
                    ing.getName(),
                    ing.getQuantity(),
                    ing.getIsAllergen()
                ))
                .collect(Collectors.toList());
            dto.setIngredients(ingredientDTOs);
        }
        
        // Información nutricional
        dto.setEnergyKcalPer100g(label.getEnergyKcalPer100g());
        dto.setEnergyKjPer100g(label.getEnergyKjPer100g());
        dto.setFatPer100g(label.getFatPer100g());
        dto.setSaturatedFatPer100g(label.getSaturatedFatPer100g());
        dto.setCarbsPer100g(label.getCarbsPer100g());
        dto.setSugarsPer100g(label.getSugarsPer100g());
        dto.setProteinPer100g(label.getProteinPer100g());
        dto.setSaltPer100g(label.getSaltPer100g());
        dto.setFiberPer100g(label.getFiberPer100g());
        
        return dto;
    }

    private ProductLabel convertToEntity(LabelPrintDTO dto) {
        ProductLabel label = new ProductLabel();
        
        // Campos básicos
        label.setProductName(dto.getProductName());
        label.setVersion(dto.getVersion());
        label.setStatus(dto.getStatus());
        label.setLanguage(dto.getLanguage());
        label.setCountryOfOrigin(dto.getCountryOfOrigin());
        label.setBatchNumber(dto.getBatchNumber());
        label.setNetWeight(dto.getNetWeight());
        label.setExpirationDate(dto.getExpirationDate());
        label.setCompanyName(dto.getCompanyName());
        label.setCompanyAddress(dto.getCompanyAddress());
        label.setUsageInstructions(dto.getUsageInstructions());
        label.setLegalDisclaimer(dto.getLegalDisclaimer());
        
        // Información nutricional
        label.setEnergyKcalPer100g(dto.getEnergyKcalPer100g());
        label.setEnergyKjPer100g(dto.getEnergyKjPer100g());
        label.setFatPer100g(dto.getFatPer100g());
        label.setSaturatedFatPer100g(dto.getSaturatedFatPer100g());
        label.setCarbsPer100g(dto.getCarbsPer100g());
        label.setSugarsPer100g(dto.getSugarsPer100g());
        label.setProteinPer100g(dto.getProteinPer100g());
        label.setSaltPer100g(dto.getSaltPer100g());
        label.setFiberPer100g(dto.getFiberPer100g());
        
        // Ingredientes
        if (dto.getIngredients() != null) {
            List<ProductLabel.Ingredient> ingredients = dto.getIngredients().stream()
                .map(ingDTO -> new ProductLabel.Ingredient(
                    ingDTO.getName(),
                    ingDTO.getQuantity(),
                    ingDTO.getIsAllergen() != null ? ingDTO.getIsAllergen() : false
                ))
                .collect(Collectors.toList());
            label.setIngredients(ingredients);
            
            // Calcular allergens automáticamente desde ingredients
            List<String> allergens = dto.getIngredients().stream()
                .filter(ing -> ing.getIsAllergen() != null && ing.getIsAllergen())
                .map(IngredientDTO::getName)
                .collect(Collectors.toList());
            label.setAllergens(allergens);
        }
        
        return label;
    }

    private void validateRequiredFields(LabelPrintDTO labelDTO) {
        if (labelDTO.getProductName() == null || labelDTO.getProductName().trim().isEmpty()) {
            throw new RuntimeException("Product name is required");
        }
        if (labelDTO.getCountryOfOrigin() == null || labelDTO.getCountryOfOrigin().trim().isEmpty()) {
            throw new RuntimeException("Country of origin is required");
        }
        if (labelDTO.getBatchNumber() == null || labelDTO.getBatchNumber().trim().isEmpty()) {
            throw new RuntimeException("Batch number is required");
        }
        if (labelDTO.getIngredients() == null || labelDTO.getIngredients().isEmpty()) {
            throw new RuntimeException("At least one ingredient is required");
        }
        if (labelDTO.getLanguage() == null || labelDTO.getLanguage().trim().isEmpty()) {
            labelDTO.setLanguage("es"); // Valor por defecto
        }
        if (labelDTO.getStatus() == null || labelDTO.getStatus().trim().isEmpty()) {
            labelDTO.setStatus("draft"); // Valor por defecto
        }
        if (labelDTO.getVersion() == null) {
            labelDTO.setVersion(1); // Valor por defecto
        }
    }
}