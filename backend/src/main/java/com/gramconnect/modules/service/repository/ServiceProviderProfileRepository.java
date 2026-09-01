package com.gramconnect.modules.service.repository;

import com.gramconnect.modules.service.entity.ServiceProviderProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceProviderProfileRepository extends JpaRepository<ServiceProviderProfile, UUID> {

    Optional<ServiceProviderProfile> findByUserId(UUID userId);

    long countByVerificationStatus(String verificationStatus);

    /**
     * Search verified service providers with optional filters:
     * - Village ID
     * - Category ID (via offerings)
     * - Minimum rating
     * - Availability
     */
    @Query("""
            SELECT DISTINCT p FROM ServiceProviderProfile p
            JOIN p.user u
            LEFT JOIN p.offerings o
            WHERE (:verificationStatus IS NULL OR p.verificationStatus = :verificationStatus)
              AND (:isAvailable IS NULL OR p.isAvailable = :isAvailable)
              AND (:villageId IS NULL OR u.villageId = :villageId)
              AND (:categoryId IS NULL OR o.category.id = :categoryId)
              AND (:minRating IS NULL OR p.averageRating >= :minRating)
            """)
    Page<ServiceProviderProfile> searchProviders(
            @Param("villageId") UUID villageId,
            @Param("categoryId") UUID categoryId,
            @Param("minRating") BigDecimal minRating,
            @Param("isAvailable") Boolean isAvailable,
            @Param("verificationStatus") String verificationStatus,
            Pageable pageable);
}
