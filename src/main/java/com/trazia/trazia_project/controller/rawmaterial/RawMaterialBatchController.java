package com.trazia.trazia_project.controller.rawmaterial;

import com.trazia.trazia_project.service.batch.RawMaterialBatchService;
import com.trazia.trazia_project.dto.batch.RawMaterialBatchDTO;
import com.trazia.trazia_project.entity.batch.RawMaterialBatch;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
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
     * Acepta multipart/form-data con archivos adjuntos
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RawMaterialBatchDTO> createRawMaterialBatch(
            @RequestPart("supplierId") String supplierId,
            @RequestPart("rawMaterialId") String rawMaterialId,
            @RequestPart("invoiceNumber") String invoiceNumber,
            @RequestPart("name") String name,
            @RequestPart("batchNumber") String batchNumber,
            @RequestPart("quantity") String quantity,
            @RequestPart("unit") String unit,
            @RequestPart("receivingDate") String receivingDate,
            @RequestPart("expirationDate") String expirationDate,
            @RequestPart(value = "comments", required = false) String comments,
            @RequestPart(value = "documents", required = false) List<MultipartFile> documents) {

        try {
            // Crear el DTO usando setters (evita problemas con builder de Lombok)
            RawMaterialBatchDTO batchDto = new RawMaterialBatchDTO();
            batchDto.setSupplierId(Long.parseLong(supplierId));
            batchDto.setRawMaterialId(Long.parseLong(rawMaterialId));
            batchDto.setInvoiceNumber(invoiceNumber);
            batchDto.setName(name);
            batchDto.setBatchNumber(batchNumber);
            batchDto.setQuantity(Double.parseDouble(quantity));
            batchDto.setUnit(unit);
            batchDto.setReceivingDate(LocalDate.parse(receivingDate));
            batchDto.setExpirationDate(LocalDate.parse(expirationDate));
            batchDto.setComments(comments);

            // TODO: Procesar documentos adjuntos (implementación futura)
            // if (documents != null && !documents.isEmpty()) {
            // for (MultipartFile file : documents) {
            // // Guardar archivo en el sistema de archivos o BD
            // System.out.println("Archivo recibido: " + file.getOriginalFilename());
            // }
            // }

            // Guardar el batch usando el servicio
            RawMaterialBatch savedBatch = rawMaterialBatchService.saveFromDTO(batchDto);

            if (savedBatch == null) {
                return ResponseEntity.badRequest().build();
            }

            // Convertir la entidad guardada a DTO y retornar
            RawMaterialBatchDTO responseDto = rawMaterialBatchService.convertToDTO(savedBatch);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

        } catch (NumberFormatException e) {
            System.err.println("Error de formato en números: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("Error al crear batch: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
