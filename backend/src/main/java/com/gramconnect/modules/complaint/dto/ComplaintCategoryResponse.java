package com.gramconnect.modules.complaint.dto;

import com.gramconnect.modules.complaint.entity.ComplaintCategory;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintCategoryResponse {
    private UUID id;
    private String name;
    private String displayName;
    private String description;
    private String icon;
    private Integer defaultSlaHours;
    private Integer displayOrder;

    public static ComplaintCategoryResponse fromEntity(ComplaintCategory entity) {
        return ComplaintCategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .displayName(entity.getDisplayName())
                .description(entity.getDescription())
                .icon(entity.getIcon())
                .defaultSlaHours(entity.getDefaultSlaHours())
                .displayOrder(entity.getDisplayOrder())
                .build();
    }
}
