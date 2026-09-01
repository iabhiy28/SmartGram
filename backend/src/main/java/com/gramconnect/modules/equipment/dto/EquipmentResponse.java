package com.gramconnect.modules.equipment.dto;

import com.gramconnect.modules.equipment.entity.Equipment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentResponse {

    private UUID id;
    private UUID ownerId;
    private String ownerName;
    private String ownerPhone;
    private UUID villageId;
    private String villageName;
    private UUID categoryId;
    private String categoryName;
    private String title;
    private String description;
    private String make;
    private String model;
    private Integer yearOfPurchase;
    private Integer horsePower;
    private BigDecimal hourlyRate;
    private BigDecimal dailyRate;
    private String photoUrls;
    private Boolean isOperational;
    private Boolean isActive;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer serviceRadiusKm;
    private Instant createdAt;
    private Instant updatedAt;

    public static EquipmentResponse fromEntity(Equipment entity) {
        return EquipmentResponse.builder()
                .id(entity.getId())
                .ownerId(entity.getOwner().getId())
                .ownerName(entity.getOwner().getFullName())
                .ownerPhone(entity.getOwner().getPhoneNumber())
                .villageId(entity.getVillage().getId())
                .villageName(entity.getVillage().getName())
                .categoryId(entity.getCategory().getId())
                .categoryName(entity.getCategory().getDisplayName())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .make(entity.getMake())
                .model(entity.getModel())
                .yearOfPurchase(entity.getYearOfPurchase())
                .horsePower(entity.getHorsePower())
                .hourlyRate(entity.getHourlyRate())
                .dailyRate(entity.getDailyRate())
                .photoUrls(entity.getPhotoUrls())
                .isOperational(entity.getIsOperational())
                .isActive(entity.getIsActive())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .serviceRadiusKm(entity.getServiceRadiusKm())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
