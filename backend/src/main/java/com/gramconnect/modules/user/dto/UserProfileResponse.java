package com.gramconnect.modules.user.dto;

import com.gramconnect.modules.user.entity.Role;
import com.gramconnect.modules.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private UUID id;
    private String phoneNumber;
    private String email;
    private String fullName;
    private Role role;
    private UUID villageId;

    // Demographics
    private LocalDate dateOfBirth;
    private String gender;
    private String occupation;
    private BigDecimal annualIncome;
    private String casteCategory;
    private Boolean landOwnership;
    private String aadhaarLastFour;

    // Preferences
    private String profileImageUrl;
    private String languagePreference;
    private String bio;

    // Status
    private Boolean isActive;
    private Boolean isVerified;
    private Instant createdAt;

    public static UserProfileResponse fromEntity(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .villageId(user.getVillageId())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .occupation(user.getOccupation())
                .annualIncome(user.getAnnualIncome())
                .casteCategory(user.getCasteCategory())
                .landOwnership(user.getLandOwnership())
                .aadhaarLastFour(user.getAadhaarLastFour())
                .profileImageUrl(user.getProfileImageUrl())
                .languagePreference(user.getLanguagePreference())
                .bio(user.getBio())
                .isActive(user.getIsActive())
                .isVerified(user.getIsVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
