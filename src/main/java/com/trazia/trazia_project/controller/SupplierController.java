package com.trazia.trazia_project.controller;

import com.trazia.trazia_project.entity.user.Supplier;
import com.trazia.trazia_project.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:3001", "http://localhost:5173" })
public class SupplierController {

    private final SupplierService supplierService;

    /**
     * GET - Obtener todos los proveedores
     */
    @GetMapping
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    /**
     * GET - Obtener proveedor por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        return supplierService.getSupplierById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST - Crear un nuevo proveedor
     */
    @PostMapping
    public ResponseEntity<Supplier> createSupplier(@RequestBody Supplier supplier) {
        Supplier createdSupplier = supplierService.createSupplier(supplier);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSupplier);
    }

    /**
     * PUT - Actualizar un proveedor
     */
    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier) {
        try {
            Supplier updatedSupplier = supplierService.updateSupplier(id, supplier);
            return ResponseEntity.ok(updatedSupplier);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE - Eliminar un proveedor
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        try {
            supplierService.deleteSupplier(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

