package com.gramconnect.modules.user.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Email(message = "Email format is invalid")
    private String email;

    private UUID villageId;

    private LocalDate dateOfBirth;

    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;

    private String occupation;

    @DecimalMin(value = "0.0", message = "Annual income cannot be negative")
    private BigDecimal annualIncome;

    @Pattern(regexp = "^(GENERAL|OBC|SC|ST|EWS)$", message = "Caste category must be GENERAL, OBC, SC, ST, or EWS")
    private String casteCategory;

    private Boolean landOwnership;

    @Pattern(regexp = "^(en|hi|kn)$", message = "Language preference must be 'en', 'hi', or 'kn'")
    private String languagePreference;

    private String bio;
}
