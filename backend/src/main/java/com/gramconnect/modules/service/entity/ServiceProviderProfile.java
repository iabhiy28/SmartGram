package com.gramconnect.modules.service.entity;

import com.gramconnect.common.entity.BaseAuditableEntity;
import com.gramconnect.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Extended profile for users with ROLE_SERVICE_PROVIDER, mapping `service_provider_profiles`.
 */
@Entity
@Table(name = "service_provider_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProviderProfile extends BaseAuditableEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Builder.Default
    @Column(name = "experience_years")
    private Integer experienceYears = 0;

    @Builder.Default
    @Column(name = "service_radius_km", nullable = false)
    private Integer serviceRadiusKm = 10;

    @Builder.Default
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    // Verification
    @Column(name = "id_proof_url", length = 500)
    private String idProofUrl;

    @Column(name = "skill_certificate_url", length = 500)
    private String skillCertificateUrl;

    @Builder.Default
    @Column(name = "verification_status", nullable = false, length = 20)
    private String verificationStatus = "PENDING"; // PENDING, VERIFIED, REJECTED

    @Column(name = "verified_by_id")
    private UUID verifiedById;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    // Transactionally maintained rating aggregates (O(1) read performance)
    @Builder.Default
    @Column(name = "average_rating", nullable = false, precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_reviews", nullable = false)
    private Integer totalReviews = 0;

    @Builder.Default
    @Column(name = "total_completed_jobs", nullable = false)
    private Integer totalCompletedJobs = 0;

    @Builder.Default
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceOffering> offerings = new ArrayList<>();
}
