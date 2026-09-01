package com.gramconnect.modules.equipment.controller;

import com.gramconnect.common.dto.ApiResponse;
import com.gramconnect.common.dto.PageResponse;
import com.gramconnect.common.security.CustomUserDetails;
import com.gramconnect.modules.equipment.dto.*;
import com.gramconnect.modules.equipment.service.EquipmentService;
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
@RequestMapping("/api/v1/equipment")
@RequiredArgsConstructor
@Tag(name = "Equipment Bookings", description = "Concurrency-safe equipment booking management")
public class EquipmentBookingController {

    private final EquipmentService equipmentService;

    @PostMapping("/{equipmentId}/book")
    @Operation(summary = "Book equipment (concurrency-safe with double-booking prevention)")
    public ResponseEntity<ApiResponse<EquipmentBookingResponse>> bookEquipment(
            @PathVariable UUID equipmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateEquipmentBookingRequest request) {
        EquipmentBookingResponse response = equipmentService.bookEquipment(equipmentId, userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Equipment booked successfully. Awaiting owner confirmation."));
    }

    @PatchMapping("/bookings/{bookingId}/status")
    @Operation(summary = "Update booking status (confirm, reject, activate, complete, cancel)")
    public ResponseEntity<ApiResponse<EquipmentBookingResponse>> updateBookingStatus(
            @PathVariable UUID bookingId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateEquipmentBookingStatusRequest request) {
        EquipmentBookingResponse response = equipmentService.updateBookingStatus(bookingId, userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Booking status updated"));
    }

    @GetMapping("/bookings/{bookingId}")
    @Operation(summary = "Get booking details by ID")
    public ResponseEntity<ApiResponse<EquipmentBookingResponse>> getBookingById(
            @PathVariable UUID bookingId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        EquipmentBookingResponse response = equipmentService.getBookingById(bookingId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Booking retrieved"));
    }

    @GetMapping("/bookings/my")
    @Operation(summary = "Get my equipment bookings (as a renter)")
    public ResponseEntity<ApiResponse<PageResponse<EquipmentBookingResponse>>> getMyBookings(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<EquipmentBookingResponse> response = equipmentService.getMyBookings(userDetails.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "My bookings retrieved"));
    }

    @GetMapping("/bookings/owner")
    @Operation(summary = "Get bookings for my equipment (as an owner)")
    public ResponseEntity<ApiResponse<PageResponse<EquipmentBookingResponse>>> getOwnerBookings(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<EquipmentBookingResponse> response = equipmentService.getOwnerBookings(userDetails.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Owner bookings retrieved"));
    }
}
