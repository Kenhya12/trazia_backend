package com.trazia.trazia_project.service.impl;

import com.trazia.trazia_project.service.batch.RawMaterialBatchService;
import com.trazia.trazia_project.repository.rawmaterial.RawMaterialBatchRepository;
import com.trazia.trazia_project.dto.batch.RawMaterialBatchDTO;
import com.trazia.trazia_project.entity.batch.RawMaterialBatch;
import com.trazia.trazia_project.entity.user.Supplier;
import com.trazia.trazia_project.service.SupplierService;

import lombok.RequiredArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class RawMaterialBatchServiceImpl implements RawMaterialBatchService {

    private final RawMaterialBatchRepository rawMaterialBatchRepository;
    private final SupplierService supplierService;

    @Override
    public List<RawMaterialBatch> getAllBatches() {
        return rawMaterialBatchRepository.findAll();
    }

    @Override
    public RawMaterialBatch saveRawMaterialBatch(@NonNull RawMaterialBatch batch) {
        validateBatch(batch);
        return rawMaterialBatchRepository.save(batch);
    }

    @Override
    public RawMaterialBatch save(@NonNull RawMaterialBatch batch) {
        return rawMaterialBatchRepository.save(batch);
    }

    @Override
    public Optional<RawMaterialBatch> findById(@NonNull Long id) {
        return rawMaterialBatchRepository.findById(id);
    }

    @Override
    public List<RawMaterialBatch> findAll() {
        return rawMaterialBatchRepository.findAll();
    }

    @Override
    public boolean existsById(Long id) {
        return rawMaterialBatchRepository.existsById(id);
    }

    @Override
    public RawMaterialBatchDTO convertToDTO(RawMaterialBatch batch) {
        if (batch == null) return null;
        
        return RawMaterialBatchDTO.builder()
            .id(batch.getId())
            .batchNumber(batch.getBatchNumber())
            .quantity(batch.getQuantity())
            .expirationDate(batch.getExpirationDate())
            .receivingDate(batch.getReceivingDate())
            .purchaseDate(batch.getPurchaseDate())
            .supplierName(batch.getSupplier() != null ? batch.getSupplier().getName() : null)
            .supplierId(batch.getSupplier() != null ? String.valueOf(batch.getSupplier().getId()) : null)
            .comments(batch.getComments())
            .name(batch.getName())
            .unit(batch.getUnit())
            .build();
    }

    @Override
    @Transactional
    public RawMaterialBatch saveFromDTO(RawMaterialBatchDTO dto) {
        if (dto == null) return null;
        
        RawMaterialBatch batch = convertToEntity(dto);
        return rawMaterialBatchRepository.save(batch);
    }

    @Override
    @Transactional
    public RawMaterialBatch convertToEntity(RawMaterialBatchDTO dto) {
        if (dto == null) return null;
        
        RawMaterialBatch batch = new RawMaterialBatch();
        
        // ✅ BUSCAR O CREAR SUPPLIER (GUARDÁNDOLO PRIMERO)
        if (dto.getSupplierId() != null && !dto.getSupplierId().isEmpty()) {
            Supplier supplier = findOrCreateAndSaveSupplier(dto);
            batch.setSupplier(supplier);
        } else {
            throw new RuntimeException("El ID del proveedor es obligatorio");
        }
        
        // ✅ SETEAR LOS DEMÁS CAMPOS
        batch.setBatchNumber(dto.getBatchNumber());
        batch.setName(dto.getName());
        batch.setQuantity(dto.getQuantity());
        batch.setUnit(dto.getUnit());
        batch.setExpirationDate(dto.getExpirationDate());
        batch.setReceivingDate(dto.getReceivingDate());
        batch.setPurchaseDate(dto.getPurchaseDate() != null ? dto.getPurchaseDate() : LocalDate.now());
        batch.setComments(dto.getComments());
        
        return batch;
    }

    /**
     * ✅ MÉTODO AUXILIAR MEJORADO - BUSCAR O CREAR Y GUARDAR SUPPLIER
     */
    @Transactional
    private Supplier findOrCreateAndSaveSupplier(RawMaterialBatchDTO dto) {
        String supplierId = dto.getSupplierId();
        
        // 1. Intentar buscar por ID numérico en la base de datos
        if (isNumeric(supplierId)) {
            try {
                Long id = Long.parseLong(supplierId);
                Optional<Supplier> existingSupplier = supplierService.getSupplierById(id);
                if (existingSupplier.isPresent()) {
                    return existingSupplier.get(); // ✅ Supplier existente
                }
            } catch (NumberFormatException e) {
                // Continuar con creación de nuevo supplier
            }
        }
        
        // 2. Crear NUEVO SUPPLIER y GUARDARLO primero
        Supplier newSupplier = createNewSupplier(dto);
        
        // 3. ✅ GUARDAR EL SUPPLIER ANTES de asociarlo al batch
        Supplier savedSupplier = supplierService.createSupplier(newSupplier);
        
        return savedSupplier; // ✅ Supplier persistido (no transient)
    }

    /**
     * ✅ CREAR NUEVO SUPPLIER BASADO EN LOS DATOS DEL DTO
     */
    private Supplier createNewSupplier(RawMaterialBatchDTO dto) {
        Supplier supplier = new Supplier();
        
        // Establecer nombre
        if (dto.getSupplierName() != null && !dto.getSupplierName().isEmpty()) {
            supplier.setName(dto.getSupplierName());
        } else {
            // Si no hay nombre, usar el ID como referencia
            supplier.setName("Proveedor " + dto.getSupplierId());
        }
        
        // Si el ID es numérico, intentar establecerlo
        if (isNumeric(dto.getSupplierId())) {
            try {
                // Nota: Esto puede fallar si el ID ya existe
                // En producción, mejor dejar que la BD genere el ID automáticamente
                supplier.setId(Long.parseLong(dto.getSupplierId()));
            } catch (Exception e) {
                // Si falla, dejar que la BD genere el ID
            }
        }
        
        return supplier;
    }

    /**
     * ✅ VERIFICAR SI UN STRING ES NUMÉRICO
     */
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void validateBatch(RawMaterialBatch batch) {
        if (batch == null) throw new IllegalArgumentException("RawMaterialBatch no puede ser nulo");
        if (batch.getBatchNumber() == null || batch.getBatchNumber().isEmpty())
            throw new IllegalArgumentException("El número de lote es obligatorio");
        if (batch.getQuantity() == null || batch.getQuantity() <= 0)
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        if (batch.getExpirationDate() == null)
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser nula");
        if (batch.getSupplier() == null)
            throw new IllegalArgumentException("El proveedor es obligatorio");
        if (batch.getReceivingDate() != null && batch.getPurchaseDate() != null &&
                batch.getReceivingDate().isBefore(batch.getPurchaseDate())) {
            throw new IllegalArgumentException("La fecha de recepción no puede ser anterior a la compra");
        }
    }
}