package com.trazia.trazia_project.service.batch;

import com.trazia.trazia_project.entity.batch.FinalProductBatch;
import com.trazia.trazia_project.entity.batch.RawMaterialBatch;
import com.trazia.trazia_project.repository.product.FinalProductBatchRepository;
import com.trazia.trazia_project.repository.rawmaterial.RawMaterialBatchRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinalProductBatchService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final FinalProductBatchRepository finalProductBatchRepository;

    private final RawMaterialBatchRepository rawMaterialBatchRepository;

    public String generateBatchNumber() {
        String datePart = LocalDate.now().format(DATE_FORMATTER);
        long countToday = finalProductBatchRepository.countByProductionDate(LocalDate.now()) + 1;
        return "FPB-" + datePart + "-" + String.format("%04d", countToday);
    }

    public FinalProductBatch createFinalProductBatch(FinalProductBatch batch, List<Long> rawMaterialBatchIds) {
        if (rawMaterialBatchIds == null || rawMaterialBatchIds.isEmpty()) {
            throw new IllegalArgumentException("rawMaterialBatchIds must not be null or empty");
        }

        List<RawMaterialBatch> rawMaterials = rawMaterialBatchRepository.findAllById(rawMaterialBatchIds);
        if (rawMaterials.size() != rawMaterialBatchIds.size()) {
            throw new IllegalArgumentException("One or more rawMaterialBatchIds do not exist");
        }
        batch.setRawMaterialBatchesUsed(rawMaterials);
        String batchNumber = generateBatchNumber();
        log.info("Generated batch number: {}", batchNumber);
        batch.setBatchNumber(batchNumber);
        FinalProductBatch savedBatch = finalProductBatchRepository.save(batch);
        log.info("Created FinalProductBatch with ID: {} and batch number: {}", savedBatch.getId(), savedBatch.getBatchNumber());
        return savedBatch;
    }

    public Optional<FinalProductBatch> findById(@NonNull Long id) {
        return finalProductBatchRepository.findById(id);
    }

}
