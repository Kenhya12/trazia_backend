package com.trazia.trazia_project.controller.rawmaterial;

import com.trazia.trazia_project.service.batch.RawMaterialBatchService;
import com.trazia.trazia_project.dto.batch.RawMaterialBatchDTO;
import com.trazia.trazia_project.entity.batch.RawMaterialBatch;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/raw-material-batches")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:3001", "http://localhost:5173" })
public class RawMaterialBatchController {

    private final RawMaterialBatchService rawMaterialBatchService;

    /**
     * GET - Obtener batch por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<RawMaterialBatchDTO> getBatchById(@PathVariable Long id) {
        return rawMaterialBatchService.findById(id)
                .map(batch -> {
                    RawMaterialBatchDTO dto = rawMaterialBatchService.convertToDTO(batch);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET - Obtener todos los batches
     */
    @GetMapping
    public ResponseEntity<List<RawMaterialBatchDTO>> getAllBatches() {
        List<RawMaterialBatch> batches = rawMaterialBatchService.findAll();
        List<RawMaterialBatchDTO> dtos = batches.stream()
                .map(rawMaterialBatchService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * POST - Crear un nuevo batch desde el frontend
     * Acepta JSON
     */
    @PostMapping
    public ResponseEntity<RawMaterialBatchDTO> createRawMaterialBatch(@RequestBody RawMaterialBatchDTO batchDto) {
        try {
            RawMaterialBatch savedBatch = rawMaterialBatchService.saveFromDTO(batchDto);
            if (savedBatch == null) {
                return ResponseEntity.badRequest().build();
            }
            RawMaterialBatchDTO responseDto = rawMaterialBatchService.convertToDTO(savedBatch);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (Exception e) {
            System.err.println("Error al crear batch: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
