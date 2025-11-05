package com.trazia.trazia_project.service.impl;

import com.trazia.trazia_project.dto.product.LabelPrintDTO;  // ✅ CORRECTO
import com.trazia.trazia_project.entity.product.ProductLabel;  // ✅ CORRECTO  
import com.trazia.trazia_project.repository.product.ProductLabelRepository;  // ✅ CORRECTO
import com.trazia.trazia_project.service.product.ProductLabelService;  // ✅ CORRECTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ProductLabelServiceImpl implements ProductLabelService {

    @Autowired
    private ProductLabelRepository productLabelRepository;

    @Override
    public List<LabelPrintDTO> findAllByCurrentUser() {
        // TODO: Filtrar por usuario autenticado
        return productLabelRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public LabelPrintDTO findById(String id) {
        ProductLabel label = productLabelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Label not found with id: " + id));
        return convertToDTO(label);
    }

    @Override
    public LabelPrintDTO create(LabelPrintDTO labelDTO) {
        ProductLabel label = convertToEntity(labelDTO);
        label.setId(UUID.randomUUID().toString());
        label.setVersion(1);
        label.setCreatedAt(LocalDate.now());
        label.setUpdatedAt(LocalDate.now());
        
        ProductLabel savedLabel = productLabelRepository.save(label);
        return convertToDTO(savedLabel);
    }

    @Override
    public LabelPrintDTO update(String id, LabelPrintDTO labelDTO) {
        ProductLabel existingLabel = productLabelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Label not found with id: " + id));
        
        // Actualizar campos
        existingLabel.setProductName(labelDTO.getProductName());
        existingLabel.setCompanyName(labelDTO.getCompanyName());
        existingLabel.setCompanyAddress(labelDTO.getCompanyAddress());
        existingLabel.setCountryOfOrigin(labelDTO.getCountryOfOrigin());
        existingLabel.setBatchNumber(labelDTO.getBatchNumber());
        existingLabel.setExpiryDate(labelDTO.getExpiryDate());
        existingLabel.setIngredients(labelDTO.getIngredients());
        existingLabel.setAllergens(labelDTO.getAllergens());
        existingLabel.setLanguage(labelDTO.getLanguage());
        existingLabel.setStatus(labelDTO.getStatus());
        existingLabel.setVersion(existingLabel.getVersion() + 1);
        existingLabel.setUpdatedAt(LocalDate.now());
        
        ProductLabel updatedLabel = productLabelRepository.save(existingLabel);
        return convertToDTO(updatedLabel);
    }

    @Override
    public void delete(String id) {
        ProductLabel label = productLabelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Label not found with id: " + id));
        productLabelRepository.delete(label);
    }

    @Override
    public byte[] generatePdf(String id) {
        // TODO: Implementar generación de PDF
        // Por ahora retornar array vacío para testing
        return new byte[0];
    }

    private LabelPrintDTO convertToDTO(ProductLabel label) {
        return LabelPrintDTO.builder()
                .id(label.getId())
                .productName(label.getProductName())
                .companyName(label.getCompanyName())
                .companyAddress(label.getCompanyAddress())
                .countryOfOrigin(label.getCountryOfOrigin())
                .batchNumber(label.getBatchNumber())
                .expiryDate(label.getExpiryDate())
                .ingredients(label.getIngredients())
                .allergens(label.getAllergens())
                .language(label.getLanguage())
                .status(label.getStatus())
                .version(label.getVersion())
                .createdAt(label.getCreatedAt())
                .updatedAt(label.getUpdatedAt())
                .build();
    }

    private ProductLabel convertToEntity(LabelPrintDTO dto) {
        ProductLabel label = new ProductLabel();
        label.setProductName(dto.getProductName());
        label.setCompanyName(dto.getCompanyName());
        label.setCompanyAddress(dto.getCompanyAddress());
        label.setCountryOfOrigin(dto.getCountryOfOrigin());
        label.setBatchNumber(dto.getBatchNumber());
        label.setExpiryDate(dto.getExpiryDate());
        label.setIngredients(dto.getIngredients());
        label.setAllergens(dto.getAllergens());
        label.setLanguage(dto.getLanguage());
        label.setStatus(dto.getStatus());
        return label;
    }
}