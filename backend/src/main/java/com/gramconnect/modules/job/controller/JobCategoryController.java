package com.gramconnect.modules.job.controller;

import com.gramconnect.common.dto.ApiResponse;
import com.gramconnect.modules.job.dto.JobCategoryResponse;
import com.gramconnect.modules.job.service.JobMarketplaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs/categories")
@RequiredArgsConstructor
@Tag(name = "Job Categories", description = "Master catalog of local agricultural and informal labor job categories")
public class JobCategoryController {

    private final JobMarketplaceService jobService;

    @GetMapping
    @Operation(summary = "List all active job categories", description = "Fetches a cached list of job categories (Harvesting, Sowing, Construction, etc.)")
    public ResponseEntity<ApiResponse<List<JobCategoryResponse>>> getAllCategories() {
        List<JobCategoryResponse> categories = jobService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.ok("Job categories retrieved successfully", categories));
    }
}
