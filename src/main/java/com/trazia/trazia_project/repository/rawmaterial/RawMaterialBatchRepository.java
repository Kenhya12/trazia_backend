package com.trazia.trazia_project.repository.rawmaterial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trazia.trazia_project.entity.batch.RawMaterialBatch;

@Repository
public interface RawMaterialBatchRepository extends JpaRepository<RawMaterialBatch, Long> {
}
