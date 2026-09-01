package com.gramconnect.modules.service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProviderProfileRequest {

    private String bio;

    @Min(value = 0, message = "Experience years cannot be negative")
    @Max(value = 60, message = "Experience years cannot exceed 60")
    private Integer experienceYears;

    @Min(value = 1, message = "Service radius must be at least 1 km")
    @Max(value = 100, message = "Service radius cannot exceed 100 km")
    private Integer serviceRadiusKm;

    private Boolean isAvailable;
    private String idProofUrl;
    private String skillCertificateUrl;
}
