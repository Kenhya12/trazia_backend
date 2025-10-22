// DocumentDTO.java
package com.trazia.trazia_project.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDTO {
    private String documentName;
    private String qualityCertificateUrl;
    private String labAnalysisUrl;
}

