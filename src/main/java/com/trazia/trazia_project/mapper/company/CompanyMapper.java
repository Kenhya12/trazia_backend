package com.trazia.trazia_project.mapper.company;

import com.trazia.trazia_project.dto.company.CompanyDTO;
import com.trazia.trazia_project.entity.company.Company;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    Company toEntity(CompanyDTO dto);

    void updateEntityFromDTO(CompanyDTO dto, @MappingTarget Company entity);
}