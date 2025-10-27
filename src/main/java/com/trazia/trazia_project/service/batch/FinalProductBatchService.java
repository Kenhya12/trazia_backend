package com.trazia.trazia_project.service.batch;

import com.trazia.trazia_project.entity.FinalProductBatch;
import com.trazia.trazia_project.entity.RawMaterialBatch;
import com.trazia.trazia_project.repository.product.FinalProductBatchRepository;
import com.trazia.trazia_project.repository.rawmaterial.RawMaterialBatchRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
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
        batch.setRawMaterialBatchesUsed(rawMaterials);
        String batchNumber = generateBatchNumber();
        batch.setBatchNumber(batchNumber);
        return finalProductBatchRepository.save(batch);
    }

    public Optional<FinalProductBatch> findById(Long id) {
        return finalProductBatchRepository.findById(id);
    }

}
