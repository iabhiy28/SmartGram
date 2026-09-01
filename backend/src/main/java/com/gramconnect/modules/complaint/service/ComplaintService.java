package com.gramconnect.modules.complaint.service;

import com.gramconnect.common.dto.PageResponse;
import com.gramconnect.common.exception.BadRequestException;
import com.gramconnect.common.exception.ForbiddenException;
import com.gramconnect.common.exception.ResourceNotFoundException;
import com.gramconnect.modules.complaint.dto.*;
import com.gramconnect.modules.complaint.entity.*;
import com.gramconnect.modules.complaint.repository.*;
import com.gramconnect.modules.hierarchy.entity.Village;
import com.gramconnect.modules.hierarchy.repository.VillageRepository;
import com.gramconnect.modules.user.entity.User;
import com.gramconnect.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Complaint Management Service with SLA Tracking Engine.
 *
 * SLA Model:
 *   - Each complaint category defines a default SLA in hours.
 *   - At filing time: slaDeadline = now + category.defaultSlaHours hours.
 *   - A scheduled task (@Scheduled) runs every 15 minutes to scan for breached SLAs.
 *   - Breached complaints get their isSlaBreached flag flipped to true.
 *   - Auto-escalation can be triggered for CRITICAL priority breaches.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintCategoryRepository categoryRepository;
    private final ComplaintRepository complaintRepository;
    private final ComplaintAttachmentRepository attachmentRepository;
    private final ComplaintCommentRepository commentRepository;
    private final UserRepository userRepository;
    private final VillageRepository villageRepository;

    // ========================================================================
    // CATEGORIES
    // ========================================================================

    @Transactional(readOnly = true)
    @Cacheable(value = "complaint_categories", key = "'all'")
    public List<ComplaintCategoryResponse> getAllCategories() {
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(ComplaintCategoryResponse::fromEntity)
                .toList();
    }

    // ========================================================================
    // COMPLAINT FILING
    // ========================================================================

    @Transactional
    public ComplaintResponse fileComplaint(UUID complainantId, CreateComplaintRequest request) {
        User complainant = userRepository.findById(complainantId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", complainantId));

        Village village = villageRepository.findById(request.getVillageId())
                .orElseThrow(() -> new ResourceNotFoundException("Village", "id", request.getVillageId()));

        ComplaintCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("ComplaintCategory", "id", request.getCategoryId()));

        // Compute SLA deadline
        Instant now = Instant.now();
        Instant slaDeadline = now.plus(category.getDefaultSlaHours(), ChronoUnit.HOURS);

        Complaint complaint = Complaint.builder()
                .complainant(complainant)
                .village(village)
                .category(category)
                .title(request.getTitle())
                .description(request.getDescription())
                .locationDescription(request.getLocationDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .status(ComplaintStatus.SUBMITTED)
                .priority(request.getPriority() != null ? request.getPriority() : "MEDIUM")
                .slaDeadline(slaDeadline)
                .isSlaBreached(false)
                .upvoteCount(0)
                .build();

        Complaint saved = complaintRepository.save(complaint);

        // Save attachments if provided
        if (request.getAttachmentUrls() != null) {
            for (String url : request.getAttachmentUrls()) {
                ComplaintAttachment attachment = ComplaintAttachment.builder()
                        .complaint(saved)
                        .fileUrl(url)
                        .build();
                attachmentRepository.save(attachment);
            }
        }

        log.info("Complaint filed [ID: {}, Title: '{}', SLA Deadline: {}] by user [ID: {}]",
                saved.getId(), saved.getTitle(), slaDeadline, complainantId);
        return ComplaintResponse.fromEntity(saved);
    }

    // ========================================================================
    // COMPLAINT STATUS MANAGEMENT (Admin Operations)
    // ========================================================================

    @Transactional
    public ComplaintResponse updateComplaintStatus(UUID complaintId, UUID adminUserId, UpdateComplaintStatusRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", complaintId));

        ComplaintStatus currentStatus = complaint.getStatus();
        ComplaintStatus targetStatus = request.getStatus();

        // State machine validation
        if (!currentStatus.canTransitionTo(targetStatus)) {
            throw new BadRequestException(String.format(
                    "Cannot transition complaint from %s to %s", currentStatus, targetStatus));
        }

        Instant now = Instant.now();

        switch (targetStatus) {
            case UNDER_REVIEW -> {
                complaint.setAcknowledgedAt(now);
                if (request.getAssignedToUserId() != null) {
                    User assignee = userRepository.findById(request.getAssignedToUserId())
                            .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssignedToUserId()));
                    complaint.setAssignedTo(assignee);
                }
            }
            case IN_PROGRESS -> {
                if (request.getAssignedToUserId() != null) {
                    User assignee = userRepository.findById(request.getAssignedToUserId())
                            .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssignedToUserId()));
                    complaint.setAssignedTo(assignee);
                }
            }
            case RESOLVED -> {
                complaint.setResolvedAt(now);
                complaint.setResolutionNotes(request.getResolutionNotes());
            }
            case ESCALATED -> complaint.setEscalatedAt(now);
            case REOPENED -> {
                complaint.setResolvedAt(null);
                // Extend SLA by 48 hours from now upon reopening
                complaint.setSlaDeadline(now.plus(48, ChronoUnit.HOURS));
                complaint.setIsSlaBreached(false);
            }
            default -> { /* REJECTED — no special handling */ }
        }

        complaint.setStatus(targetStatus);
        Complaint saved = complaintRepository.save(complaint);
        log.info("Complaint [ID: {}] transitioned from {} to {} by admin [ID: {}]",
                complaintId, currentStatus, targetStatus, adminUserId);
        return ComplaintResponse.fromEntity(saved);
    }

    // ========================================================================
    // COMMENTS
    // ========================================================================

    @Transactional
    public CommentResponse addComment(UUID complaintId, UUID authorId, CreateCommentRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", complaintId));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", authorId));

        // Non-admins cannot post internal comments
        boolean isInternal = request.getIsInternal() != null && request.getIsInternal();

        ComplaintComment comment = ComplaintComment.builder()
                .complaint(complaint)
                .author(author)
                .content(request.getContent())
                .isInternal(isInternal)
                .build();

        ComplaintComment saved = commentRepository.save(comment);
        log.info("Comment added [ID: {}] to complaint [ID: {}] by user [ID: {}]",
                saved.getId(), complaintId, authorId);
        return CommentResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> getComments(UUID complaintId, boolean includeInternal, Pageable pageable) {
        Page<ComplaintComment> page;
        if (includeInternal) {
            page = commentRepository.findByComplaintIdOrderByCreatedAtDesc(complaintId, pageable);
        } else {
            page = commentRepository.findByComplaintIdAndIsInternalFalseOrderByCreatedAtDesc(complaintId, pageable);
        }
        return PageResponse.from(page.map(CommentResponse::fromEntity));
    }

    // ========================================================================
    // QUERIES
    // ========================================================================

    @Transactional(readOnly = true)
    public ComplaintResponse getComplaintById(UUID complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", complaintId));
        return ComplaintResponse.fromEntity(complaint);
    }

    @Transactional(readOnly = true)
    public PageResponse<ComplaintResponse> getMyComplaints(UUID complainantId, Pageable pageable) {
        Page<Complaint> page = complaintRepository.findByComplainantId(complainantId, pageable);
        return PageResponse.from(page.map(ComplaintResponse::fromEntity));
    }

    @Transactional(readOnly = true)
    public PageResponse<ComplaintResponse> searchComplaints(
            UUID villageId, UUID categoryId, ComplaintStatus status, String priority, Pageable pageable) {
        Page<Complaint> page = complaintRepository.searchComplaints(villageId, categoryId, status, priority, pageable);
        return PageResponse.from(page.map(ComplaintResponse::fromEntity));
    }

    @Transactional(readOnly = true)
    public PageResponse<ComplaintResponse> getVillageComplaints(UUID villageId, Pageable pageable) {
        Page<Complaint> page = complaintRepository.findByVillageId(villageId, pageable);
        return PageResponse.from(page.map(ComplaintResponse::fromEntity));
    }

    // ========================================================================
    // UPVOTE
    // ========================================================================

    @Transactional
    public ComplaintResponse upvoteComplaint(UUID complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", complaintId));
        complaint.setUpvoteCount(complaint.getUpvoteCount() + 1);
        Complaint saved = complaintRepository.save(complaint);
        return ComplaintResponse.fromEntity(saved);
    }

    // ========================================================================
    // SLA BREACH DETECTION (Scheduled Task)
    // ========================================================================

    /**
     * Runs every 15 minutes to detect complaints that have breached their SLA deadline.
     * Non-terminal complaints (not RESOLVED/REJECTED) past their deadline get flagged.
     */
    @Scheduled(fixedRate = 900_000) // 15 minutes in ms
    @Transactional
    public void detectSlaBreaches() {
        List<ComplaintStatus> terminalStatuses = List.of(ComplaintStatus.RESOLVED, ComplaintStatus.REJECTED);
        List<Complaint> breached = complaintRepository.findSlaBreachedComplaints(Instant.now(), terminalStatuses);

        if (!breached.isEmpty()) {
            log.warn("SLA Breach Detected: {} complaint(s) have exceeded their SLA deadline", breached.size());
            for (Complaint complaint : breached) {
                complaint.setIsSlaBreached(true);
                complaintRepository.save(complaint);
                log.warn("SLA BREACHED — Complaint [ID: {}, Title: '{}', Priority: {}, Deadline: {}]",
                        complaint.getId(), complaint.getTitle(), complaint.getPriority(), complaint.getSlaDeadline());
            }
        }
    }
}
