package com.gramconnect.modules.complaint.controller;

import com.gramconnect.common.dto.ApiResponse;
import com.gramconnect.modules.complaint.dto.ComplaintCategoryResponse;
import com.gramconnect.modules.complaint.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/complaints/categories")
@RequiredArgsConstructor
@Tag(name = "Complaint Categories", description = "Master complaint category endpoints")
public class ComplaintCategoryController {

    private final ComplaintService complaintService;

    @GetMapping
    @Operation(summary = "Get all active complaint categories")
    public ResponseEntity<ApiResponse<List<ComplaintCategoryResponse>>> getAllCategories() {
        List<ComplaintCategoryResponse> categories = complaintService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories, "Complaint categories retrieved"));
    }
}
