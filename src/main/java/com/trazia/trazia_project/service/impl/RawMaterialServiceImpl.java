package com.trazia.trazia_project.service.impl;

import com.trazia.trazia_project.entity.material.RawMaterial;
import com.trazia.trazia_project.entity.user.Supplier;
import com.trazia.trazia_project.dto.material.RawMaterialDTO;
import com.trazia.trazia_project.repository.material.RawMaterialRepository;
import com.trazia.trazia_project.repository.user.SupplierRepository;
import com.trazia.trazia_project.service.material.RawMaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RawMaterialServiceImpl implements RawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;
    private final SupplierRepository supplierRepository;

    @Override
    public List<RawMaterial> findAll() {
        return rawMaterialRepository.findAll();
    }

    @Override
    public Optional<RawMaterial> findById(@NonNull Long id) {
        return rawMaterialRepository.findById(id);
    }

    @Override
    public RawMaterial save(@NonNull RawMaterial rawMaterial) {
        return rawMaterialRepository.save(rawMaterial);
    }

    @Override
    public void deleteById(@NonNull Long id) {
        rawMaterialRepository.deleteById(id);
    }

    @Override
    public boolean existsById(@NonNull Long id) {
        return rawMaterialRepository.existsById(id);
    }

    @Override
public RawMaterialDTO convertToDTO(RawMaterial rawMaterial) {
    return RawMaterialDTO.builder()
            .id(rawMaterial.getId())
            .name(rawMaterial.getName())
            .description(rawMaterial.getDescription())
            .costPerUnit(rawMaterial.getCostPerUnit())
            .unit(rawMaterial.getUnit())
            .category(rawMaterial.getCategory())
            .internalCode(rawMaterial.getInternalCode())
            .minStock(rawMaterial.getMinStock())
            .currentStock(rawMaterial.getCurrentStock())
            .supplierId(rawMaterial.getSupplier() != null ? rawMaterial.getSupplier().getId() : null)
            .supplierName(rawMaterial.getSupplier() != null ? rawMaterial.getSupplier().getName() : null)
            .createdAt(rawMaterial.getCreatedAt())
            .updatedAt(rawMaterial.getUpdatedAt())
            .build();
}

    @Override
    public RawMaterial convertToEntity(RawMaterialDTO rawMaterialDTO) {
        RawMaterial rawMaterial = RawMaterial.builder()
                .id(rawMaterialDTO.getId())
                .name(rawMaterialDTO.getName())
                .description(rawMaterialDTO.getDescription())
                .costPerUnit(rawMaterialDTO.getCostPerUnit())
                .unit(rawMaterialDTO.getUnit())
                .category(rawMaterialDTO.getCategory())
                .internalCode(rawMaterialDTO.getInternalCode())
                .minStock(rawMaterialDTO.getMinStock())
                .currentStock(rawMaterialDTO.getCurrentStock())
                .build();

        // Set supplier if supplierId is provided and exists
        if (rawMaterialDTO.getSupplierId() != null) {
            try {
                Supplier supplier = supplierRepository.findById(rawMaterialDTO.getSupplierId())
                        .orElse(null);
                rawMaterial.setSupplier(supplier);
            } catch (Exception e) {
                log.warn("Supplier with id {} not found, setting supplier to null", rawMaterialDTO.getSupplierId());
                rawMaterial.setSupplier(null);
            }
        }

        return rawMaterial;
    }
}