package com.trazia.trazia_project.service.impl;

import com.trazia.trazia_project.entity.user.Supplier;
import com.trazia.trazia_project.service.SupplierService;
import com.trazia.trazia_project.repository.user.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Override
    public Optional<Supplier> getSupplierById(@NonNull Long id) {
        return supplierRepository.findById(id);
    }

    @Override
    public Supplier createSupplier(@NonNull Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Override
    public Supplier updateSupplier(@NonNull Long id, @NonNull Supplier supplier) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found with id: " + id);
        }
        supplier.setId(id);
        return supplierRepository.save(supplier);
    }

    @Override
    public void deleteSupplier(@NonNull Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found with id: " + id);
        }
        supplierRepository.deleteById(id);
    }
}
