package com.trazia.trazia_project.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import com.trazia.trazia_project.dto.RawMaterialBatchDTO;
import com.trazia.trazia_project.dto.DocumentDTO;
import com.trazia.trazia_project.entity.RawMaterialBatch;
import com.trazia.trazia_project.repository.RawMaterialBatchRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.List;

public class RawMaterialBatchServiceTest {
    
    @InjectMocks
    private RawMaterialBatchService rawMaterialBatchService;
    
    @Mock
    private RawMaterialBatchRepository rawMaterialBatchRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveRawMaterialBatch_withDtoMapping() {
        // Crear DTO de documento
        DocumentDTO docDto = DocumentDTO.builder()
            .documentName("Certificado Calidad")
            .qualityCertificateUrl("http://example.com/cert.pdf")
            .labAnalysisUrl("http://example.com/lab.pdf")
            .build();

        // Crear DTO de lote
        RawMaterialBatchDTO batchDto = RawMaterialBatchDTO.builder()
            .batchNumber("Batch123")
            .purchaseDate(LocalDate.now().minusDays(1))
            .receivingDate(LocalDate.now())
            .supplierId(1L)
            .documents(List.of(docDto))
            .build();

        RawMaterialBatch mockSavedBatch = new RawMaterialBatch();
        mockSavedBatch.setId(1L);
        mockSavedBatch.setBatchNumber(batchDto.getBatchNumber());
        mockSavedBatch.setPurchaseDate(batchDto.getPurchaseDate());
        mockSavedBatch.setReceivingDate(batchDto.getReceivingDate());

        when(rawMaterialBatchRepository.save(any(RawMaterialBatch.class))).thenReturn(mockSavedBatch);

        RawMaterialBatch entityToSave = rawMaterialBatchService.mapDtoToEntity(batchDto);
        RawMaterialBatch savedEntity = rawMaterialBatchService.saveRawMaterialBatch(entityToSave);

        assertNotNull(savedEntity);
        assertEquals(1L, savedEntity.getId());
        assertEquals(batchDto.getBatchNumber(), savedEntity.getBatchNumber());
    }
}
