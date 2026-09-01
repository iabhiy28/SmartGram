package com.gramconnect.modules.announcement.dto;

import com.gramconnect.modules.announcement.entity.Announcement;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementResponse {
    private UUID id;
    private UUID authorId;
    private String authorName;
    private UUID villageId;
    private String villageName;
    private String title;
    private String content;
    private String announcementType;
    private String priority;
    private String attachmentUrl;
    private Boolean isActive;
    private Instant expiresAt;
    private Integer viewCount;
    private Instant createdAt;

    public static AnnouncementResponse fromEntity(Announcement entity) {
        AnnouncementResponseBuilder builder = AnnouncementResponse.builder()
                .id(entity.getId())
                .authorId(entity.getAuthor().getId())
                .authorName(entity.getAuthor().getFullName())
                .title(entity.getTitle())
                .content(entity.getContent())
                .announcementType(entity.getAnnouncementType())
                .priority(entity.getPriority())
                .attachmentUrl(entity.getAttachmentUrl())
                .isActive(entity.getIsActive())
                .expiresAt(entity.getExpiresAt())
                .viewCount(entity.getViewCount())
                .createdAt(entity.getCreatedAt());

        if (entity.getVillage() != null) {
            builder.villageId(entity.getVillage().getId())
                   .villageName(entity.getVillage().getName());
        }
        return builder.build();
    }
}
