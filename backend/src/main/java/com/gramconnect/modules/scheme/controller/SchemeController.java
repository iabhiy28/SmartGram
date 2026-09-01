package com.gramconnect.modules.scheme.controller;

import com.gramconnect.common.dto.ApiResponse;
import com.gramconnect.common.dto.PageResponse;
import com.gramconnect.common.security.CustomUserDetails;
import com.gramconnect.modules.scheme.dto.EligibilityCheckRequest;
import com.gramconnect.modules.scheme.dto.SchemeResponse;
import com.gramconnect.modules.scheme.service.SchemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/schemes")
@RequiredArgsConstructor
@Tag(name = "Government Schemes", description = "Scheme discovery and eligibility screening")
public class SchemeController {

    private final SchemeService schemeService;

    @GetMapping
    @Operation(summary = "Search government schemes")
    public ResponseEntity<ApiResponse<PageResponse<SchemeResponse>>> searchSchemes(
            @RequestParam(required = false) String schemeType,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        PageResponse<SchemeResponse> response = schemeService.searchSchemes(schemeType, department, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Schemes retrieved"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get scheme by ID with eligibility rules")
    public ResponseEntity<ApiResponse<SchemeResponse>> getSchemeById(@PathVariable UUID id) {
        SchemeResponse response = schemeService.getSchemeById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Scheme retrieved"));
    }

    @PostMapping("/{id}/check-eligibility")
    @Operation(summary = "Check eligibility for a specific scheme")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkEligibility(
            @PathVariable UUID id,
            @Valid @RequestBody EligibilityCheckRequest request) {
        boolean eligible = schemeService.checkEligibility(id, request);
        Map<String, Object> result = Map.of(
                "schemeId", id,
                "eligible", eligible
        );
        return ResponseEntity.ok(ApiResponse.success(result, eligible ? "You are eligible for this scheme!" : "You are not eligible for this scheme."));
    }

    @PostMapping("/discover")
    @Operation(summary = "Discover all schemes you are eligible for")
    public ResponseEntity<ApiResponse<List<SchemeResponse>>> discoverEligibleSchemes(
            @Valid @RequestBody EligibilityCheckRequest request,
            @PageableDefault(size = 100) Pageable pageable) {
        List<SchemeResponse> eligible = schemeService.discoverEligibleSchemes(request, pageable);
        return ResponseEntity.ok(ApiResponse.success(eligible,
                String.format("Found %d scheme(s) you may be eligible for", eligible.size())));
    }

    @PostMapping("/{id}/save")
    @Operation(summary = "Save/bookmark a scheme")
    public ResponseEntity<ApiResponse<Void>> saveScheme(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        schemeService.saveScheme(userDetails.getId(), id);
        return ResponseEntity.ok(ApiResponse.success(null, "Scheme saved"));
    }

    @DeleteMapping("/{id}/save")
    @Operation(summary = "Remove saved scheme")
    public ResponseEntity<ApiResponse<Void>> unsaveScheme(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        schemeService.unsaveScheme(userDetails.getId(), id);
        return ResponseEntity.ok(ApiResponse.success(null, "Scheme unsaved"));
    }

    @GetMapping("/saved")
    @Operation(summary = "Get my saved schemes")
    public ResponseEntity<ApiResponse<PageResponse<SchemeResponse>>> getSavedSchemes(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<SchemeResponse> response = schemeService.getSavedSchemes(userDetails.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Saved schemes retrieved"));
    }
}
