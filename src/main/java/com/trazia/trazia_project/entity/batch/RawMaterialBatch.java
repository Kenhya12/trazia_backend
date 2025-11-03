package com.trazia.trazia_project.entity.batch;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

import com.trazia.trazia_project.entity.document.Document;
import com.trazia.trazia_project.entity.user.Supplier;

@Entity
@Table(name = "raw_material_batches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Entidad que representa un lote de materia prima en el sistema.
 * Incluye información relevante para la trazabilidad, como proveedor,
 * fechas clave, cantidad, unidad y documentos asociados.
 */
public class RawMaterialBatch {

    /**
     * Identificador único del lote de materia prima (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Número de lote asignado por el proveedor.
     */
    @Column(name = "batch_number", nullable = false)
    private String batchNumber;

    /**
     * Cantidad del lote (por ejemplo, en kg o litros).
     */
    @Column(name = "quantity", nullable = false)
    private Double quantity;

    /**
     * Fecha de caducidad de la materia prima.
     */
    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    /**
     * Fecha en la que se realizó la compra de la materia prima.
     */
    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    /**
     * Fecha de recepción del lote en el almacén.
     */
    @Column(name = "receiving_date", nullable = false)
    private LocalDate receivingDate;

    /**
     * Proveedor que suministra la materia prima.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    /**
     * Nombre de la materia prima.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Unidad de medida de la materia prima (por ejemplo, kg, litros).
     */
    @Column(name = "unit", nullable = false)
    private String unit;

    /**
     * Documentos asociados al lote, para trazabilidad y soporte documental.
     */
    @OneToMany(mappedBy = "rawMaterialBatch", cascade = CascadeType.ALL)
    private List<Document> documents;

    /**
     * Comentarios u observaciones sobre el lote.
     */
    @Column(name = "comments")
    private String comments;

    /**
     * Setter auxiliar para compatibilidad con tests que usan 'setProvider'.
     * Permite establecer el nombre del proveedor directamente.
     * @param providerName nombre del proveedor a establecer en el objeto supplier.
     */
    public void setProvider(String providerName) {
        if (this.supplier == null) {
            this.supplier = new Supplier();
        }
        this.supplier.setName(providerName);
    }
}
