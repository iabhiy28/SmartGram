package com.gramconnect.modules.announcement.repository;

import com.gramconnect.modules.announcement.entity.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, UUID> {

    @Query("""
            SELECT e FROM EmergencyContact e
            WHERE e.isActive = true
              AND (e.village.id = :villageId OR e.village IS NULL)
            ORDER BY e.displayOrder ASC
            """)
    List<EmergencyContact> findActiveContactsForVillage(@Param("villageId") UUID villageId);

    List<EmergencyContact> findByIsActiveTrueOrderByDisplayOrderAsc();
}
