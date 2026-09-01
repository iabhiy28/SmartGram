package com.gramconnect.modules.scheme.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * Eligibility screening request. The user provides their profile details
 * and we check them against each scheme's eligibility rules.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligibilityCheckRequest {

    @Min(value = 1, message = "Age must be at least 1")
    private Integer age;

    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;

    private Double annualIncome;

    @Pattern(regexp = "^(SC|ST|OBC|GENERAL)$", message = "Category must be SC, ST, OBC, or GENERAL")
    private String category;

    @Pattern(regexp = "^(BPL|APL)$", message = "Economic status must be BPL or APL")
    private String economicStatus;

    private String occupation;
    private String state;
    private Boolean isDisabled;
    private Boolean isMinority;
}
