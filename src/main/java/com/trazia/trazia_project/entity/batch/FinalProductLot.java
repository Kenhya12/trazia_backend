package com.trazia.trazia_project.entity.batch;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import com.trazia.trazia_project.entity.recipe.Recipe;

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

    private String batchNumber; // Número de lote

    private LocalDate expiryDate;

    /** 🔹 Relación inversa: muchos lotes pueden provenir de una receta */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    @ToString.Exclude
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "final_product_batch_id")
    private FinalProductBatch finalProductBatch;
}