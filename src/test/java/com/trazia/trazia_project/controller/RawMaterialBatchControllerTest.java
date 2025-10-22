package com.trazia.trazia_project.controller;

import com.trazia.trazia_project.entity.RawMaterialBatch;
import com.trazia.trazia_project.service.RawMaterialBatchService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RawMaterialBatchController.class)

@AutoConfigureMockMvc(addFilters = false)
public class RawMaterialBatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RawMaterialBatchService rawMaterialBatchService;

    @MockBean
    private com.trazia.trazia_project.security.JwtTokenProvider jwtTokenProvider;

    @Test
    public void testGetRawMaterialBatchById_found() throws Exception {
        RawMaterialBatch batch = new RawMaterialBatch();
        batch.setId(1L);
        batch.setBatchNumber("Batch123");
        batch.setPurchaseDate(LocalDate.now().minusDays(10));
        batch.setReceivingDate(LocalDate.now().minusDays(8));

        Mockito.when(rawMaterialBatchService.findById(1L)).thenReturn(Optional.of(batch));

        mockMvc.perform(get("/raw-material-batches/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batchNumber").value("Batch123"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void testGetRawMaterialBatchById_notFound() throws Exception {
        Mockito.when(rawMaterialBatchService.findById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/raw-material-batches/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
