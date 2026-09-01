package com.gramconnect.modules.hierarchy.repository;

import com.gramconnect.modules.hierarchy.entity.Village;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface VillageRepository extends JpaRepository<Village, UUID> {

    List<Village> findByPanchayatIdOrderByNameAsc(UUID panchayatId);

    List<Village> findByPinCodeOrderByNameAsc(String pinCode);

    /**
     * Bounding box and Haversine distance query to discover nearby villages within radius (km).
     * Earth radius = 6371 km.
     */
    @Query(value = """
            SELECT v.* FROM villages v
            WHERE v.latitude IS NOT NULL AND v.longitude IS NOT NULL
              AND (6371 * acos(
                    cos(radians(:latitude)) * cos(radians(v.latitude)) *
                    cos(radians(v.longitude) - radians(:longitude)) +
                    sin(radians(:latitude)) * sin(radians(v.latitude))
                  )) <= :radiusKm
            ORDER BY (
                6371 * acos(
                    cos(radians(:latitude)) * cos(radians(v.latitude)) *
                    cos(radians(v.longitude) - radians(:longitude)) +
                    sin(radians(:latitude)) * sin(radians(v.latitude))
                )
            ) ASC
            LIMIT :limit
            """, nativeQuery = true)
    List<Village> findNearbyVillages(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radiusKm") double radiusKm,
            @Param("limit") int limit);
}
