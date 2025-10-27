package com.trazia.trazia_project.service.company;

import com.trazia.trazia_project.dto.company.CompanyDTO;
import com.trazia.trazia_project.entity.company.Company;
import com.trazia.trazia_project.entity.user.User;
import com.trazia.trazia_project.exception.user.UserNotFoundException;
import com.trazia.trazia_project.exception.company.CompanyNotFoundException;
import com.trazia.trazia_project.mapper.company.CompanyMapper;
import com.trazia.trazia_project.repository.company.CompanyRepository;
import com.trazia.trazia_project.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper; // Mapper de MapStruct

    /**
     * Registra una nueva empresa y la asocia al usuario administrador
     * @param dto Datos de la empresa
     * @param username Nombre de usuario del administrador
     * @return Empresa registrada
     */
    public Company registerCompany(CompanyDTO dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        Company company = companyMapper.toEntity(dto);
        company.setUser(user);

        Company savedCompany = companyRepository.save(company);
        log.info("Company '{}' registered by user '{}'", dto.getBusinessName(), username);
        return savedCompany;
    }

    /**
     * Actualiza los datos de la empresa asociada al usuario
     * @param companyId Id de la empresa
     * @param dto Datos de la empresa
     * @param username Nombre de usuario del administrador
     * @return Empresa actualizada
     */
    public Company updateCompany(Long companyId, CompanyDTO dto, String username) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found"));

        if (!company.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("User not authorized to update this company");
        }

        companyMapper.updateEntityFromDTO(dto, company);

        Company savedCompany = companyRepository.save(company);
        log.info("Company '{}' updated by user '{}'", dto.getBusinessName(), username);
        return savedCompany;
    }
}