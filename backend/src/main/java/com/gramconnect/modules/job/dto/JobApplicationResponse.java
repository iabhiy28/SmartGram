package com.gramconnect.modules.job.dto;

import com.gramconnect.modules.job.entity.ApplicationStatus;
import com.gramconnect.modules.job.entity.JobApplication;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationResponse {

    private UUID id;
    private UUID jobId;
    private String jobTitle;
    private UUID applicantId;
    private String applicantName;
    private String applicantPhone;
    private String applicantOccupation;
    private ApplicationStatus status;
    private String coverNote;
    private Integer employerRating;
    private String employerFeedback;
    private Integer workerRating;
    private String workerFeedback;
    private Instant appliedAt;

    public static JobApplicationResponse fromEntity(JobApplication app) {
        return JobApplicationResponse.builder()
                .id(app.getId())
                .jobId(app.getJob().getId())
                .jobTitle(app.getJob().getTitle())
                .applicantId(app.getApplicant().getId())
                .applicantName(app.getApplicant().getFullName())
                .applicantPhone(app.getApplicant().getPhoneNumber())
                .applicantOccupation(app.getApplicant().getOccupation())
                .status(app.getStatus())
                .coverNote(app.getCoverNote())
                .employerRating(app.getEmployerRating())
                .employerFeedback(app.getEmployerFeedback())
                .workerRating(app.getWorkerRating())
                .workerFeedback(app.getWorkerFeedback())
                .appliedAt(app.getCreatedAt())
                .build();
    }
}
