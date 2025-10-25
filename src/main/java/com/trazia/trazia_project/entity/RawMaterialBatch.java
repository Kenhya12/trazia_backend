package com.trazia.trazia_project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "raw_material_batches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterialBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Número de lote asignado por el proveedor
    @Column(name = "batch_number", nullable = false)
    private String batchNumber;

    // Cantidad del lote (por ejemplo, en kg o litros)
    @Column(name = "quantity", nullable = false)
    private Double quantity;

    // Fecha de caducidad de la materia prima
    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    // Fecha en la que se realizó la compra
    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    // Fecha de recepción en el almacén
    @Column(name = "receiving_date", nullable = false)
    private LocalDate receivingDate;

    // Proveedor que suministra la materia prima
    @ManyToOne(optional = false)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    // Documentos asociados al lote, para trazabilidad y soporte documental
    @OneToMany(mappedBy = "rawMaterialBatch", cascade = CascadeType.ALL)
    private List<Document> documents;

    // Setter auxiliar para compatibilidad con tests que usan 'setProvider'
    public void setProvider(String providerName) {
        if (this.supplier == null) {
            this.supplier = new Supplier();
        }
        this.supplier.setName(providerName);
    }
}

