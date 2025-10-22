/* package com.trazia.trazia_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trazia.trazia_project.dto.company.CompanyDTO;
import com.trazia.trazia_project.entity.Company;
import com.trazia.trazia_project.entity.User;
import com.trazia.trazia_project.security.JwtTokenProvider;
import com.trazia.trazia_project.service.company.CompanyService;
import com.trazia.trazia_project.controller.CompanyController;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

@WebMvcTest(controllers = CompanyController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyService companyService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
@WithMockUser(username = "adminUser", roles = {"ADMIN"})
public void testRegisterCompany() throws Exception {
    CompanyDTO dto = new CompanyDTO();
    dto.setBusinessName("Test Company");
    dto.setTaxId("12345678A");
    dto.setAddress("Test Address");
    dto.setHealthRegistration("HealthReg001");
    dto.setLogo(null);

    User mockUser = new User();
    mockUser.setUsername("adminUser");
    // Si User tiene id u otros atributos, configúralos también

    Company company = new Company();
    company.setId(1L);
    company.setBusinessName(dto.getBusinessName());
    company.setTaxId(dto.getTaxId());
    company.setAddress(dto.getAddress());
    company.setHealthRegistration(dto.getHealthRegistration());
    company.setUser(mockUser);

    when(userRepository.findByUsername("adminUser")).thenReturn(Optional.of(mockUser));
    when(companyService.registerCompany(Mockito.any(CompanyDTO.class), Mockito.anyString())).thenReturn(company);

    mockMvc.perform(post("/api/company/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.businessName").value("Test Company"));
}

@Test
@WithMockUser(username = "adminUser", roles = {"ADMIN"})
public void testUpdateCompany() throws Exception {
    Long companyId = 1L;
    CompanyDTO dto = new CompanyDTO();
    dto.setBusinessName("Updated Company");
    dto.setTaxId("87654321B");
    dto.setAddress("Updated Address");
    dto.setHealthRegistration("HealthReg002");
    dto.setLogo(null);

    User mockUser = new User();
    mockUser.setUsername("adminUser");

    Company mockCompany = new Company();
    mockCompany.setId(companyId);
    mockCompany.setUser(mockUser);

    when(companyRepository.findById(companyId)).thenReturn(Optional.of(mockCompany));
    when(companyService.updateCompany(Mockito.eq(companyId), Mockito.any(CompanyDTO.class), Mockito.anyString()))
            .thenReturn(mockCompany);

    mockMvc.perform(put("/api/company/update/{id}", companyId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.businessName").value("Updated Company"));
}
} */