package com.trazia.trazia_project.service;

import com.trazia.trazia_project.entity.FinalProductBatch;
import com.trazia.trazia_project.entity.RawMaterialBatch;
import com.trazia.trazia_project.repository.FinalProductBatchRepository;
import com.trazia.trazia_project.repository.RawMaterialBatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@Service
public class FinalProductBatchService {

    @Autowired
    private FinalProductBatchRepository finalProductBatchRepository;

    @Autowired
    private RawMaterialBatchRepository rawMaterialBatchRepository;

    public String generateBatchNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long countToday = finalProductBatchRepository.countByProductionDate(LocalDate.now()) + 1;
        return "FPB-" + datePart + "-" + String.format("%04d", countToday);
    }

    public FinalProductBatch createFinalProductBatch(FinalProductBatch batch, List<Long> rawMaterialBatchIds) {
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
