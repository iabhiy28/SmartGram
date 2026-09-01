package com.gramconnect.modules.equipment.repository;

import com.gramconnect.modules.equipment.entity.Equipment;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Equipment e WHERE e.id = :id")
    Optional<Equipment> findEquipmentForUpdate(@Param("id") UUID id);

    Page<Equipment> findByOwnerId(UUID ownerId, Pageable pageable);

    @Query("""
            SELECT e FROM Equipment e
            WHERE e.isActive = true
              AND (:isOperational IS NULL OR e.isOperational = :isOperational)
              AND (:villageId IS NULL OR e.village.id = :villageId)
              AND (:categoryId IS NULL OR e.category.id = :categoryId)
            """)
    Page<Equipment> searchEquipment(
            @Param("villageId") UUID villageId,
            @Param("categoryId") UUID categoryId,
            @Param("isOperational") Boolean isOperational,
            Pageable pageable);
}
