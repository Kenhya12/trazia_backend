package com.trazia.trazia_project.entity.product;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_labels")
@Data
public class ProductLabel {

    @Id
    private String id;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String companyAddress;

    @Column(nullable = false)
    private String countryOfOrigin;

    @Column(nullable = false)
    private String batchNumber;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String ingredients;

    @ElementCollection
    @CollectionTable(name = "label_allergens", joinColumns = @JoinColumn(name = "label_id"))
    @Column(name = "allergen")
    private List<String> allergens = new ArrayList<>();

    @Column(nullable = false)
    private String language = "es";

    @Column(nullable = false)
    private String status = "draft";

    private Integer version = 1;
    
    private LocalDate createdAt;
    private LocalDate updatedAt;
}