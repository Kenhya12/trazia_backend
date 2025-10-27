package com.trazia.trazia_project.service.company;

import com.trazia.trazia_project.dto.company.CompanyDTO;
import com.trazia.trazia_project.entity.company.Company;
import com.trazia.trazia_project.entity.user.User;
import com.trazia.trazia_project.repository.company.CompanyRepository;
import com.trazia.trazia_project.repository.user.UserRepository;

import org.springframework.security.access.AccessDeniedException;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    /**
     * Registra una nueva empresa y la asocia al usuario administrador
     * @param dto Datos de la empresa
     * @param username Nombre de usuario del administrador
     * @return Empresa registrada
     */
    public Company registerCompany(CompanyDTO dto, String username) {
        
        // Buscar usuario por nombre de usuario
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Crear objeto empresa y asociarlo al usuario
        Company company = new Company();
        mapCompanyData(company, dto);
        
        // Asociación usuario -> empresa (asumiendo relación uno a uno)
        company.setUser(user);

        // Guardar la empresa y devolverla
        Company savedCompany = companyRepository.save(company);
        log.info("Company '{}' registered by user '{}'", dto.getBusinessName(), username);
        return savedCompany;
    }

    public Company updateCompany(Long companyId, CompanyDTO dto, String username) {
        // Verificar que la empresa exista y pertenezca al usuario
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));

        if (!company.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("User not authorized to update this company");
        }

        // Actualizar campos editables
        mapCompanyData(company, dto);
        Company savedCompany = companyRepository.save(company);
        log.info("Company '{}' updated by user '{}'", dto.getBusinessName(), username);
        return savedCompany;
    }

    private void mapCompanyData(Company company, CompanyDTO dto) {
        company.setBusinessName(dto.getBusinessName());
        company.setTaxId(dto.getTaxId());
        company.setAddress(dto.getAddress());
        company.setHealthRegistration(dto.getHealthRegistration());
        company.setLogo(dto.getLogo());
    }
}
