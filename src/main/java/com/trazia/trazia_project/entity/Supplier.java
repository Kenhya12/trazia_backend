// Supplier.java
package com.trazia.trazia_project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del proveedor
    @Column(nullable = false)
    private String name;

    // Nombre de la persona de contacto
    @Column(name = "contact_name")
    private String contactName;

    // Email para contacto
    @Column(name = "contact_email")
    private String contactEmail;

    // Teléfono de contacto
    @Column(name = "contact_phone")
    private String contactPhone;

}