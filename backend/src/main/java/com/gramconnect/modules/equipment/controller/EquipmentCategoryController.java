package com.gramconnect.modules.equipment.controller;

import com.gramconnect.common.dto.ApiResponse;
import com.gramconnect.modules.equipment.dto.EquipmentCategoryResponse;
import com.gramconnect.modules.equipment.service.EquipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/equipment/categories")
@RequiredArgsConstructor
@Tag(name = "Equipment Categories", description = "Master equipment category endpoints")
public class EquipmentCategoryController {

    private final EquipmentService equipmentService;

    @GetMapping
    @Operation(summary = "Get all active equipment categories")
    public ResponseEntity<ApiResponse<List<EquipmentCategoryResponse>>> getAllCategories() {
        List<EquipmentCategoryResponse> categories = equipmentService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories, "Equipment categories retrieved"));
    }
}
