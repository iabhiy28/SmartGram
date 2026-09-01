package com.gramconnect.modules.hierarchy.controller;

import com.gramconnect.common.dto.ApiResponse;
import com.gramconnect.modules.hierarchy.dto.DistrictResponse;
import com.gramconnect.modules.hierarchy.dto.PanchayatResponse;
import com.gramconnect.modules.hierarchy.dto.StateResponse;
import com.gramconnect.modules.hierarchy.dto.VillageResponse;
import com.gramconnect.modules.hierarchy.service.HierarchyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hierarchy")
@RequiredArgsConstructor
@Tag(name = "Administrative Hierarchy", description = "Cascading location lookup endpoints for States, Districts, Panchayats, and Villages")
public class HierarchyController {

    private final HierarchyService hierarchyService;

    @GetMapping("/states")
    @Operation(summary = "List all states", description = "Fetches a sorted list of all active states and union territories in India.")
    public ResponseEntity<ApiResponse<List<StateResponse>>> getAllStates() {
        List<StateResponse> states = hierarchyService.getAllStates();
        return ResponseEntity.ok(ApiResponse.ok("States retrieved successfully", states));
    }

    @GetMapping("/districts/{stateId}")
    @Operation(summary = "List districts in a state", description = "Fetches all districts for a given state UUID.")
    public ResponseEntity<ApiResponse<List<DistrictResponse>>> getDistrictsByState(@PathVariable UUID stateId) {
        List<DistrictResponse> districts = hierarchyService.getDistrictsByState(stateId);
        return ResponseEntity.ok(ApiResponse.ok("Districts retrieved successfully", districts));
    }

    @GetMapping("/panchayats/{districtId}")
    @Operation(summary = "List Panchayats in a district", description = "Fetches all Gram Panchayats for a given district UUID.")
    public ResponseEntity<ApiResponse<List<PanchayatResponse>>> getPanchayatsByDistrict(@PathVariable UUID districtId) {
        List<PanchayatResponse> panchayats = hierarchyService.getPanchayatsByDistrict(districtId);
        return ResponseEntity.ok(ApiResponse.ok("Panchayats retrieved successfully", panchayats));
    }

    @GetMapping("/villages/{panchayatId}")
    @Operation(summary = "List villages in a Panchayat", description = "Fetches all villages under a specific Gram Panchayat.")
    public ResponseEntity<ApiResponse<List<VillageResponse>>> getVillagesByPanchayat(@PathVariable UUID panchayatId) {
        List<VillageResponse> villages = hierarchyService.getVillagesByPanchayat(panchayatId);
        return ResponseEntity.ok(ApiResponse.ok("Villages retrieved successfully", villages));
    }

    @GetMapping("/villages/details/{villageId}")
    @Operation(summary = "Get village details", description = "Fetches full metadata including coordinates and PIN code for a single village.")
    public ResponseEntity<ApiResponse<VillageResponse>> getVillageById(@PathVariable UUID villageId) {
        VillageResponse village = hierarchyService.getVillageById(villageId);
        return ResponseEntity.ok(ApiResponse.ok("Village details retrieved successfully", village));
    }

    @GetMapping("/villages/search")
    @Operation(summary = "Search villages by PIN code", description = "Quick search for villages matching an Indian postal PIN code.")
    public ResponseEntity<ApiResponse<List<VillageResponse>>> searchVillagesByPinCode(
            @RequestParam String pincode) {

        List<VillageResponse> villages = hierarchyService.searchVillagesByPinCode(pincode);
        return ResponseEntity.ok(ApiResponse.ok("Villages found", villages));
    }

    @GetMapping("/villages/nearby")
    @Operation(summary = "Discover nearby villages by coordinates", description = "Calculates Haversine distance and returns all villages within a given radius in kilometers.")
    public ResponseEntity<ApiResponse<List<VillageResponse>>> getNearbyVillages(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(defaultValue = "15.0") double radiusKm,
            @RequestParam(defaultValue = "20") int limit) {

        List<VillageResponse> villages = hierarchyService.getNearbyVillages(lat, lng, radiusKm, limit);
        return ResponseEntity.ok(ApiResponse.ok("Nearby villages retrieved successfully", villages));
    }
}
