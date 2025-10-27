package com.trazia.trazia_project.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trazia.trazia_project.entity.batch.FinalProductBatch;

import java.time.LocalDate;

@Repository
public interface FinalProductBatchRepository extends JpaRepository<FinalProductBatch, Long> {

    long countByProductionDate(LocalDate productionDate);
}

