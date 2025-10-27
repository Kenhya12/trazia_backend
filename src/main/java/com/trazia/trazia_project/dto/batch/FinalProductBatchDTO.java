package com.trazia.trazia_project.dto.batch;

import java.time.LocalDate;
import lombok.Data;

@Data
public class FinalProductBatchDTO {
    private Long id;
    private String batchNumber;
    private LocalDate productionDate;
    private String recipeLink;
    private String responsiblePerson;
}
