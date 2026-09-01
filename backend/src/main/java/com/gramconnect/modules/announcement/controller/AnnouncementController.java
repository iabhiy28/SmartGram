package com.gramconnect.modules.announcement.controller;

import com.gramconnect.common.dto.ApiResponse;
import com.gramconnect.common.dto.PageResponse;
import com.gramconnect.common.security.CustomUserDetails;
import com.gramconnect.modules.announcement.dto.AnnouncementResponse;
import com.gramconnect.modules.announcement.dto.CreateAnnouncementRequest;
import com.gramconnect.modules.announcement.service.AnnouncementService;
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
@RequestMapping("/api/v1/announcements")
@RequiredArgsConstructor
@Tag(name = "Announcements", description = "Village announcements and broadcasts")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    @Operation(summary = "Create a new announcement (Admin)")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> createAnnouncement(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateAnnouncementRequest request) {
        AnnouncementResponse response = announcementService.createAnnouncement(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Announcement created"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get announcement by ID")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> getAnnouncementById(@PathVariable UUID id) {
        AnnouncementResponse response = announcementService.getAnnouncementById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Announcement retrieved"));
    }

    @GetMapping("/village/{villageId}")
    @Operation(summary = "Get active announcements for a village")
    public ResponseEntity<ApiResponse<PageResponse<AnnouncementResponse>>> getVillageAnnouncements(
            @PathVariable UUID villageId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<AnnouncementResponse> response = announcementService.getVillageAnnouncements(villageId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Village announcements retrieved"));
    }

    @GetMapping("/my")
    @Operation(summary = "Get my posted announcements")
    public ResponseEntity<ApiResponse<PageResponse<AnnouncementResponse>>> getMyAnnouncements(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<AnnouncementResponse> response = announcementService.getMyAnnouncements(userDetails.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "My announcements retrieved"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate an announcement")
    public ResponseEntity<ApiResponse<Void>> deactivateAnnouncement(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        announcementService.deactivateAnnouncement(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Announcement deactivated"));
    }

    @PostMapping("/{id}/view")
    @Operation(summary = "Increment view count")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> incrementViewCount(@PathVariable UUID id) {
        AnnouncementResponse response = announcementService.incrementViewCount(id);
        return ResponseEntity.ok(ApiResponse.success(response, "View counted"));
    }
}
