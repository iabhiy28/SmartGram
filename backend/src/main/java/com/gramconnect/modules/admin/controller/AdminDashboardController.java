package com.gramconnect.modules.admin.controller;

import com.gramconnect.common.dto.ApiResponse;
import com.gramconnect.common.security.CustomUserDetails;
import com.gramconnect.modules.admin.dto.AdminDashboardStatsResponse;
import com.gramconnect.modules.admin.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "Admin Dashboard", description = "Panchayat & Super Admin Metrics Command Center")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('PANCHAYAT_ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get aggregated village KPI metrics for admin dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardStatsResponse>> getDashboardStats(
            @RequestParam(required = false) UUID villageId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        UUID targetVillageId = villageId != null ? villageId : userDetails.getVillageId();
        AdminDashboardStatsResponse stats = dashboardService.getPanchayatDashboardStats(targetVillageId);
        return ResponseEntity.ok(ApiResponse.success(stats, "Admin dashboard statistics retrieved successfully"));
    }
}
