package com.gramconnect.modules.complaint.repository;

import com.gramconnect.modules.complaint.entity.Complaint;
import com.gramconnect.modules.complaint.entity.ComplaintStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, UUID> {

    Page<Complaint> findByComplainantId(UUID complainantId, Pageable pageable);

    Page<Complaint> findByVillageId(UUID villageId, Pageable pageable);

    @Query("""
            SELECT c FROM Complaint c
            WHERE (:villageId IS NULL OR c.village.id = :villageId)
              AND (:categoryId IS NULL OR c.category.id = :categoryId)
              AND (:status IS NULL OR c.status = :status)
              AND (:priority IS NULL OR c.priority = :priority)
            """)
    Page<Complaint> searchComplaints(
            @Param("villageId") UUID villageId,
            @Param("categoryId") UUID categoryId,
            @Param("status") ComplaintStatus status,
            @Param("priority") String priority,
            Pageable pageable);

    /** Find complaints that have breached their SLA (deadline passed, not resolved/rejected) */
    @Query("""
            SELECT c FROM Complaint c
            WHERE c.slaDeadline < :now
              AND c.isSlaBreached = false
              AND c.status NOT IN :terminalStatuses
            """)
    List<Complaint> findSlaBreachedComplaints(
            @Param("now") Instant now,
            @Param("terminalStatuses") List<ComplaintStatus> terminalStatuses);

    /** Count complaints by status for a village — dashboard analytics */
    @Query("SELECT c.status, COUNT(c) FROM Complaint c WHERE c.village.id = :villageId GROUP BY c.status")
    List<Object[]> countByStatusForVillage(@Param("villageId") UUID villageId);
}
