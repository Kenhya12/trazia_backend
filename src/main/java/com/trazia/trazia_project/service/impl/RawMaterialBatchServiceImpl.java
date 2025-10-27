package com.trazia.trazia_project.service.impl;

import com.trazia.trazia_project.service.batch.RawMaterialBatchService;
import com.trazia.trazia_project.repository.rawmaterial.RawMaterialBatchRepository;
import com.trazia.trazia_project.dto.batch.RawMaterialBatchDTO;
import com.trazia.trazia_project.entity.batch.RawMaterialBatch;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RawMaterialBatchServiceImpl implements RawMaterialBatchService {

    private final RawMaterialBatchRepository rawMaterialBatchRepository;

    @Override
    public List<RawMaterialBatch> getAllBatches() {
        return rawMaterialBatchRepository.findAll();
    }

    @Override
    public RawMaterialBatch saveRawMaterialBatch(RawMaterialBatch batch) {
        validateBatch(batch);
        return rawMaterialBatchRepository.save(batch);
    }

    @Override
    public RawMaterialBatch save(RawMaterialBatch batch) {
        return rawMaterialBatchRepository.save(batch);
    }

    @Override
    public Optional<RawMaterialBatch> findById(Long id) {
        return rawMaterialBatchRepository.findById(id);
    }

    @Override
    public List<RawMaterialBatch> findAll() {
        return rawMaterialBatchRepository.findAll();
    }

    @Override
    public RawMaterialBatchDTO convertToDTO(RawMaterialBatch batch) {
        if (batch == null) return null;
        RawMaterialBatchDTO dto = new RawMaterialBatchDTO();
        dto.setId(batch.getId());
        dto.setName(batch.getName());
        dto.setQuantity(batch.getQuantity());
        dto.setUnit(batch.getUnit());
        return dto;
    }

    @Override
    public RawMaterialBatch saveFromDTO(RawMaterialBatchDTO dto) {
        if (dto == null) return null;
        RawMaterialBatch batch = new RawMaterialBatch();
        batch.setName(dto.getName());
        batch.setQuantity(dto.getQuantity());
        batch.setUnit(dto.getUnit());
        return rawMaterialBatchRepository.save(batch);
    }

    private void validateBatch(RawMaterialBatch batch) {
        if (batch == null) throw new IllegalArgumentException("RawMaterialBatch no puede ser nulo");
        if (batch.getBatchNumber() == null || batch.getBatchNumber().isEmpty())
            throw new IllegalArgumentException("El número de lote es obligatorio");
        if (batch.getQuantity() == null || batch.getQuantity() <= 0)
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        if (batch.getExpirationDate() == null)
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser nula");
        if (batch.getSupplier() == null || batch.getSupplier().getName().isEmpty())
            throw new IllegalArgumentException("El proveedor es obligatorio");
        if (batch.getReceivingDate() != null && batch.getPurchaseDate() != null &&
                batch.getReceivingDate().isBefore(batch.getPurchaseDate())) {
            throw new IllegalArgumentException("La fecha de recepción no puede ser anterior a la compra");
        }
    }
}