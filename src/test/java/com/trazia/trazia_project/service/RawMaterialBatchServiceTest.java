package com.trazia.trazia_project.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import com.trazia.trazia_project.entity.RawMaterialBatch;
import com.trazia.trazia_project.repository.RawMaterialBatchRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RawMaterialBatchServiceTest {

    @Mock
    private RawMaterialBatchRepository rawMaterialBatchRepository;

    @InjectMocks
    private RawMaterialBatchService rawMaterialBatchService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveRawMaterialBatch() {
        RawMaterialBatch batch = new RawMaterialBatch();
        batch.setBatchNumber("BatchTest");
        batch.setPurchaseDate(LocalDate.now().minusDays(1));
        batch.setReceivingDate(LocalDate.now());

        when(rawMaterialBatchRepository.save(any(RawMaterialBatch.class))).thenReturn(batch);

        RawMaterialBatch savedBatch = rawMaterialBatchService.saveRawMaterialBatch(batch);

        assertNotNull(savedBatch);
        assertEquals("BatchTest", savedBatch.getBatchNumber());
    }

    @Test
    public void testFindByIdFound() {
        RawMaterialBatch batch = new RawMaterialBatch();
        batch.setId(1L);
        when(rawMaterialBatchRepository.findById(1L)).thenReturn(Optional.of(batch));

        Optional<RawMaterialBatch> found = rawMaterialBatchService.findById(1L);
        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
    }

    @Test
    public void testFindByIdNotFound() {
        when(rawMaterialBatchRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<RawMaterialBatch> found = rawMaterialBatchService.findById(2L);
        assertFalse(found.isPresent());
    }

    @Test
    public void testFindAll() {
        RawMaterialBatch batch = new RawMaterialBatch();
        batch.setBatchNumber("Batch1");
        when(rawMaterialBatchRepository.findAll()).thenReturn(List.of(batch));

        List<RawMaterialBatch> list = rawMaterialBatchService.findAll();
        assertFalse(list.isEmpty());
        assertEquals("Batch1", list.get(0).getBatchNumber());
    }

    @Test
    public void testFindAllEmpty() {
        when(rawMaterialBatchRepository.findAll()).thenReturn(Collections.emptyList());
        List<RawMaterialBatch> list = rawMaterialBatchService.findAll();
        assertTrue(list.isEmpty());
    }
}
