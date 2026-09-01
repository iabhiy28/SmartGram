package com.gramconnect.modules.scheme.entity;

import com.gramconnect.common.entity.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * SchemeEligibilityRule Entity mapping `scheme_eligibility_rules`.
 * Key-value style eligibility criteria for government schemes.
 * e.g. rule_key = "MIN_AGE", rule_value = "18", description = "Applicant must be 18+"
 */
@Entity
@Table(name = "scheme_eligibility_rules")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchemeEligibilityRule extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scheme_id", nullable = false)
    private GovernmentScheme scheme;

    @Column(name = "rule_key", nullable = false, length = 50)
    private String ruleKey;

    @Column(name = "rule_value", nullable = false, length = 200)
    private String ruleValue;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
