package com.trazia.trazia_project.service;

import com.trazia.trazia_project.dto.company.CompanyDTO;
import com.trazia.trazia_project.entity.Company;
import com.trazia.trazia_project.service.company.CompanyService;
import com.trazia.trazia_project.entity.User;
import com.trazia.trazia_project.repository.company.CompanyRepository;
import com.trazia.trazia_project.repository.user.UserRepository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class CompanyServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    private User mockUser;
    private Company mockCompany;
    private CompanyDTO mockCompanyDTO;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setUsername("adminUser");

        mockCompanyDTO = new CompanyDTO();
        mockCompanyDTO.setBusinessName("My Company");
        mockCompanyDTO.setTaxId("12345678A");
        mockCompanyDTO.setAddress("123 Main St.");
        mockCompanyDTO.setHealthRegistration("HealthReg123");
        mockCompanyDTO.setLogo(null);

        mockCompany = new Company();
        mockCompany.setId(1L);
        mockCompany.setBusinessName(mockCompanyDTO.getBusinessName());
        mockCompany.setTaxId(mockCompanyDTO.getTaxId());
        mockCompany.setAddress(mockCompanyDTO.getAddress());
        mockCompany.setHealthRegistration(mockCompanyDTO.getHealthRegistration());
        mockCompany.setUser(mockUser);
    }

    @Test
    public void testRegisterCompany_Success() {
        when(userRepository.findByUsername("adminUser")).thenReturn(Optional.of(mockUser));
        when(companyRepository.save(any(Company.class))).thenReturn(mockCompany);

        Company result = companyService.registerCompany(mockCompanyDTO, "adminUser");

        assertEquals(mockCompanyDTO.getBusinessName(), result.getBusinessName());
        assertEquals(mockCompanyDTO.getTaxId(), result.getTaxId());
        verify(companyRepository).save(any(Company.class));
    }

    @Test
    public void testRegisterCompany_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            companyService.registerCompany(mockCompanyDTO, "unknown");
        });
    }

    @Test
    public void testUpdateCompany_Success() {
        when(companyRepository.findById(1L)).thenReturn(Optional.of(mockCompany));
        when(companyRepository.save(any(Company.class))).thenReturn(mockCompany);

        CompanyDTO updateDTO = new CompanyDTO();
        updateDTO.setBusinessName("Updated Name");
        updateDTO.setTaxId("87654321B");
        updateDTO.setAddress("Updated Address");
        updateDTO.setHealthRegistration("Updated HealthReg");
        updateDTO.setLogo(null);

        Company updated = companyService.updateCompany(1L, updateDTO, "adminUser");

        assertEquals("Updated Name", updated.getBusinessName());
        verify(companyRepository).save(any(Company.class));
    }

    @Test
    public void testUpdateCompany_AccessDenied() {
        User anotherUser = new User();
        anotherUser.setUsername("otherUser");
        mockCompany.setUser(anotherUser);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(mockCompany));

        CompanyDTO updateDTO = new CompanyDTO();

        assertThrows(AccessDeniedException.class, () -> {
            companyService.updateCompany(1L, updateDTO, "adminUser");
        });
    }
}
