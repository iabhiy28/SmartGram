package com.gramconnect.modules.announcement.controller;

import com.gramconnect.common.dto.ApiResponse;
import com.gramconnect.modules.announcement.dto.EmergencyContactResponse;
import com.gramconnect.modules.announcement.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/emergency-contacts")
@RequiredArgsConstructor
@Tag(name = "Emergency Contacts", description = "Village and district-level emergency contacts")
public class EmergencyContactController {

    private final AnnouncementService announcementService;

    @GetMapping
    @Operation(summary = "Get emergency contacts (optionally filtered by village)")
    public ResponseEntity<ApiResponse<List<EmergencyContactResponse>>> getEmergencyContacts(
            @RequestParam(required = false) UUID villageId) {
        List<EmergencyContactResponse> contacts = announcementService.getEmergencyContacts(villageId);
        return ResponseEntity.ok(ApiResponse.success(contacts, "Emergency contacts retrieved"));
    }
}
