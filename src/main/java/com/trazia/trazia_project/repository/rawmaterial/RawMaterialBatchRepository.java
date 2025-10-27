package com.trazia.trazia_project.repository.rawmaterial;

import com.trazia.trazia_project.entity.RawMaterialBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawMaterialBatchRepository extends JpaRepository<RawMaterialBatch, Long> {
}
