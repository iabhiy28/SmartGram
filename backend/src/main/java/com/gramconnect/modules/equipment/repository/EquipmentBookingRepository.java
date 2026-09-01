package com.gramconnect.modules.equipment.repository;

import com.gramconnect.modules.equipment.entity.EquipmentBooking;
import com.gramconnect.modules.equipment.entity.EquipmentBookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface EquipmentBookingRepository extends JpaRepository<EquipmentBooking, UUID> {

    /**
     * Date-range overlap collision detection.
     * Two date ranges overlap when: startA <= endB AND endA >= startB.
     * Only counts bookings in date-blocking statuses (PENDING, CONFIRMED, ACTIVE).
     */
    @Query("""
            SELECT COUNT(b) FROM EquipmentBooking b
            WHERE b.equipment.id = :equipmentId
              AND b.status IN :blockingStatuses
              AND b.startDate <= :endDate
              AND b.endDate >= :startDate
            """)
    long countOverlappingBookings(
            @Param("equipmentId") UUID equipmentId,
            @Param("blockingStatuses") java.util.List<EquipmentBookingStatus> blockingStatuses,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Overloaded convenience — excludes a specific booking ID (useful for status updates
     * where we need to check if other bookings overlap after cancellation).
     */
    @Query("""
            SELECT COUNT(b) FROM EquipmentBooking b
            WHERE b.equipment.id = :equipmentId
              AND b.id <> :excludeBookingId
              AND b.status IN :blockingStatuses
              AND b.startDate <= :endDate
              AND b.endDate >= :startDate
            """)
    long countOverlappingBookingsExcluding(
            @Param("equipmentId") UUID equipmentId,
            @Param("excludeBookingId") UUID excludeBookingId,
            @Param("blockingStatuses") java.util.List<EquipmentBookingStatus> blockingStatuses,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /** All bookings placed by a specific renter */
    Page<EquipmentBooking> findByRenterId(UUID renterId, Pageable pageable);

    /** All bookings for equipment owned by a specific owner */
    @Query("""
            SELECT b FROM EquipmentBooking b
            WHERE b.equipment.owner.id = :ownerId
            ORDER BY b.createdAt DESC
            """)
    Page<EquipmentBooking> findByEquipmentOwnerId(@Param("ownerId") UUID ownerId, Pageable pageable);

    /** All bookings for a specific equipment listing */
    Page<EquipmentBooking> findByEquipmentId(UUID equipmentId, Pageable pageable);
}
