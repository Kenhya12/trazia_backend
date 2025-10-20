package com.trazia.trazia_project.mapper.retention;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ConversionMapper {
    ConversionMapper INSTANCE = Mappers.getMapper(ConversionMapper.class);

    // Métodos de mapeo entre entidades y DTOs
}