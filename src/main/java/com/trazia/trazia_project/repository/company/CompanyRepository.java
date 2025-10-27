package com.trazia.trazia_project.repository.company;

import com.trazia.trazia_project.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    // Puedes agregar consultas personalizadas si necesitas
}
