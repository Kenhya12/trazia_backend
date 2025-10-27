package com.trazia.trazia_project.entity.batch;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "final_product_batches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinalProductBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_number", nullable = false, unique = true)
    private String batchNumber;

    @Column(name = "production_date", nullable = false)
    private LocalDate productionDate;

    @Column(name = "recipe_link")
    private String recipeLink;

    @Column(name = "responsible_person")
    private String responsiblePerson;

    @ManyToMany
    @JoinTable(
        name = "final_product_raw_material_batches",
        joinColumns = @JoinColumn(name = "final_product_batch_id"),
        inverseJoinColumns = @JoinColumn(name = "raw_material_batch_id")
    )
    
    private List<RawMaterialBatch> rawMaterialBatchesUsed;
}
