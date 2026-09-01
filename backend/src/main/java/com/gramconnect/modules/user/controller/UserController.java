package com.gramconnect.modules.user.controller;

import com.gramconnect.common.dto.ApiResponse;
import com.gramconnect.common.security.CustomUserDetails;
import com.gramconnect.modules.user.dto.UpdateProfileRequest;
import com.gramconnect.modules.user.dto.UserProfileResponse;
import com.gramconnect.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User profile retrieval and demographic updates")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile", description = "Fetches the full demographic and contact profile of the currently logged-in user.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        UserProfileResponse response = userService.getProfile(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("User profile retrieved successfully", response));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile", description = "Updates non-credential profile details such as name, village, demographics, and language preferences.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {

        UserProfileResponse response = userService.updateProfile(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("User profile updated successfully", response));
    }
}
