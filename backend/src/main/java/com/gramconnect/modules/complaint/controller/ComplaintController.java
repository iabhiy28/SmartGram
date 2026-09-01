package com.gramconnect.modules.complaint.controller;

import com.gramconnect.common.dto.ApiResponse;
import com.gramconnect.common.dto.PageResponse;
import com.gramconnect.common.security.CustomUserDetails;
import com.gramconnect.modules.complaint.dto.*;
import com.gramconnect.modules.complaint.entity.ComplaintStatus;
import com.gramconnect.modules.complaint.service.ComplaintService;
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
@RequestMapping("/api/v1/complaints")
@RequiredArgsConstructor
@Tag(name = "Complaints", description = "Civic complaint management with SLA tracking")
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    @Operation(summary = "File a new complaint")
    public ResponseEntity<ApiResponse<ComplaintResponse>> fileComplaint(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateComplaintRequest request) {
        ComplaintResponse response = complaintService.fileComplaint(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Complaint filed successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get complaint by ID")
    public ResponseEntity<ApiResponse<ComplaintResponse>> getComplaintById(@PathVariable UUID id) {
        ComplaintResponse response = complaintService.getComplaintById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Complaint retrieved"));
    }

    @GetMapping("/my")
    @Operation(summary = "Get my filed complaints")
    public ResponseEntity<ApiResponse<PageResponse<ComplaintResponse>>> getMyComplaints(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<ComplaintResponse> response = complaintService.getMyComplaints(userDetails.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "My complaints retrieved"));
    }

    @GetMapping
    @Operation(summary = "Search complaints with filters")
    public ResponseEntity<ApiResponse<PageResponse<ComplaintResponse>>> searchComplaints(
            @RequestParam(required = false) UUID villageId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(required = false) String priority,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<ComplaintResponse> response = complaintService.searchComplaints(villageId, categoryId, status, priority, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Complaints search results"));
    }

    @GetMapping("/village/{villageId}")
    @Operation(summary = "Get all complaints for a village")
    public ResponseEntity<ApiResponse<PageResponse<ComplaintResponse>>> getVillageComplaints(
            @PathVariable UUID villageId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<ComplaintResponse> response = complaintService.getVillageComplaints(villageId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Village complaints retrieved"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update complaint status (Admin)")
    public ResponseEntity<ApiResponse<ComplaintResponse>> updateComplaintStatus(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateComplaintStatusRequest request) {
        ComplaintResponse response = complaintService.updateComplaintStatus(id, userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Complaint status updated"));
    }

    @PostMapping("/{id}/upvote")
    @Operation(summary = "Upvote a complaint")
    public ResponseEntity<ApiResponse<ComplaintResponse>> upvoteComplaint(@PathVariable UUID id) {
        ComplaintResponse response = complaintService.upvoteComplaint(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Complaint upvoted"));
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Add a comment to a complaint")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateCommentRequest request) {
        CommentResponse response = complaintService.addComment(id, userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Comment added"));
    }

    @GetMapping("/{id}/comments")
    @Operation(summary = "Get comments for a complaint")
    public ResponseEntity<ApiResponse<PageResponse<CommentResponse>>> getComments(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean includeInternal,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<CommentResponse> response = complaintService.getComments(id, includeInternal, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Comments retrieved"));
    }
}
