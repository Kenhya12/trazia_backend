package com.trazia.trazia_project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "empresas")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Company {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "tax_id", nullable = false, unique = true)
    private String taxId;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "health_registration")
    private String healthRegistration;

    @Lob
    @Column(name = "logo")
    private byte[] logo;
}
