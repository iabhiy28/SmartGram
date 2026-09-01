package com.gramconnect.modules.auth.dto;

import com.gramconnect.modules.user.entity.Role;
import jakarta.validation.constraints.*;
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
public class RegisterRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be a valid 10-digit Indian mobile number")
    private String phoneNumber;

    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 50, message = "Password must be between 6 and 50 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotNull(message = "Role is required")
    private Role role;

    private UUID villageId;

    // Optional Demographics for Government Scheme Eligibility Matching
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;

    private String occupation;

    @DecimalMin(value = "0.0", message = "Annual income cannot be negative")
    private BigDecimal annualIncome;

    @Pattern(regexp = "^(GENERAL|OBC|SC|ST|EWS)$", message = "Caste category must be GENERAL, OBC, SC, ST, or EWS")
    private String casteCategory;

    private Boolean landOwnership;

    @Pattern(regexp = "^\\d{4}$", message = "Aadhaar last four must be exactly 4 digits")
    private String aadhaarLastFour;

    @Pattern(regexp = "^(en|hi|kn)$", message = "Language preference must be 'en', 'hi', or 'kn'")
    private String languagePreference;
}
