package com.trazia.trazia_project.controller.rawmaterial;

import com.trazia.trazia_project.service.batch.RawMaterialBatchService;
import com.trazia.trazia_project.dto.batch.RawMaterialBatchDTO;
import com.trazia.trazia_project.entity.batch.RawMaterialBatch;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/raw-material-batches")
@RequiredArgsConstructor
public class RawMaterialBatchController {

    private final RawMaterialBatchService rawMaterialBatchService;

    @GetMapping("/{id}")
    public ResponseEntity<RawMaterialBatchDTO> getBatchById(@PathVariable Long id) {
        // Buscar la entidad
        return rawMaterialBatchService.findById(id)
                .map(batch -> {
                    RawMaterialBatchDTO dto = rawMaterialBatchService.convertToDTO(batch);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RawMaterialBatchDTO> createRawMaterialBatch(@Valid @RequestBody RawMaterialBatchDTO batchDto) {
        RawMaterialBatch savedBatch = rawMaterialBatchService.saveFromDTO(batchDto);
        if (savedBatch == null) {
            return ResponseEntity.badRequest().build();
        }
        RawMaterialBatchDTO dto = rawMaterialBatchService.convertToDTO(savedBatch);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<RawMaterialBatchDTO>> getAllBatches() {
        List<RawMaterialBatch> batches = rawMaterialBatchService.getAllBatches();
        List<RawMaterialBatchDTO> dtoList = batches.stream()
                .map(rawMaterialBatchService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }
}