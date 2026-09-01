package com.gramconnect.modules.job.dto;

import com.gramconnect.modules.job.entity.Job;
import com.gramconnect.modules.job.entity.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {

    private UUID id;
    private UUID employerId;
    private String employerName;
    private String employerPhone;
    private UUID villageId;
    private String villageName;
    private UUID categoryId;
    private String categoryDisplayName;
    private String title;
    private String description;
    private Integer workersNeeded;
    private Integer workersAccepted;
    private Integer remainingSpots;
    private BigDecimal dailyWage;
    private BigDecimal totalBudget;
    private LocalDate startDate;
    private LocalDate endDate;
    private String requiredSkills;
    private Integer minExperienceYears;
    private String genderPreference;
    private String locationDetails;
    private JobStatus status;
    private Instant createdAt;

    public static JobResponse fromEntity(Job job) {
        int remaining = Math.max(0, job.getWorkersNeeded() - job.getWorkersAccepted());
        return JobResponse.builder()
                .id(job.getId())
                .employerId(job.getEmployer().getId())
                .employerName(job.getEmployer().getFullName())
                .employerPhone(job.getEmployer().getPhoneNumber())
                .villageId(job.getVillage().getId())
                .villageName(job.getVillage().getName())
                .categoryId(job.getCategory().getId())
                .categoryDisplayName(job.getCategory().getDisplayName())
                .title(job.getTitle())
                .description(job.getDescription())
                .workersNeeded(job.getWorkersNeeded())
                .workersAccepted(job.getWorkersAccepted())
                .remainingSpots(remaining)
                .dailyWage(job.getDailyWage())
                .totalBudget(job.getTotalBudget())
                .startDate(job.getStartDate())
                .endDate(job.getEndDate())
                .requiredSkills(job.getRequiredSkills())
                .minExperienceYears(job.getMinExperienceYears())
                .genderPreference(job.getGenderPreference())
                .locationDetails(job.getLocationDetails())
                .status(job.getStatus())
                .createdAt(job.getCreatedAt())
                .build();
    }
}
