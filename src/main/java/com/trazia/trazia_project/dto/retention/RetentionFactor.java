package com.trazia.trazia_project.dto.retention;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetentionFactor {
    private String nutrient;
    private double retentionFactor;
    private String processingType;
    private String retnCode;      // Código del factor de retención
    private String fdGrpCd;       // Código del grupo alimenticio
    private String retnDesc;      // Descripción del alimento/preparación
    private String nutrNo;        // Código del nutriente
    private String nutrDesc;      // Descripción del nutriente
    private double retenFactor;
}
