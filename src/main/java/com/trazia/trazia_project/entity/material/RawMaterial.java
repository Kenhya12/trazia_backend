package com.trazia.trazia_project.entity.material;

import com.trazia.trazia_project.entity.user.Supplier;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "raw_materials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "cost_per_unit", precision = 10, scale = 2)
    private BigDecimal costPerUnit;

    @Column(length = 20)
    private String unit;

    @Column(length = 50)
    private String category;

    // Nuevos campos
    @Column(name = "internal_code", length = 50)
    private String internalCode;
    
    @Column(name = "min_stock", precision = 10, scale = 2)
    private BigDecimal minStock;
    
    @Column(name = "current_stock", precision = 10, scale = 2)
    private BigDecimal currentStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}