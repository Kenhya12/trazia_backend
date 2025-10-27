package com.trazia.trazia_project.controller.company;

import com.trazia.trazia_project.entity.Company;
import com.trazia.trazia_project.service.company.CompanyService;
import com.trazia.trazia_project.entity.User;


import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.trazia.trazia_project.dto.company.CompanyDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RestController
@RequestMapping("/api/company")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }
    
    @JsonManagedReference
@OneToOne
@JoinColumn(name = "user_id", nullable = false, unique = true)
private User user;


    @PostMapping("/register")
    public ResponseEntity<?> registerCompany(@RequestBody CompanyDTO companyDTO, Principal principal) {
        Company createdCompany = companyService.registerCompany(companyDTO, principal.getName());
        return ResponseEntity.ok(createdCompany);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Company> updateCompany(@PathVariable Long id,
            @RequestBody CompanyDTO dto,
            Principal principal) {
        Company updatedCompany = companyService.updateCompany(id, dto, principal.getName());
        return ResponseEntity.ok(updatedCompany);
    }
}
