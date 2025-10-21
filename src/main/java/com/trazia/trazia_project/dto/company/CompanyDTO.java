package com.trazia.trazia_project.dto.company;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {

    private String businessName;
    private String taxId;
    private String address;
    private String healthRegistration;
    private byte[] logo; // Puede ser base64 codificado si se adapta al frontend
}

