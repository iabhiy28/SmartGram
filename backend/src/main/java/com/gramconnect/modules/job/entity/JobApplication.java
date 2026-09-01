package com.gramconnect.modules.job.entity;

import com.gramconnect.common.entity.BaseAuditableEntity;
import com.gramconnect.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * JobApplication Entity mapping `job_applications`.
 * Unique constraint on (job_id, applicant_id) ensures one application per citizen per job.
 */
@Entity
@Table(name = "job_applications", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"job_id", "applicant_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplication extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Column(name = "cover_note", columnDefinition = "TEXT")
    private String coverNote;

    // Two-way post-job rating
    @Column(name = "employer_rating")
    private Integer employerRating;

    @Column(name = "employer_feedback", columnDefinition = "TEXT")
    private String employerFeedback;

    @Column(name = "worker_rating")
    private Integer workerRating;

    @Column(name = "worker_feedback", columnDefinition = "TEXT")
    private String workerFeedback;

    // Lifecycle timestamps
    @Column(name = "shortlisted_at")
    private Instant shortlistedAt;

    @Column(name = "accepted_at")
    private Instant acceptedAt;

    @Column(name = "rejected_at")
    private Instant rejectedAt;

    @Column(name = "withdrawn_at")
    private Instant withdrawnAt;

    @Column(name = "completed_at")
    private Instant completedAt;
}
