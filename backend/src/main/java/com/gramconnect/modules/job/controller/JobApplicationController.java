package com.gramconnect.modules.job.controller;

import com.gramconnect.common.dto.ApiResponse;
import com.gramconnect.common.dto.PageResponse;
import com.gramconnect.common.security.CustomUserDetails;
import com.gramconnect.modules.job.dto.*;
import com.gramconnect.modules.job.service.JobMarketplaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Applications & Ratings", description = "Endpoints for applying to jobs, capacity management, status workflows, and two-way ratings")
public class JobApplicationController {

    private final JobMarketplaceService jobService;

    @PostMapping("/{jobId}/apply")
    @Operation(summary = "Apply for a Job", description = "Villager applies for an open job requirement.")
    public ResponseEntity<ApiResponse<JobApplicationResponse>> applyForJob(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID jobId,
            @RequestBody(required = false) ApplyJobRequest request) {

        ApplyJobRequest req = request != null ? request : new ApplyJobRequest();
        JobApplicationResponse response = jobService.applyForJob(userDetails.getId(), jobId, req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Applied for job successfully", response));
    }

    @GetMapping("/applications/my")
    @Operation(summary = "List My Job Applications (Worker View)", description = "Fetches job applications submitted by the authenticated worker.")
    public ResponseEntity<ApiResponse<PageResponse<JobApplicationResponse>>> getMyApplications(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        PageResponse<JobApplicationResponse> response = jobService.getMyApplications(userDetails.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok("My applications retrieved successfully", response));
    }

    @GetMapping("/{jobId}/applicants")
    @Operation(summary = "List Applicants for a Job (Employer View)", description = "Employer reviews all applicants who applied to their job posting.")
    public ResponseEntity<ApiResponse<PageResponse<JobApplicationResponse>>> getJobApplicants(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID jobId,
            @PageableDefault(size = 20, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        PageResponse<JobApplicationResponse> response = jobService.getJobApplicants(jobId, userDetails.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok("Applicants retrieved successfully", response));
    }

    @PatchMapping("/applications/{applicationId}/status")
    @Operation(summary = "Update Application Status", description = "Employer shortlists/accepts/rejects candidate; worker withdraws.")
    public ResponseEntity<ApiResponse<JobApplicationResponse>> updateApplicationStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID applicationId,
            @Valid @RequestBody UpdateApplicationStatusRequest request) {

        JobApplicationResponse response = jobService.updateApplicationStatus(applicationId, userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Application status updated successfully", response));
    }

    @PostMapping("/applications/{applicationId}/rate-worker")
    @Operation(summary = "Employer Rates Worker", description = "Employer rates an accepted worker upon job completion.")
    public ResponseEntity<ApiResponse<JobApplicationResponse>> rateWorker(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID applicationId,
            @Valid @RequestBody RateWorkerRequest request) {

        JobApplicationResponse response = jobService.rateWorker(applicationId, userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Worker rated successfully", response));
    }

    @PostMapping("/applications/{applicationId}/rate-employer")
    @Operation(summary = "Worker Rates Employer", description = "Worker rates the employer regarding wage payment and conditions.")
    public ResponseEntity<ApiResponse<JobApplicationResponse>> rateEmployer(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID applicationId,
            @Valid @RequestBody RateEmployerRequest request) {

        JobApplicationResponse response = jobService.rateEmployer(applicationId, userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Employer rated successfully", response));
    }
}
