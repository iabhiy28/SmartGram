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
@Tag(name = "Equipment", description = "Agricultural equipment listing and management")
public class EquipmentController {

    private final EquipmentService equipmentService;

    @PostMapping
    @Operation(summary = "List new equipment for rental")
    public ResponseEntity<ApiResponse<EquipmentResponse>> createEquipment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateEquipmentRequest request) {
        EquipmentResponse response = equipmentService.createEquipment(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Equipment listed successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update equipment listing")
    public ResponseEntity<ApiResponse<EquipmentResponse>> updateEquipment(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateEquipmentRequest request) {
        EquipmentResponse response = equipmentService.updateEquipment(id, userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Equipment updated successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get equipment by ID")
    public ResponseEntity<ApiResponse<EquipmentResponse>> getEquipmentById(@PathVariable UUID id) {
        EquipmentResponse response = equipmentService.getEquipmentById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Equipment retrieved"));
    }

    @GetMapping
    @Operation(summary = "Search available equipment")
    public ResponseEntity<ApiResponse<PageResponse<EquipmentResponse>>> searchEquipment(
            @RequestParam(required = false) UUID villageId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) Boolean isOperational,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<EquipmentResponse> response = equipmentService.searchEquipment(villageId, categoryId, isOperational, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Equipment search results"));
    }

    @GetMapping("/my-listings")
    @Operation(summary = "Get my equipment listings")
    public ResponseEntity<ApiResponse<PageResponse<EquipmentResponse>>> getMyListings(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<EquipmentResponse> response = equipmentService.getMyListings(userDetails.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "My equipment listings retrieved"));
    }
}
