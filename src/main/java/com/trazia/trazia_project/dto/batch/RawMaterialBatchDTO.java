package com.trazia.trazia_project.dto.batch;

import lombok.*;
import java.time.LocalDate;
import java.util.List;
import com.trazia.trazia_project.dto.document.DocumentDTO;
import com.trazia.trazia_project.entity.batch.RawMaterialBatch;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterialBatchDTO {
    
    private Long id;
    private Long supplierId;
    private Long rawMaterialId;
    private String supplierName;
    private String invoiceNumber;
    private String batchNumber;
    private String name;
    private String unit;
    private Double quantity;
    private LocalDate purchaseDate;
    private LocalDate receivingDate;
    private LocalDate expirationDate;
    private String comments;
    private List<DocumentDTO> documents;
    
    // Constructor desde entidad
    public RawMaterialBatchDTO(RawMaterialBatch batch) {
        this.id = batch.getId();
        this.batchNumber = batch.getBatchNumber();
        this.quantity = batch.getQuantity();
        this.expirationDate = batch.getExpirationDate();
        this.receivingDate = batch.getReceivingDate();
        this.supplierName = batch.getSupplier() != null ? batch.getSupplier().getName() : null;
        this.supplierId = batch.getSupplier() != null ? batch.getSupplier().getId() : null;
        this.comments = batch.getComments();
        this.name = batch.getName();
        this.unit = batch.getUnit();
    }
}
