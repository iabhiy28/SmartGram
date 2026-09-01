package com.gramconnect.modules.complaint.dto;

import com.gramconnect.modules.complaint.entity.ComplaintComment;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private UUID id;
    private UUID authorId;
    private String authorName;
    private String content;
    private Boolean isInternal;
    private Instant createdAt;

    public static CommentResponse fromEntity(ComplaintComment entity) {
        return CommentResponse.builder()
                .id(entity.getId())
                .authorId(entity.getAuthor().getId())
                .authorName(entity.getAuthor().getFullName())
                .content(entity.getContent())
                .isInternal(entity.getIsInternal())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
