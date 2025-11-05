package com.trazia.trazia_project.service.material;

import com.trazia.trazia_project.entity.material.RawMaterial;
import com.trazia.trazia_project.dto.material.RawMaterialDTO;
import java.util.List;
import java.util.Optional;

public interface RawMaterialService {
    List<RawMaterial> findAll();
    Optional<RawMaterial> findById(Long id);
    RawMaterial save(RawMaterial rawMaterial);
    void deleteById(Long id);
    boolean existsById(Long id);
    RawMaterialDTO convertToDTO(RawMaterial rawMaterial);
    RawMaterial convertToEntity(RawMaterialDTO rawMaterialDTO);
}