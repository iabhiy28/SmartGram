package com.gramconnect.modules.auth.dto;

import com.gramconnect.modules.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private long expiresInMs;
    private UUID userId;
    private String phoneNumber;
    private String fullName;
    private Role role;
    private UUID villageId;
    private String languagePreference;
}
