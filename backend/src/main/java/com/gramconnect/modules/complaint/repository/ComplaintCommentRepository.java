package com.gramconnect.modules.complaint.repository;

import com.gramconnect.modules.complaint.entity.ComplaintComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ComplaintCommentRepository extends JpaRepository<ComplaintComment, UUID> {

    Page<ComplaintComment> findByComplaintIdOrderByCreatedAtDesc(UUID complaintId, Pageable pageable);

    /** Non-internal comments visible to the complainant */
    Page<ComplaintComment> findByComplaintIdAndIsInternalFalseOrderByCreatedAtDesc(UUID complaintId, Pageable pageable);
}
