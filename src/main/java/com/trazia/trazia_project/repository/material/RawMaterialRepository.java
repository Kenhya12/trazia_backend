package com.trazia.trazia_project.repository.material;

import com.trazia.trazia_project.entity.material.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {
    List<RawMaterial> findByNameContainingIgnoreCase(String name);
    List<RawMaterial> findByCategory(String category);
}