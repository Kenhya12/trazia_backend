package com.trazia.trazia_project.controller.rawmaterial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trazia.trazia_project.config.TestSecurityConfig;
import com.trazia.trazia_project.dto.batch.RawMaterialBatchDTO;
import com.trazia.trazia_project.entity.batch.RawMaterialBatch;
import com.trazia.trazia_project.service.batch.RawMaterialBatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class) // ← CLAVE: Importar la configuración de seguridad para tests
@SuppressWarnings({"null", "unchecked"})
public class RawMaterialBatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RawMaterialBatchService rawMaterialBatchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetBatchById_Found() throws Exception {
        // Given
        RawMaterialBatch batch = createBatch(1L, "BATCH-001", "Raw Material A");
        RawMaterialBatchDTO dto = createBatchDTO(1L, "BATCH-001", "Raw Material A");

        when(rawMaterialBatchService.findById(1L)).thenReturn(Optional.of(batch));
        when(rawMaterialBatchService.convertToDTO(batch)).thenReturn(dto);

        // When & Then
        mockMvc.perform(get("/raw-material-batches/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.batchNumber").value("BATCH-001"))
                .andExpect(jsonPath("$.name").value("Raw Material A"));

        verify(rawMaterialBatchService).findById(1L);
        verify(rawMaterialBatchService).convertToDTO(batch);
    }

    @Test
    void testGetBatchById_NotFound() throws Exception {
        // Given
        when(rawMaterialBatchService.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/raw-material-batches/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(rawMaterialBatchService).findById(1L);
        verify(rawMaterialBatchService, never()).convertToDTO(any());
    }

    @Test
    void testGetAllBatches_WithBatches() throws Exception {
        // Given
        RawMaterialBatch batch1 = createBatch(1L, "BATCH-001", "Material A");
        RawMaterialBatch batch2 = createBatch(2L, "BATCH-002", "Material B");
        
        RawMaterialBatchDTO dto1 = createBatchDTO(1L, "BATCH-001", "Material A");
        RawMaterialBatchDTO dto2 = createBatchDTO(2L, "BATCH-002", "Material B");

        when(rawMaterialBatchService.getAllBatches()).thenReturn(Arrays.asList(batch1, batch2));
        when(rawMaterialBatchService.convertToDTO(batch1)).thenReturn(dto1);
        when(rawMaterialBatchService.convertToDTO(batch2)).thenReturn(dto2);

        // When & Then
        mockMvc.perform(get("/raw-material-batches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].batchNumber").value("BATCH-001"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].batchNumber").value("BATCH-002"));

        verify(rawMaterialBatchService).getAllBatches();
    }

    @Test
    void testGetAllBatches_EmptyList() throws Exception {
        // Given
        when(rawMaterialBatchService.getAllBatches()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/raw-material-batches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(jsonPath("$", empty()));

        verify(rawMaterialBatchService).getAllBatches();
    }

    @Test
    void testCreateRawMaterialBatch_Valid() throws Exception {
        // Given
        RawMaterialBatchDTO requestDTO = createBatchDTO(null, "NEW-BATCH", "New Material");
        RawMaterialBatch savedBatch = createBatch(1L, "NEW-BATCH", "New Material");
        RawMaterialBatchDTO responseDTO = createBatchDTO(1L, "NEW-BATCH", "New Material");

        when(rawMaterialBatchService.saveFromDTO(any(RawMaterialBatchDTO.class))).thenReturn(savedBatch);
        when(rawMaterialBatchService.convertToDTO(savedBatch)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/raw-material-batches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.batchNumber").value("NEW-BATCH"))
                .andExpect(jsonPath("$.name").value("New Material"));

        verify(rawMaterialBatchService).saveFromDTO(any(RawMaterialBatchDTO.class));
        verify(rawMaterialBatchService).convertToDTO(savedBatch);
    }

    @Test
    void testCreateRawMaterialBatch_Invalid_ReturnsBadRequest() throws Exception {
        // Given
        when(rawMaterialBatchService.saveFromDTO(any(RawMaterialBatchDTO.class))).thenReturn(null);

        RawMaterialBatchDTO invalidDTO = createBatchDTO(null, null, null);

        // When & Then
        mockMvc.perform(post("/raw-material-batches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(rawMaterialBatchService).saveFromDTO(any(RawMaterialBatchDTO.class));
        verify(rawMaterialBatchService, never()).convertToDTO(any());
    }

    // ==================== HELPER METHODS ====================

    private RawMaterialBatch createBatch(Long id, String batchNumber, String name) {
        RawMaterialBatch batch = new RawMaterialBatch();
        batch.setId(id);
        batch.setBatchNumber(batchNumber);
        batch.setName(name);
        batch.setQuantity(100.0);
        batch.setUnit("kg");
        return batch;
    }

    private RawMaterialBatchDTO createBatchDTO(Long id, String batchNumber, String name) {
        RawMaterialBatchDTO dto = new RawMaterialBatchDTO();
        dto.setId(id);
        dto.setBatchNumber(batchNumber);
        dto.setName(name);
        dto.setQuantity(100.0);
        dto.setUnit("kg");
        return dto;
    }
}