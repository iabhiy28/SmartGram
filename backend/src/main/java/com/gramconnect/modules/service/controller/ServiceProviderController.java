package com.gramconnect.modules.service.controller;

import com.gramconnect.common.dto.ApiResponse;
import com.gramconnect.common.dto.PageResponse;
import com.gramconnect.common.security.CustomUserDetails;
import com.gramconnect.modules.service.dto.*;
import com.gramconnect.modules.service.service.ServiceMarketplaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/services/providers")
@RequiredArgsConstructor
@Tag(name = "Service Providers Directory", description = "Service provider profiles, offerings, public search, and bookmarks")
public class ServiceProviderController {

    private final ServiceMarketplaceService marketplaceService;

    @GetMapping
    @Operation(summary = "Search & Filter Service Providers", description = "Public search for verified local service providers by village, category, rating, and availability.")
    public ResponseEntity<ApiResponse<PageResponse<ServiceProviderResponse>>> searchProviders(
            @RequestParam(required = false) UUID villageId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) Boolean isAvailable,
            @PageableDefault(size = 20, sort = "averageRating", direction = Sort.Direction.DESC) Pageable pageable) {

        PageResponse<ServiceProviderResponse> response = marketplaceService.searchProviders(
                villageId, categoryId, minRating, isAvailable, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Providers retrieved successfully", response));
    }

    @GetMapping("/{providerId}")
    @Operation(summary = "Get Service Provider Profile by ID", description = "Fetches complete provider details including active offerings and verified badges.")
    public ResponseEntity<ApiResponse<ServiceProviderResponse>> getProviderById(@PathVariable UUID providerId) {
        ServiceProviderResponse response = marketplaceService.getProviderById(providerId);
        return ResponseEntity.ok(ApiResponse.ok("Provider profile retrieved successfully", response));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    @Operation(summary = "Get current logged-in provider profile", description = "Fetches the provider profile for the authenticated user.")
    public ResponseEntity<ApiResponse<ServiceProviderResponse>> getMyProviderProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ServiceProviderResponse response = marketplaceService.getProviderByUserId(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("My provider profile retrieved successfully", response));
    }

    @PostMapping("/profile")
    @Operation(summary = "Create Service Provider Profile", description = "Onboards a user as a service provider with operating radius and ID proof.")
    public ResponseEntity<ApiResponse<ServiceProviderResponse>> createProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateProviderProfileRequest request) {

        ServiceProviderResponse response = marketplaceService.createProviderProfile(userDetails.getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Provider profile created successfully. Pending Panchayat verification.", response));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    @Operation(summary = "Update Service Provider Profile", description = "Updates availability, radius, and bio.")
    public ResponseEntity<ApiResponse<ServiceProviderResponse>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateProviderProfileRequest request) {

        ServiceProviderResponse response = marketplaceService.updateProviderProfile(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Provider profile updated successfully", response));
    }

    @PostMapping("/offerings")
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    @Operation(summary = "Add a Service Offering", description = "Adds a category offering with rate card (e.g. Electrician - ₹250/hour).")
    public ResponseEntity<ApiResponse<ServiceOfferingResponse>> addOffering(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateOfferingRequest request) {

        ServiceOfferingResponse response = marketplaceService.addOffering(userDetails.getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Offering added successfully", response));
    }

    @GetMapping("/{providerId}/offerings")
    @Operation(summary = "List offerings for a provider", description = "Fetches all active services and rate cards provided by a specific provider.")
    public ResponseEntity<ApiResponse<List<ServiceOfferingResponse>>> getOfferingsByProvider(@PathVariable UUID providerId) {
        List<ServiceOfferingResponse> offerings = marketplaceService.getOfferingsByProvider(providerId);
        return ResponseEntity.ok(ApiResponse.ok("Offerings retrieved successfully", offerings));
    }

    @PostMapping("/{providerId}/bookmark")
    @Operation(summary = "Toggle Bookmark / Favorite Provider", description = "Saves or removes a provider from the user's bookmarked directory.")
    public ResponseEntity<ApiResponse<Void>> toggleBookmark(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID providerId) {

        marketplaceService.toggleFavorite(userDetails.getId(), providerId);
        return ResponseEntity.ok(ApiResponse.ok("Bookmark updated successfully", null));
    }

    @GetMapping("/bookmarks")
    @Operation(summary = "List bookmarked service providers", description = "Fetches the authenticated user's saved favorites.")
    public ResponseEntity<ApiResponse<PageResponse<ServiceProviderResponse>>> getBookmarkedProviders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {

        PageResponse<ServiceProviderResponse> response = marketplaceService.getSavedProviders(userDetails.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok("Bookmarks retrieved successfully", response));
    }
}
