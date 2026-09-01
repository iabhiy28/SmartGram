package com.gramconnect.modules.complaint.dto;

import com.gramconnect.modules.complaint.entity.Complaint;
import com.gramconnect.modules.complaint.entity.ComplaintStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintResponse {

    private UUID id;
    private UUID complainantId;
    private String complainantName;
    private UUID villageId;
    private String villageName;
    private UUID categoryId;
    private String categoryName;
    private String title;
    private String description;
    private String locationDescription;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private ComplaintStatus status;
    private String priority;
    private Instant slaDeadline;
    private Boolean isSlaBreached;
    private UUID assignedToId;
    private String assignedToName;
    private String resolutionNotes;
    private Integer upvoteCount;
    private Instant acknowledgedAt;
    private Instant resolvedAt;
    private Instant escalatedAt;
    private Instant createdAt;
    private Instant updatedAt;

    public static ComplaintResponse fromEntity(Complaint entity) {
        ComplaintResponse.ComplaintResponseBuilder builder = ComplaintResponse.builder()
                .id(entity.getId())
                .complainantId(entity.getComplainant().getId())
                .complainantName(entity.getComplainant().getFullName())
                .villageId(entity.getVillage().getId())
                .villageName(entity.getVillage().getName())
                .categoryId(entity.getCategory().getId())
                .categoryName(entity.getCategory().getDisplayName())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .locationDescription(entity.getLocationDescription())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .status(entity.getStatus())
                .priority(entity.getPriority())
                .slaDeadline(entity.getSlaDeadline())
                .isSlaBreached(entity.getIsSlaBreached())
                .resolutionNotes(entity.getResolutionNotes())
                .upvoteCount(entity.getUpvoteCount())
                .acknowledgedAt(entity.getAcknowledgedAt())
                .resolvedAt(entity.getResolvedAt())
                .escalatedAt(entity.getEscalatedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt());

        if (entity.getAssignedTo() != null) {
            builder.assignedToId(entity.getAssignedTo().getId())
                   .assignedToName(entity.getAssignedTo().getFullName());
        }

        return builder.build();
    }
}
