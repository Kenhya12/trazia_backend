package com.trazia.trazia_project.service;

import com.trazia.trazia_project.entity.RawMaterialBatch;
import com.trazia.trazia_project.repository.RawMaterialBatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RawMaterialBatchService {

    @Autowired
    private RawMaterialBatchRepository rawMaterialBatchRepository;

    public RawMaterialBatch saveRawMaterialBatch(RawMaterialBatch batch) {
        if (batch == null) {
            throw new IllegalArgumentException("RawMaterialBatch no puede ser nulo");
        }

        if (batch.getBatchNumber() == null || batch.getBatchNumber().isEmpty()) {
            throw new IllegalArgumentException("El número de lote es obligatorio");
        }

        if (batch.getQuantity() == null || batch.getQuantity() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        if (batch.getExpirationDate() == null) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser nula");
        }

        if (batch.getSupplier() == null || batch.getSupplier().getName().isEmpty()) {
            throw new IllegalArgumentException("El proveedor es obligatorio");
        }

        if (batch.getReceivingDate() != null && batch.getPurchaseDate() != null &&
                batch.getReceivingDate().isBefore(batch.getPurchaseDate())) {
            throw new IllegalArgumentException("La fecha de recepción no puede ser anterior a la compra");
        }

        return rawMaterialBatchRepository.save(batch);
    }

    // Método público save() compatible con tests de Mockito
    public RawMaterialBatch save(RawMaterialBatch batch) {
        return rawMaterialBatchRepository.save(batch);
    }

    public Optional<RawMaterialBatch> findById(Long id) {
        return rawMaterialBatchRepository.findById(id);
    }

    public List<RawMaterialBatch> findAll() {
        return rawMaterialBatchRepository.findAll();
    }

    public List<RawMaterialBatch> getAllBatches() {
        return rawMaterialBatchRepository.findAll();
    }

    // Otros métodos del servicio según necesidades
}
