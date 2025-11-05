package com.trazia.trazia_project.service;

import com.trazia.trazia_project.entity.user.Supplier;

import java.util.List;
import java.util.Optional;

public interface SupplierService {
    List<Supplier> getAllSuppliers();
    Optional<Supplier> getSupplierById(Long id);
    Supplier createSupplier(Supplier supplier); // ✅ DEBE EXISTIR
    Supplier updateSupplier(Long id, Supplier supplier);
    void deleteSupplier(Long id);
}

