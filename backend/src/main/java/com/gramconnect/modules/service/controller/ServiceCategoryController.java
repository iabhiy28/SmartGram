package com.gramconnect.modules.service.controller;

import com.gramconnect.common.dto.ApiResponse;
import com.gramconnect.modules.service.dto.ServiceCategoryResponse;
import com.gramconnect.modules.service.service.ServiceMarketplaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services/categories")
@RequiredArgsConstructor
@Tag(name = "Service Categories", description = "Public catalog of active service categories (Electrician, Plumber, Mechanic, etc.)")
public class ServiceCategoryController {

    private final ServiceMarketplaceService marketplaceService;

    @GetMapping
    @Operation(summary = "List all active service categories", description = "Fetches a cached list of master service categories.")
    public ResponseEntity<ApiResponse<List<ServiceCategoryResponse>>> getAllCategories() {
        List<ServiceCategoryResponse> categories = marketplaceService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.ok("Categories retrieved successfully", categories));
    }
}
