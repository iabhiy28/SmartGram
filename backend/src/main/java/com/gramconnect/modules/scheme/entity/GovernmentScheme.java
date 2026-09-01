package com.gramconnect.modules.scheme.entity;

import com.gramconnect.common.entity.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * GovernmentScheme Entity mapping `government_schemes`.
 * Central/State welfare schemes with eligibility screening.
 */
@Entity
@Table(name = "government_schemes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GovernmentScheme extends BaseAuditableEntity {

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "scheme_type", nullable = false, length = 30)
    private String schemeType; // CENTRAL, STATE

    @Column(name = "department", length = 150)
    private String department;

    @Column(name = "benefits_summary", columnDefinition = "TEXT")
    private String benefitsSummary;

    @Column(name = "application_process", columnDefinition = "TEXT")
    private String applicationProcess;

    @Column(name = "required_documents", columnDefinition = "TEXT")
    private String requiredDocuments;

    @Column(name = "official_link", length = 500)
    private String officialLink;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "target_state", length = 100)
    private String targetState;

    @Builder.Default
    @OneToMany(mappedBy = "scheme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SchemeEligibilityRule> eligibilityRules = new ArrayList<>();
}
