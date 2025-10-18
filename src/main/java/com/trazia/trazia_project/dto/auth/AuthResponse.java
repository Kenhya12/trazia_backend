package com.trazia.trazia_project.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    
    @Builder.Default  // ← AÑADIR
    private String type = "Bearer";
    
    private String username;
    private String email;
}




