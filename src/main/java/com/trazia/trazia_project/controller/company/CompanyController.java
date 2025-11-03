package com.trazia.trazia_project.controller.company;

import com.trazia.trazia_project.entity.company.Company;
import com.trazia.trazia_project.service.company.CompanyService;

import com.trazia.trazia_project.dto.company.CompanyDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import java.security.Principal;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/company")
public class CompanyController {

    private static final Logger log = LoggerFactory.getLogger(CompanyController.class);
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/register")
    public ResponseEntity<?> registerCompany(@Valid @RequestBody CompanyDTO companyDTO, Principal principal) {
        log.info("Usuario autenticado '{}' intenta registrar empresa '{}'", principal.getName(), companyDTO.getBusinessName());
        try {
            Company createdCompany = companyService.registerCompany(companyDTO, principal.getName());
            log.info("Empresa registrada correctamente: {}", createdCompany.getBusinessName());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCompany);
        } catch (Exception e) {
            log.error("Error registrando empresa para usuario {}: {}", principal.getName(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al registrar empresa: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCompany(@PathVariable Long id,
            @Valid @RequestBody CompanyDTO dto,
            Principal principal) {
        log.info("Usuario '{}' intenta actualizar empresa con id {}", principal.getName(), id);
        try {
            Company updatedCompany = companyService.updateCompany(id, dto, principal.getName());
            log.info("Empresa actualizada correctamente: {}", updatedCompany.getBusinessName());
            return ResponseEntity.ok(updatedCompany);
        } catch (Exception e) {
            log.error("Error actualizando empresa id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar empresa: " + e.getMessage());
        }
    }
}
