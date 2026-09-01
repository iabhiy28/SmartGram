package com.gramconnect.modules.hierarchy.dto;

import com.gramconnect.modules.hierarchy.entity.Village;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VillageResponse {

    private UUID id;
    private UUID panchayatId;
    private String name;
    private String pinCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer population;

    public static VillageResponse fromEntity(Village village) {
        return VillageResponse.builder()
                .id(village.getId())
                .panchayatId(village.getPanchayat().getId())
                .name(village.getName())
                .pinCode(village.getPinCode())
                .latitude(village.getLatitude())
                .longitude(village.getLongitude())
                .population(village.getPopulation())
                .build();
    }
}
