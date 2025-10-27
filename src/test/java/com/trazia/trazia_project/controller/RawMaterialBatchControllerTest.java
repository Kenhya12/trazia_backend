package com.trazia.trazia_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.trazia.trazia_project.controller.rawmaterial.RawMaterialBatchController;
import com.trazia.trazia_project.entity.batch.RawMaterialBatch;
import com.trazia.trazia_project.service.batch.RawMaterialBatchService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(RawMaterialBatchController.class)
public class RawMaterialBatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RawMaterialBatchService rawMaterialBatchService;

    @MockBean
    private com.trazia.trazia_project.security.JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setup() {
        System.out.println("---- Running setup ----");
        Mockito.when(jwtTokenProvider.validateToken(Mockito.anyString())).thenReturn(true);
        Mockito.when(jwtTokenProvider.getUsernameFromToken(Mockito.anyString())).thenReturn("testuser");
    }

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

    @Test
    public void testGetAllRawMaterialBatches() throws Exception {
        List<RawMaterialBatch> batches = List.of(
                new RawMaterialBatch(/* init with example data */),
                new RawMaterialBatch(/* init with example data */));

        Mockito.when(rawMaterialBatchService.getAllBatches()).thenReturn(batches);

        mockMvc.perform(get("/raw-material-batches")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(batches.size()));
    }

    @Test
    public void testCreateRawMaterialBatch() throws Exception {
        RawMaterialBatch batch = new RawMaterialBatch();
        batch.setId(100L);
        batch.setBatchNumber("Batch100");
        batch.setPurchaseDate(LocalDate.now().minusDays(10));
        batch.setReceivingDate(LocalDate.now().minusDays(5));

        Mockito.when(rawMaterialBatchService.save(Mockito.any(RawMaterialBatch.class))).thenReturn(batch);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String json = mapper.writeValueAsString(batch);

        mockMvc.perform(post("/raw-material-batches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.batchNumber").value("Batch100"));
    }

    @Test
    public void testCreateRawMaterialBatch_badRequest() throws Exception {
        RawMaterialBatch batch = new RawMaterialBatch();
        batch.setBatchNumber("InvalidBatch");

        // Simulamos que el servicio devuelve null → error en creación
        Mockito.when(rawMaterialBatchService.save(Mockito.any(RawMaterialBatch.class))).thenReturn(null);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String json = mapper.writeValueAsString(batch);

        mockMvc.perform(post("/raw-material-batches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllRawMaterialBatches_emptyList() throws Exception {
        Mockito.when(rawMaterialBatchService.getAllBatches()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/raw-material-batches")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
