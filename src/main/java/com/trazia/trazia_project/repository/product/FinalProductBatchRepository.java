package com.trazia.trazia_project.repository.product;

import com.trazia.trazia_project.entity.FinalProductBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface FinalProductBatchRepository extends JpaRepository<FinalProductBatch, Long> {

    long countByProductionDate(LocalDate productionDate);
}

