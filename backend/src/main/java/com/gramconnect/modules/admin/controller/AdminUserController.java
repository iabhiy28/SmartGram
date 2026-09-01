package com.gramconnect.modules.admin.controller;

import com.gramconnect.common.dto.ApiResponse;
import com.gramconnect.common.dto.PageResponse;
import com.gramconnect.common.exception.ResourceNotFoundException;
import com.gramconnect.modules.user.dto.UserProfileResponse;
import com.gramconnect.modules.user.entity.Role;
import com.gramconnect.modules.user.entity.User;
import com.gramconnect.modules.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PANCHAYAT_ADMIN', 'SUPER_ADMIN')")
@Tag(name = "Admin User Management", description = "Panchayat & Super Admin user administration and verification endpoints")
public class AdminUserController {

    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "List users by village and role", description = "Allows Panchayat administrators to browse and filter registered users.")
    public ResponseEntity<ApiResponse<PageResponse<UserProfileResponse>>> getUsers(
            @RequestParam(required = false) UUID villageId,
            @RequestParam(required = false) Role role,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<User> page;
        if (villageId != null && role != null) {
            page = userRepository.findByVillageIdAndRole(villageId, role, pageable);
        } else if (villageId != null) {
            page = userRepository.findActiveUsersByVillage(villageId, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }

        Page<UserProfileResponse> dtoPage = page.map(UserProfileResponse::fromEntity);
        return ResponseEntity.ok(ApiResponse.ok("Users retrieved successfully", PageResponse.from(dtoPage)));
    }

    @PatchMapping("/{userId}/status")
    @Operation(summary = "Activate or Deactivate User Account", description = "Toggles user active state (e.g. suspending abusive accounts).")
    public ResponseEntity<ApiResponse<UserProfileResponse>> toggleUserStatus(
            @PathVariable UUID userId,
            @RequestParam boolean active) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setIsActive(active);
        User savedUser = userRepository.save(user);
        log.info("Admin updated user [ID: {}] active status to: {}", userId, active);

        return ResponseEntity.ok(ApiResponse.ok("User status updated successfully", UserProfileResponse.fromEntity(savedUser)));
    }

    @PatchMapping("/{userId}/verify")
    @Operation(summary = "Verify User Profile", description = "Marks a user as officially verified by the Gram Panchayat.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> verifyUser(
            @PathVariable UUID userId,
            @RequestParam boolean verified) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setIsVerified(verified);
        User savedUser = userRepository.save(user);
        log.info("Admin updated user [ID: {}] verification status to: {}", userId, verified);

        return ResponseEntity.ok(ApiResponse.ok("User verification updated successfully", UserProfileResponse.fromEntity(savedUser)));
    }
}
