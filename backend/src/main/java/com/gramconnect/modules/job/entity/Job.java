package com.gramconnect.modules.job.entity;

import com.gramconnect.common.entity.BaseAuditableEntity;
import com.gramconnect.modules.hierarchy.entity.Village;
import com.gramconnect.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Job Entity mapping `jobs`.
 * Represents seasonal farm labor or local construction job requirements posted by employers/farmers.
 */
@Entity
@Table(name = "jobs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employer_id", nullable = false)
    private User employer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "village_id", nullable = false)
    private Village village;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private JobCategory category;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "workers_needed", nullable = false)
    private Integer workersNeeded;

    @Builder.Default
    @Column(name = "workers_accepted", nullable = false)
    private Integer workersAccepted = 0;

    @Column(name = "daily_wage", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyWage;

    @Column(name = "total_budget", precision = 12, scale = 2)
    private BigDecimal totalBudget;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "required_skills", columnDefinition = "TEXT")
    private String requiredSkills;

    @Builder.Default
    @Column(name = "min_experience_years")
    private Integer minExperienceYears = 0;

    @Column(name = "gender_preference", length = 10)
    private String genderPreference;

    @Column(name = "location_details", columnDefinition = "TEXT")
    private String locationDetails;

    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private JobStatus status = JobStatus.OPEN;

    @Column(name = "filled_at")
    private Instant filledAt;

    @Builder.Default
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> applications = new ArrayList<>();

    public boolean hasRemainingCapacity() {
        return workersAccepted < workersNeeded;
    }
}
