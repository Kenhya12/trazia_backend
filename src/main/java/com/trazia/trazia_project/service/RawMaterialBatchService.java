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
