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
        batch.setExpirationDate(LocalDate.now().plusMonths(6)); // nuevo
        batch.setQuantity(10.0); // nuevo
        batch.setProvider("Proveedor ABC"); // nuevo

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

    @Test
    public void testSaveRawMaterialBatch_NullBatch() {
        assertThrows(IllegalArgumentException.class, () -> {
            rawMaterialBatchService.saveRawMaterialBatch(null);
        });
    }

    @Test
    public void testFindById_ExceptionThrown() {
        when(rawMaterialBatchRepository.findById(any(Long.class)))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            rawMaterialBatchService.findById(99L);
        });
    }

    @Test
    public void testSaveRawMaterialBatch_FutureDate() {
        RawMaterialBatch batch = new RawMaterialBatch();
        batch.setBatchNumber("FutureBatch");
        batch.setPurchaseDate(LocalDate.now().plusDays(5));
        batch.setReceivingDate(LocalDate.now().plusDays(6));
        batch.setExpirationDate(LocalDate.now().plusMonths(6));
        batch.setQuantity(10.0);
        batch.setProvider("Proveedor XYZ"); // <-- asignar proveedor válido

        when(rawMaterialBatchRepository.save(any(RawMaterialBatch.class))).thenReturn(batch);

        RawMaterialBatch result = rawMaterialBatchService.saveRawMaterialBatch(batch);
        assertNotNull(result);
        assertEquals("FutureBatch", result.getBatchNumber());
    }

    @Test
    void testSaveRawMaterialBatch_QuantityZero() {
        RawMaterialBatch batch = new RawMaterialBatch();
        batch.setBatchNumber("BatchZero");
        batch.setPurchaseDate(LocalDate.now());
        batch.setReceivingDate(LocalDate.now());
        batch.setExpirationDate(LocalDate.now().plusMonths(6));
        batch.setQuantity(0.0);
        batch.setProvider("Proveedor Test");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            rawMaterialBatchService.saveRawMaterialBatch(batch);
        });
        assertEquals("La cantidad debe ser mayor que cero", exception.getMessage());
    }

    @Test
    void testSaveRawMaterialBatch_ExpirationDateNull() {
        RawMaterialBatch batch = new RawMaterialBatch();
        batch.setBatchNumber("BatchNoExp");
        batch.setPurchaseDate(LocalDate.now());
        batch.setReceivingDate(LocalDate.now());
        batch.setQuantity(5.0);
        batch.setProvider("Proveedor Test");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            rawMaterialBatchService.saveRawMaterialBatch(batch);
        });
        assertEquals("La fecha de vencimiento no puede ser nula", exception.getMessage());
    }

    @Test
    void testSaveRawMaterialBatch_MultipleValidBatches() {
        RawMaterialBatch batch1 = new RawMaterialBatch();
        batch1.setBatchNumber("Batch1");
        batch1.setPurchaseDate(LocalDate.now().minusDays(2));
        batch1.setReceivingDate(LocalDate.now().minusDays(1));
        batch1.setExpirationDate(LocalDate.now().plusMonths(3));
        batch1.setQuantity(10.0);
        batch1.setProvider("Proveedor A");

        RawMaterialBatch batch2 = new RawMaterialBatch();
        batch2.setBatchNumber("Batch2");
        batch2.setPurchaseDate(LocalDate.now().minusDays(3));
        batch2.setReceivingDate(LocalDate.now().minusDays(2));
        batch2.setExpirationDate(LocalDate.now().plusMonths(4));
        batch2.setQuantity(20.0);
        batch2.setProvider("Proveedor B");

        when(rawMaterialBatchRepository.save(any(RawMaterialBatch.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RawMaterialBatch saved1 = rawMaterialBatchService.saveRawMaterialBatch(batch1);
        RawMaterialBatch saved2 = rawMaterialBatchService.saveRawMaterialBatch(batch2);

        assertNotNull(saved1);
        assertNotNull(saved2);
        assertEquals("Batch1", saved1.getBatchNumber());
        assertEquals("Batch2", saved2.getBatchNumber());
    }

    @Test
void testSaveRawMaterialBatch_ReceivingBeforePurchase() {
    RawMaterialBatch batch = new RawMaterialBatch();
    batch.setBatchNumber("BatchInvalidDates");
    batch.setPurchaseDate(LocalDate.now());
    batch.setReceivingDate(LocalDate.now().minusDays(1));
    batch.setExpirationDate(LocalDate.now().plusMonths(3));
    batch.setQuantity(5.0);
    batch.setProvider("Proveedor Test");

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        rawMaterialBatchService.saveRawMaterialBatch(batch);
    });
    assertEquals("La fecha de recepción no puede ser anterior a la compra", exception.getMessage());
}

}
