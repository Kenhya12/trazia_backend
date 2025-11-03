package com.trazia.trazia_project.mapper.company;

import org.springframework.stereotype.Component;
import com.trazia.trazia_project.dto.company.CompanyDTO;
import com.trazia.trazia_project.entity.company.Company;

@Component
public class CompanyMapperImpl implements CompanyMapper {

    @Override
    public Company toEntity(CompanyDTO dto) {
        if (dto == null)
            return null;
        Company company = new Company();
        company.setBusinessName(dto.getBusinessName());
        company.setAddress(dto.getAddress());
        // company.setCountry(dto.getCountry()); // comentar o eliminar si no existe
        return company;
    }

    @Override
    public void updateEntityFromDTO(CompanyDTO dto, Company entity) {
        if (dto == null || entity == null)
            return;
        entity.setBusinessName(dto.getBusinessName());
        entity.setAddress(dto.getAddress());
        // entity.setCountry(dto.getCountry()); // comentar o eliminar si no existe
    }
}