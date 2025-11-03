package com.trazia.trazia_project.service;

import com.trazia.trazia_project.dto.batch.RawMaterialBatchDTO;
import com.trazia.trazia_project.entity.batch.RawMaterialBatch;
import com.trazia.trazia_project.repository.rawmaterial.RawMaterialBatchRepository;
import com.trazia.trazia_project.service.impl.RawMaterialBatchServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RawMaterialBatchServiceTest {

    @Mock
    private RawMaterialBatchRepository repository;

    @InjectMocks
    private RawMaterialBatchServiceImpl service;

    private RawMaterialBatch batch;
    private RawMaterialBatchDTO batchDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        batch = new RawMaterialBatch();
        batch.setId(1L);
        batch.setName("Batch 1");

        batchDTO = new RawMaterialBatchDTO();
        batchDTO.setName("Batch 1");
    }

    @Test
    void testGetBatchByIdExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(batch));
        Optional<RawMaterialBatch> result = service.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(batch.getName(), result.get().getName());
    }

    @Test
    void testGetBatchByIdNotExists() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        Optional<RawMaterialBatch> result = service.findById(2L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testConvertToDTO() {
        RawMaterialBatchDTO dto = service.convertToDTO(batch);
        assertEquals(batch.getName(), dto.getName());
    }

    @Test
    void testConvertToDTONull() {
        RawMaterialBatchDTO dto = service.convertToDTO(null);
        assertNull(dto);
    }

    @Test
    void testSaveFromDTOValid() {
        when(repository.save(any(RawMaterialBatch.class))).thenReturn(batch);
        RawMaterialBatch saved = service.saveFromDTO(batchDTO);
        assertNotNull(saved);
        assertEquals(batchDTO.getName(), saved.getName());
    }

    @Test
    void testSaveFromDTOInvalid() {
        batchDTO.setName(null);
        RawMaterialBatch saved = service.saveFromDTO(batchDTO);
        assertNull(saved);
    }

    @Test
    void testSaveFromDTONull() {
        RawMaterialBatch saved = service.saveFromDTO(null);
        assertNull(saved);
    }

    @Test
    void testSaveFromDTOWithEmptyName() {
        batchDTO.setName("");
        RawMaterialBatch saved = service.saveFromDTO(batchDTO);
        assertNull(saved);
    }

    @Test
    void testGetAllBatchesNonEmpty() {
        when(repository.findAll()).thenReturn(Arrays.asList(batch));
        List<RawMaterialBatch> result = service.getAllBatches();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllBatchesEmpty() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        List<RawMaterialBatch> result = service.getAllBatches();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllBatchesMultiple() {
        RawMaterialBatch batch2 = new RawMaterialBatch();
        batch2.setId(2L);
        batch2.setName("Batch 2");

        when(repository.findAll()).thenReturn(Arrays.asList(batch, batch2));
        List<RawMaterialBatch> result = service.getAllBatches();
        assertEquals(2, result.size());
    }

    @Test
    void testGetAllBatchesWithNullElement() {
        when(repository.findAll()).thenReturn(Arrays.asList(batch, null));
        List<RawMaterialBatch> result = service.getAllBatches();
        assertEquals(2, result.size());
        assertNull(result.get(1));
    }

    @Test
    void testServiceThrowsExceptionOnSave() {
        when(repository.save(any())).thenThrow(new RuntimeException("DB error"));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.saveFromDTO(batchDTO));
        assertEquals("DB error", ex.getMessage());
    }

    @Test
    void testGetAllBatchesThrowsException() {
        when(repository.findAll()).thenThrow(new RuntimeException("DB error"));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getAllBatches());
        assertEquals("DB error", ex.getMessage());
    }

    @Test
    void testFindByIdThrowsException() {
        when(repository.findById(anyLong())).thenThrow(new RuntimeException("DB error"));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.findById(1L));
        assertEquals("DB error", ex.getMessage());
    }

    @Test
void testServiceRobustCoverage() {
    // 1️⃣ findById con ID válido
    when(repository.findById(1L)).thenReturn(Optional.of(batch));
    Optional<RawMaterialBatch> result = service.findById(1L);
    assertTrue(result.isPresent());
    assertEquals("Batch 1", result.get().getName());

    // 2️⃣ findById con ID inexistente
    when(repository.findById(2L)).thenReturn(Optional.empty());
    assertTrue(service.findById(2L).isEmpty());

    // 3️⃣ findById lanza excepción
    when(repository.findById(3L)).thenThrow(new RuntimeException("DB error"));
    RuntimeException ex = assertThrows(RuntimeException.class, () -> service.findById(3L));
    assertEquals("DB error", ex.getMessage());

    // 4️⃣ saveFromDTO válido
    when(repository.save(any(RawMaterialBatch.class))).thenReturn(batch);
    RawMaterialBatch saved = service.saveFromDTO(batchDTO);
    assertNotNull(saved);
    assertEquals("Batch 1", saved.getName());

    // 5️⃣ saveFromDTO nulo y con nombre vacío
    assertNull(service.saveFromDTO(null));
    batchDTO.setName("");
    RawMaterialBatch savedEmptyName = service.saveFromDTO(batchDTO);
    assertNotNull(savedEmptyName); // el servicio permite nombres vacíos

    // 6️⃣ getAllBatches con elementos normales y nulos
    RawMaterialBatch batch2 = new RawMaterialBatch();
    batch2.setId(2L);
    batch2.setName("Batch 2");

    when(repository.findAll()).thenReturn(Arrays.asList(batch, batch2, null));
    List<RawMaterialBatch> allBatches = service.getAllBatches();
    assertEquals(3, allBatches.size());
    assertEquals("Batch 1", allBatches.get(0).getName());
    assertEquals("Batch 2", allBatches.get(1).getName());
    assertNull(allBatches.get(2));

    // 7️⃣ getAllBatches lanza excepción
    when(repository.findAll()).thenThrow(new RuntimeException("DB error"));
    RuntimeException ex2 = assertThrows(RuntimeException.class, () -> service.getAllBatches());
    assertEquals("DB error", ex2.getMessage());

    // 8️⃣ convertToDTO nulo y válido
    RawMaterialBatchDTO dto = service.convertToDTO(batch);
    assertEquals("Batch 1", dto.getName());
    assertNull(service.convertToDTO(null));
}
}