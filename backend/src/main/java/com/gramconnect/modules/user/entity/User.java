package com.gramconnect.modules.user.entity;

import com.gramconnect.common.entity.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Core User Entity mapping the `users` table.
 * Serves as the central identity for Villagers, Service Providers, Employers, Admins, and Super Admins.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseAuditableEntity {

    @Column(name = "phone_number", nullable = false, unique = true, length = 15)
    private String phoneNumber;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private Role role;

    @Column(name = "village_id")
    private UUID villageId;

    // Demographics for Government Scheme Eligibility Screening
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "occupation", length = 50)
    private String occupation;

    @Column(name = "annual_income", precision = 12, scale = 2)
    private BigDecimal annualIncome;

    @Column(name = "caste_category", length = 30)
    private String casteCategory;

    @Builder.Default
    @Column(name = "land_ownership")
    private Boolean landOwnership = false;

    @Column(name = "aadhaar_last_four", length = 4)
    private String aadhaarLastFour;

    // Profile & UX Preferences
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Builder.Default
    @Column(name = "language_preference", nullable = false, length = 5)
    private String languagePreference = "en";

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    // Account Status
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;
}
