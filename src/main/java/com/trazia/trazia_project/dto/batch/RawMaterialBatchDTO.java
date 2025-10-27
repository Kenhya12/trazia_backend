package com.trazia.trazia_project.dto.batch;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

import com.trazia.trazia_project.dto.document.DocumentDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterialBatchDTO {
    private Long id;

    private String batchNumber;

    private LocalDate purchaseDate;

    private LocalDate receivingDate;

    private Long supplierId;

    private List<DocumentDTO> documents;

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }
}

