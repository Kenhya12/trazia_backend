package com.trazia.trazia_project.service;

import com.trazia.trazia_project.entity.Document;
import com.trazia.trazia_project.entity.RawMaterialBatch;
import com.trazia.trazia_project.dto.RawMaterialBatchDTO;
import com.trazia.trazia_project.repository.RawMaterialBatchRepository;
import com.trazia.trazia_project.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RawMaterialBatchService {

    @Autowired
    private RawMaterialBatchRepository rawMaterialBatchRepository;

    // Método para obtener todos los lotes guardados
    public List<RawMaterialBatch> getAllBatches() {
        return rawMaterialBatchRepository.findAll();
    }

    @Autowired
    private SupplierRepository supplierRepository;

    public RawMaterialBatch saveRawMaterialBatch(RawMaterialBatch batch) {
        return rawMaterialBatchRepository.save(batch);
    }

    public RawMaterialBatch mapDtoToEntity(RawMaterialBatchDTO dto) {
        RawMaterialBatch entity = new RawMaterialBatch();

        entity.setBatchNumber(dto.getBatchNumber());
        entity.setPurchaseDate(dto.getPurchaseDate());
        entity.setReceivingDate(dto.getReceivingDate());

        // Buscar y asignar proveedor
        supplierRepository.findById(dto.getSupplierId()).ifPresent(entity::setSupplier);

        // Mapear lista de documentos del DTO a entidades
        if (dto.getDocuments() != null) {
            List<Document> documents = dto.getDocuments().stream().map(docDto -> {
                Document doc = new Document();
                doc.setDocumentName(docDto.getDocumentName());
                doc.setQualityCertificateUrl(docDto.getQualityCertificateUrl());
                doc.setLabAnalysisUrl(docDto.getLabAnalysisUrl());
                doc.setRawMaterialBatch(entity); // asignar relación inversa
                return doc;
            }).toList();

            entity.setDocuments(documents);
        }

        return entity;
    }
}
