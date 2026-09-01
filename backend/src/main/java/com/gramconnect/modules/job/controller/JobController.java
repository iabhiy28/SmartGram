package com.gramconnect.modules.job.controller;

import com.gramconnect.common.dto.ApiResponse;
import com.gramconnect.common.dto.PageResponse;
import com.gramconnect.common.security.CustomUserDetails;
import com.gramconnect.modules.job.dto.CreateJobRequest;
import com.gramconnect.modules.job.dto.JobResponse;
import com.gramconnect.modules.job.entity.JobStatus;
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

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Tag(name = "Local Jobs Marketplace", description = "Endpoints for posting, browsing, searching, and managing agricultural and labor jobs")
public class JobController {

    private final JobMarketplaceService jobService;

    @PostMapping
    @Operation(summary = "Post a New Job", description = "Employers/Farmers post temporary or seasonal job requirements.")
    public ResponseEntity<ApiResponse<JobResponse>> createJob(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateJobRequest request) {

        JobResponse response = jobService.createJob(userDetails.getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Job posted successfully", response));
    }

    @GetMapping
    @Operation(summary = "Search & Browse Local Jobs", description = "Public search for open agricultural and labor jobs by village, category, and daily wage.")
    public ResponseEntity<ApiResponse<PageResponse<JobResponse>>> searchJobs(
            @RequestParam(required = false) UUID villageId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) JobStatus status,
            @RequestParam(required = false) BigDecimal minWage,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        PageResponse<JobResponse> response = jobService.searchJobs(villageId, categoryId, status, minWage, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Jobs retrieved successfully", response));
    }

    @GetMapping("/{jobId}")
    @Operation(summary = "Get Job Details by ID", description = "Fetches complete job posting metadata including remaining capacity.")
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(@PathVariable UUID jobId) {
        JobResponse response = jobService.getJobById(jobId);
        return ResponseEntity.ok(ApiResponse.ok("Job details retrieved successfully", response));
    }

    @GetMapping("/my-posted")
    @Operation(summary = "List Jobs Posted by Current Employer", description = "Fetches job postings created by the authenticated user.")
    public ResponseEntity<ApiResponse<PageResponse<JobResponse>>> getMyPostedJobs(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        PageResponse<JobResponse> response = jobService.getMyPostedJobs(userDetails.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok("Posted jobs retrieved successfully", response));
    }
}
