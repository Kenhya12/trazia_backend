// Document.java
package com.trazia.trazia_project.entity.document;

import com.trazia.trazia_project.entity.batch.RawMaterialBatch;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre o descripción del documento
    @Column(name = "document_name", nullable = false)
    private String documentName;

    // Relación con el lote de materia prima al que pertenece
    @ManyToOne
    @JoinColumn(name = "raw_material_batch_id")
    private RawMaterialBatch rawMaterialBatch;

    @Column(name = "quality_certificate_url")
    private String qualityCertificateUrl;

    @Column(name = "lab_analysis_url")
    private String labAnalysisUrl;

    // Otros campos como ruta de archivo, tipo, fecha, etc. pueden ser agregados
    // aquí
}
