package com.trazia.trazia_project.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterialBatchDTO {

    private String batchNumber;

    private LocalDate purchaseDate;

    private LocalDate receivingDate;

    private Long supplierId;

    private List<DocumentDTO> documents;
}
