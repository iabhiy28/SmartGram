package com.gramconnect.modules.scheme.dto;

import com.gramconnect.modules.scheme.entity.GovernmentScheme;
import com.gramconnect.modules.scheme.entity.SchemeEligibilityRule;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchemeResponse {
    private UUID id;
    private String title;
    private String description;
    private String schemeType;
    private String department;
    private String benefitsSummary;
    private String applicationProcess;
    private String requiredDocuments;
    private String officialLink;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private String targetState;
    private List<EligibilityRuleResponse> eligibilityRules;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EligibilityRuleResponse {
        private UUID id;
        private String ruleKey;
        private String ruleValue;
        private String description;

        public static EligibilityRuleResponse fromEntity(SchemeEligibilityRule rule) {
            return EligibilityRuleResponse.builder()
                    .id(rule.getId())
                    .ruleKey(rule.getRuleKey())
                    .ruleValue(rule.getRuleValue())
                    .description(rule.getDescription())
                    .build();
        }
    }

    public static SchemeResponse fromEntity(GovernmentScheme entity) {
        List<EligibilityRuleResponse> rules = entity.getEligibilityRules() != null
                ? entity.getEligibilityRules().stream().map(EligibilityRuleResponse::fromEntity).toList()
                : List.of();

        return SchemeResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .schemeType(entity.getSchemeType())
                .department(entity.getDepartment())
                .benefitsSummary(entity.getBenefitsSummary())
                .applicationProcess(entity.getApplicationProcess())
                .requiredDocuments(entity.getRequiredDocuments())
                .officialLink(entity.getOfficialLink())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .isActive(entity.getIsActive())
                .targetState(entity.getTargetState())
                .eligibilityRules(rules)
                .build();
    }
}
