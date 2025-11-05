package com.trazia.trazia_project.controller.rawmaterial;

import com.trazia.trazia_project.entity.material.RawMaterial;
import com.trazia.trazia_project.service.material.RawMaterialService;
import com.trazia.trazia_project.dto.material.RawMaterialDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/raw-materials")
@RequiredArgsConstructor
@Slf4j
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;

    /**
     * GET - Obtener todas las materias primas
     */
    @GetMapping
    public ResponseEntity<List<RawMaterialDTO>> getAllRawMaterials() {
        try {
            List<RawMaterial> rawMaterials = rawMaterialService.findAll();
            List<RawMaterialDTO> dtos = rawMaterials.stream()
                    .map(rawMaterialService::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            log.error("Error getting raw materials", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET - Obtener materia prima por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<RawMaterialDTO> getRawMaterialById(@PathVariable Long id) {
        try {
            return rawMaterialService.findById(id)
                    .map(rawMaterialService::convertToDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting raw material with id: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * POST - Crear nueva materia prima
     */
    @PostMapping
    public ResponseEntity<RawMaterialDTO> createRawMaterial(@RequestBody RawMaterialDTO rawMaterialDTO) {
        try {
            RawMaterial rawMaterial = rawMaterialService.convertToEntity(rawMaterialDTO);
            RawMaterial savedRawMaterial = rawMaterialService.save(rawMaterial);
            RawMaterialDTO responseDTO = rawMaterialService.convertToDTO(savedRawMaterial);
            return ResponseEntity.status(201).body(responseDTO);
        } catch (Exception e) {
            log.error("Error creating raw material", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT - Actualizar materia prima existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<RawMaterialDTO> updateRawMaterial(@PathVariable Long id, @RequestBody RawMaterialDTO rawMaterialDTO) {
        try {
            if (!rawMaterialService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            RawMaterial rawMaterial = rawMaterialService.convertToEntity(rawMaterialDTO);
            rawMaterial.setId(id);
            RawMaterial updatedRawMaterial = rawMaterialService.save(rawMaterial);
            RawMaterialDTO responseDTO = rawMaterialService.convertToDTO(updatedRawMaterial);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error updating raw material with id: {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE - Eliminar materia prima
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRawMaterial(@PathVariable Long id) {
        try {
            if (!rawMaterialService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            rawMaterialService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting raw material with id: {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }
}