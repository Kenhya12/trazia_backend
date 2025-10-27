package com.trazia.trazia_project.dto.batch;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

import com.trazia.trazia_project.dto.document.DocumentDTO;
import com.trazia.trazia_project.entity.RawMaterialBatch;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterialBatchDTO {
    private Long id;

    private String supplierName;

    private String batchNumber;

    private String name;
    
    private String unit;

    private LocalDate purchaseDate;

    private LocalDate receivingDate;

    private Double quantity;

    private LocalDate expirationDate;

    private Long supplierId;

    private List<DocumentDTO> documents;

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public RawMaterialBatchDTO(RawMaterialBatch batch) {
        this.id = batch.getId();
        this.batchNumber = batch.getBatchNumber();
        this.quantity = batch.getQuantity();
        this.expirationDate = batch.getExpirationDate();
        this.supplierName = batch.getSupplier() != null ? batch.getSupplier().getName() : null;
    }
}

