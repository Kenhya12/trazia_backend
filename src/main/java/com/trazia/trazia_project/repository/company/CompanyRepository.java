package com.trazia.trazia_project.repository.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trazia.trazia_project.entity.company.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    // Puedes agregar consultas personalizadas si necesitas
}
