package com.gramconnect.modules.equipment.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEquipmentRequest {

    @NotBlank(message = "Equipment title is required")
    @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
    private String title;

    @NotNull(message = "Equipment category is required")
    private UUID categoryId;

    @NotNull(message = "Village is required")
    private UUID villageId;

    private String description;

    @Size(max = 100, message = "Make must not exceed 100 characters")
    private String make;

    @Size(max = 100, message = "Model must not exceed 100 characters")
    private String model;

    @Min(value = 1950, message = "Year of purchase must be a valid year")
    private Integer yearOfPurchase;

    @Min(value = 1, message = "Horse power must be at least 1")
    private Integer horsePower;

    @DecimalMin(value = "0.01", message = "Hourly rate must be positive")
    private BigDecimal hourlyRate;

    @DecimalMin(value = "0.01", message = "Daily rate must be positive")
    private BigDecimal dailyRate;

    private List<String> photoUrls;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @Min(value = 1, message = "Service radius must be at least 1 km")
    @Max(value = 100, message = "Service radius cannot exceed 100 km")
    private Integer serviceRadiusKm;
}
