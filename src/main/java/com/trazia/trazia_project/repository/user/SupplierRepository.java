package com.trazia.trazia_project.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trazia.trazia_project.entity.user.Supplier;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByName(String name);
    boolean existsByName(String name);
}