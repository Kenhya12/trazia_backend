package com.trazia.trazia_project.entity.company;

import com.trazia.trazia_project.entity.user.User;

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

    @Column(name = "comercial_registry")
    private String comercial_regiString;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "website")
    private String website;

    @Lob
    @Column(name = "logo")
    private byte[] logo;
}
