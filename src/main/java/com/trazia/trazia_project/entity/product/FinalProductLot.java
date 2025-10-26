package com.trazia.trazia_project.entity.product;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "final_product_lots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinalProductLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String batchNumber;      // Número de lote
    private LocalDate expiryDate;
}
