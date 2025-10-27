package com.trazia.trazia_project.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trazia.trazia_project.entity.user.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
