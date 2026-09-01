package com.gramconnect.modules.service.dto;

import com.gramconnect.modules.service.entity.ServiceCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCategoryResponse {

    private UUID id;
    private String name;
    private String displayName;
    private String description;
    private String icon;
    private Integer displayOrder;

    public static ServiceCategoryResponse fromEntity(ServiceCategory category) {
        return ServiceCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .displayName(category.getDisplayName())
                .description(category.getDescription())
                .icon(category.getIcon())
                .displayOrder(category.getDisplayOrder())
                .build();
    }
}
