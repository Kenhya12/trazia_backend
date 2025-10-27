package com.trazia.trazia_project.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.trazia.trazia_project.entity.batch.FinalProductBatch;
import com.trazia.trazia_project.entity.batch.RawMaterialBatch;
import com.trazia.trazia_project.repository.product.FinalProductBatchRepository;
import com.trazia.trazia_project.repository.rawmaterial.RawMaterialBatchRepository;
import com.trazia.trazia_project.service.batch.FinalProductBatchService;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

public class FinalProductBatchServiceTest {

    @Mock
    private FinalProductBatchRepository finalProductBatchRepository;

    @Mock
    private RawMaterialBatchRepository rawMaterialBatchRepository;

    @InjectMocks
    private FinalProductBatchService finalProductBatchService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateFinalProductBatch() {
        FinalProductBatch batch = new FinalProductBatch();
        batch.setProductionDate(LocalDate.now());
        batch.setResponsiblePerson("Tester");

        List<Long> rawIds = List.of(1L, 2L);
        List<RawMaterialBatch> rawBatches = List.of(new RawMaterialBatch(), new RawMaterialBatch());

        when(rawMaterialBatchRepository.findAllById(rawIds)).thenReturn(rawBatches);
        when(finalProductBatchRepository.countByProductionDate(any(LocalDate.class))).thenReturn(0L);
        when(finalProductBatchRepository.save(any(FinalProductBatch.class))).thenAnswer(i -> i.getArgument(0));

        FinalProductBatch saved = finalProductBatchService.createFinalProductBatch(batch, rawIds);

        assertNotNull(saved.getBatchNumber());
        assertEquals(2, saved.getRawMaterialBatchesUsed().size());
        assertEquals("Tester", saved.getResponsiblePerson());
    }

    @Test
    public void testCreateFinalProductBatch_generatesSequentialBatchNumbers() {
        FinalProductBatch batch = new FinalProductBatch();
        batch.setProductionDate(LocalDate.now());
        batch.setResponsiblePerson("Tester");

        when(finalProductBatchRepository.countByProductionDate(any(LocalDate.class))).thenReturn(5L);
        when(finalProductBatchRepository.save(any(FinalProductBatch.class))).thenAnswer(i -> i.getArgument(0));

        FinalProductBatch saved = finalProductBatchService.createFinalProductBatch(batch, List.of());
        assertTrue(saved.getBatchNumber().contains("6"));
    }
}
