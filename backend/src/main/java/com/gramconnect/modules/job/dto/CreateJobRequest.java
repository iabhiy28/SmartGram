package com.gramconnect.modules.job.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateJobRequest {

    @NotBlank(message = "Job title is required")
    @Size(min = 3, max = 150, message = "Job title must be between 3 and 150 characters")
    private String title;

    @NotNull(message = "Job category is required")
    private UUID categoryId;

    @NotNull(message = "Village is required")
    private UUID villageId;

    private String description;

    @NotNull(message = "Number of workers needed is required")
    @Min(value = 1, message = "At least 1 worker is required")
    @Max(value = 200, message = "Workers needed cannot exceed 200")
    private Integer workersNeeded;

    @NotNull(message = "Daily wage is required")
    @DecimalMin(value = "50.0", message = "Daily wage must be at least ₹50")
    private BigDecimal dailyWage;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private String requiredSkills;
    private Integer minExperienceYears;

    @Pattern(regexp = "^(MALE|FEMALE|ANY)$", message = "Gender preference must be MALE, FEMALE, or ANY")
    private String genderPreference;

    private String locationDetails;
}
