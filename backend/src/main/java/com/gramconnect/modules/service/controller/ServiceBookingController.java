package com.gramconnect.modules.service.controller;

import com.gramconnect.common.dto.ApiResponse;
import com.gramconnect.common.dto.PageResponse;
import com.gramconnect.common.security.CustomUserDetails;
import com.gramconnect.modules.service.dto.CreateServiceBookingRequest;
import com.gramconnect.modules.service.dto.ServiceBookingResponse;
import com.gramconnect.modules.service.dto.UpdateBookingStatusRequest;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/services/bookings")
@RequiredArgsConstructor
@Tag(name = "Service Bookings Lifecycle", description = "Endpoints for requesting, accepting, managing, and completing service bookings")
public class ServiceBookingController {

    private final ServiceMarketplaceService marketplaceService;

    @PostMapping
    @Operation(summary = "Request a Service Booking", description = "Villager submits a service request to a local provider.")
    public ResponseEntity<ApiResponse<ServiceBookingResponse>> createBooking(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateServiceBookingRequest request) {

        ServiceBookingResponse response = marketplaceService.createBooking(userDetails.getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Service requested successfully", response));
    }

    @GetMapping("/my")
    @Operation(summary = "List My Service Requests (Citizen View)", description = "Fetches bookings requested by the logged-in citizen.")
    public ResponseEntity<ApiResponse<PageResponse<ServiceBookingResponse>>> getMyBookings(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        PageResponse<ServiceBookingResponse> response = marketplaceService.getVillagerBookings(userDetails.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok("Bookings retrieved successfully", response));
    }

    @GetMapping("/provider")
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    @Operation(summary = "List Incoming Service Requests (Provider View)", description = "Fetches booking requests received by the logged-in service provider.")
    public ResponseEntity<ApiResponse<PageResponse<ServiceBookingResponse>>> getProviderBookings(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        PageResponse<ServiceBookingResponse> response = marketplaceService.getProviderBookings(userDetails.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok("Incoming requests retrieved successfully", response));
    }

    @PatchMapping("/{bookingId}/status")
    @Operation(summary = "Update Booking Status", description = "Transitions booking state (ACCEPT, DECLINE, START, COMPLETE, CANCEL).")
    public ResponseEntity<ApiResponse<ServiceBookingResponse>> updateBookingStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID bookingId,
            @Valid @RequestBody UpdateBookingStatusRequest request) {

        ServiceBookingResponse response = marketplaceService.updateBookingStatus(bookingId, userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Booking status updated successfully", response));
    }
}
