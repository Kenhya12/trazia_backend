package com.trazia.trazia_project.controller;

import com.trazia.trazia_project.entity.RawMaterialBatch;
import com.trazia.trazia_project.service.RawMaterialBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/raw-material-batches")
public class RawMaterialBatchController {

    @Autowired
    private RawMaterialBatchService rawMaterialBatchService;

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
