package com.trazia.trazia_project.controller;

import com.trazia.trazia_project.entity.RawMaterialBatch;
import com.trazia.trazia_project.service.RawMaterialBatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/raw-material-batches")
public class RawMaterialBatchController {

    private final RawMaterialBatchService rawMaterialBatchService;

    public RawMaterialBatchController(RawMaterialBatchService rawMaterialBatchService) {
        this.rawMaterialBatchService = rawMaterialBatchService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RawMaterialBatch> getBatchById(@PathVariable Long id) {
        Optional<RawMaterialBatch> batch = rawMaterialBatchService.findById(id);
        if (batch.isPresent()) {
            return ResponseEntity.ok(batch.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint para crear un nuevo lote de materia prima
    @PostMapping
    public ResponseEntity<RawMaterialBatch> createRawMaterialBatch(@RequestBody RawMaterialBatch batch) {
        RawMaterialBatch savedBatch = rawMaterialBatchService.saveRawMaterialBatch(batch);
        return ResponseEntity.ok(savedBatch);
    }

    // Endpoint para obtener todos los lotes (opcional, para pruebas)
    @GetMapping
    public ResponseEntity<java.util.List<RawMaterialBatch>> getAllBatches() {
        return ResponseEntity.ok(rawMaterialBatchService.getAllBatches());
    }

}
