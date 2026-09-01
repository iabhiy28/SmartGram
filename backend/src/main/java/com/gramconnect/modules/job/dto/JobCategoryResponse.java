package com.gramconnect.modules.job.dto;

import com.gramconnect.modules.job.entity.JobCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobCategoryResponse {

    private UUID id;
    private String name;
    private String displayName;
    private String description;
    private String icon;
    private Integer displayOrder;

    public static JobCategoryResponse fromEntity(JobCategory category) {
        return JobCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .displayName(category.getDisplayName())
                .description(category.getDescription())
                .icon(category.getIcon())
                .displayOrder(category.getDisplayOrder())
                .build();
    }
}
