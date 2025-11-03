package com.trazia.trazia_project.controller.company;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trazia.trazia_project.dto.company.CompanyDTO;
import com.trazia.trazia_project.entity.company.Company;
import com.trazia.trazia_project.service.company.CompanyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyService companyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "adminUser", roles = {"USER", "ADMIN"})
    void testRegisterCompany_Success() throws Exception {
        // Given
        CompanyDTO requestDTO = createCompanyDTO("Test Company", "12345678A");
        Company responseCompany = createCompany(1L, "Test Company");

        when(companyService.registerCompany(any(CompanyDTO.class), eq("adminUser")))
                .thenReturn(responseCompany);

        // When & Then
        mockMvc.perform(post("/api/company/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.businessName").value("Test Company"));
    }

    @Test
    @WithMockUser(username = "adminUser", roles = {"USER", "ADMIN"})
    void testUpdateCompany_Success() throws Exception {
        // Given
        CompanyDTO requestDTO = createCompanyDTO("Updated Company", "87654321B");
        Company responseCompany = createCompany(1L, "Updated Company");

        when(companyService.updateCompany(eq(1L), any(CompanyDTO.class), eq("adminUser")))
                .thenReturn(responseCompany);

        // When & Then
        mockMvc.perform(put("/api/company/update/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.businessName").value("Updated Company"));
    }

    @Test
    @WithMockUser(username = "adminUser", roles = {"USER", "ADMIN"})
    void testUpdateCompany_NotFound() throws Exception {
        // Given
        CompanyDTO requestDTO = createCompanyDTO("Non-existent Company", "99999999X");

        when(companyService.updateCompany(eq(999L), any(CompanyDTO.class), eq("adminUser")))
                .thenThrow(new RuntimeException("Company not found"));

        // When & Then
        mockMvc.perform(put("/api/company/update/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testRegisterCompany_Unauthenticated() throws Exception {
        // Given
        CompanyDTO requestDTO = createCompanyDTO("Test Company", "12345678A");

        // When & Then - Sin autenticación debería fallar
        mockMvc.perform(post("/api/company/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden()); // O isUnauthorized
    }

    // Métodos helper para crear objetos de prueba
    private CompanyDTO createCompanyDTO(String businessName, String taxId) {
        CompanyDTO dto = new CompanyDTO();
        dto.setBusinessName(businessName);
        dto.setTaxId(taxId);
        dto.setAddress("Test Address");
        dto.setHealthRegistration("HealthReg001");
        return dto;
    }

    private Company createCompany(Long id, String businessName) {
        Company company = new Company();
        company.setId(id);
        company.setBusinessName(businessName);
        company.setTaxId("12345678A");
        company.setAddress("Test Address");
        company.setHealthRegistration("HealthReg001");
        return company;
    }
}