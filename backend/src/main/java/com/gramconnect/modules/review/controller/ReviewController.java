package com.gramconnect.modules.review.controller;

import com.gramconnect.common.dto.ApiResponse;
import com.gramconnect.common.dto.PageResponse;
import com.gramconnect.common.security.CustomUserDetails;
import com.gramconnect.modules.review.dto.CreateReviewRequest;
import com.gramconnect.modules.review.dto.ReviewResponse;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
@Tag(name = "Verified Reviews & Ratings", description = "Endpoints for submitting verified post-service ratings and querying provider feedback")
public class ReviewController {

    private final ServiceMarketplaceService marketplaceService;

    @PostMapping("/bookings/{bookingId}/reviews")
    @Operation(summary = "Submit Verified Review", description = "Submits a verified review for a COMPLETED service booking and transactionally recalculates provider average rating.")
    public ResponseEntity<ApiResponse<ReviewResponse>> submitReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID bookingId,
            @Valid @RequestBody CreateReviewRequest request) {

        ReviewResponse response = marketplaceService.submitReview(bookingId, userDetails.getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Review submitted successfully. Thank you for rating!", response));
    }

    @GetMapping("/providers/{providerId}/reviews")
    @Operation(summary = "List Provider Reviews", description = "Public paginated list of verified customer reviews for a service provider.")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getProviderReviews(
            @PathVariable UUID providerId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        PageResponse<ReviewResponse> response = marketplaceService.getProviderReviews(providerId, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Reviews retrieved successfully", response));
    }
}
