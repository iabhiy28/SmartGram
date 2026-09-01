package com.gramconnect.modules.equipment.dto;

import com.gramconnect.modules.equipment.entity.EquipmentCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentCategoryResponse {

    private UUID id;
    private String name;
    private String displayName;
    private String description;
    private String icon;
    private Integer displayOrder;

    public static EquipmentCategoryResponse fromEntity(EquipmentCategory entity) {
        return EquipmentCategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .displayName(entity.getDisplayName())
                .description(entity.getDescription())
                .icon(entity.getIcon())
                .displayOrder(entity.getDisplayOrder())
                .build();
    }
}
