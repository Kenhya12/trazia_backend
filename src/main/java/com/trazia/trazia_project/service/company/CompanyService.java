package com.trazia.trazia_project.service.company;

import com.trazia.trazia_project.dto.company.CompanyDTO;
import com.trazia.trazia_project.entity.Company;
import com.trazia.trazia_project.entity.User;
import com.trazia.trazia_project.repository.CompanyRepository;
import com.trazia.trazia_project.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

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
        company.setBusinessName(dto.getBusinessName());
        company.setTaxId(dto.getTaxId());
        company.setAddress(dto.getAddress());
        company.setHealthRegistration(dto.getHealthRegistration());
        company.setLogo(dto.getLogo());
        
        // Asociación usuario -> empresa (asumiendo relación uno a uno)
        company.setUser(user);

        // Guardar empresa en base de datos
        return companyRepository.save(company);
    }
}
