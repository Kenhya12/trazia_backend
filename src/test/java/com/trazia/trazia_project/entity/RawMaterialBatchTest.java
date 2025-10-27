package com.trazia.trazia_project.entity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.trazia.trazia_project.entity.batch.RawMaterialBatch;

import java.time.LocalDate;

public class RawMaterialBatchTest {

    @Test
    public void testRawMaterialBatchProperties() {
        RawMaterialBatch batch = new RawMaterialBatch();
        batch.setBatchNumber("Batch001");
        batch.setPurchaseDate(LocalDate.of(2025, 10, 10));
        batch.setReceivingDate(LocalDate.of(2025, 10, 11));
        batch.setSupplier(null); // asumiendo que Supplier es otra entidad

        assertEquals("Batch001", batch.getBatchNumber());
        assertEquals(LocalDate.of(2025, 10, 10), batch.getPurchaseDate());
        assertEquals(LocalDate.of(2025, 10, 11), batch.getReceivingDate());
        assertNull(batch.getSupplier());
    }
}
