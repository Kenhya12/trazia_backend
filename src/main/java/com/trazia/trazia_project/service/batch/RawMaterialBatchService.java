package com.trazia.trazia_project.service.batch;

import com.trazia.trazia_project.dto.batch.RawMaterialBatchDTO;
import com.trazia.trazia_project.entity.batch.RawMaterialBatch;

import java.util.List;
import java.util.Optional;

public interface RawMaterialBatchService {
    List<RawMaterialBatch> getAllBatches();
    RawMaterialBatch saveRawMaterialBatch(RawMaterialBatch batch);
    RawMaterialBatch save(RawMaterialBatch batch);
    Optional<RawMaterialBatch> findById(Long id);
    List<RawMaterialBatch> findAll();
    RawMaterialBatchDTO convertToDTO(RawMaterialBatch batch);
    RawMaterialBatch saveFromDTO(RawMaterialBatchDTO dto);
    RawMaterialBatch convertToEntity(RawMaterialBatchDTO dto);
    boolean existsById(Long id); 
}