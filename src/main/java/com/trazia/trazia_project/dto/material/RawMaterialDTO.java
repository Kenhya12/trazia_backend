package com.trazia.trazia_project.dto.material;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterialDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal costPerUnit;
    private String unit;
    private String category;
    private Long supplierId;
    private String supplierName;
    private String internalCode;
    private BigDecimal minStock;
    private BigDecimal currentStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}