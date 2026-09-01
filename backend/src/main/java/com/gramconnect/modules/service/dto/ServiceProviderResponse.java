package com.gramconnect.modules.service.dto;

import com.gramconnect.modules.service.entity.ServiceProviderProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProviderResponse {

    private UUID id;
    private UUID userId;
    private String fullName;
    private String phoneNumber;
    private UUID villageId;
    private String profileImageUrl;
    private String bio;
    private Integer experienceYears;
    private Integer serviceRadiusKm;
    private Boolean isAvailable;
    private String verificationStatus;
    private BigDecimal averageRating;
    private Integer totalReviews;
    private Integer totalCompletedJobs;
    private Instant createdAt;
    private List<ServiceOfferingResponse> offerings;

    public static ServiceProviderResponse fromEntity(ServiceProviderProfile profile) {
        List<ServiceOfferingResponse> offeringDtos = null;
        if (profile.getOfferings() != null) {
            offeringDtos = profile.getOfferings().stream()
                    .filter(o -> Boolean.TRUE.equals(o.getIsActive()))
                    .map(ServiceOfferingResponse::fromEntity)
                    .toList();
        }

        return ServiceProviderResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .fullName(profile.getUser().getFullName())
                .phoneNumber(profile.getUser().getPhoneNumber())
                .villageId(profile.getUser().getVillageId())
                .profileImageUrl(profile.getUser().getProfileImageUrl())
                .bio(profile.getBio())
                .experienceYears(profile.getExperienceYears())
                .serviceRadiusKm(profile.getServiceRadiusKm())
                .isAvailable(profile.getIsAvailable())
                .verificationStatus(profile.getVerificationStatus())
                .averageRating(profile.getAverageRating())
                .totalReviews(profile.getTotalReviews())
                .totalCompletedJobs(profile.getTotalCompletedJobs())
                .createdAt(profile.getCreatedAt())
                .offerings(offeringDtos)
                .build();
    }
}
