package com.gramconnect.modules.announcement.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnnouncementRequest {

    private UUID villageId; // null = platform-wide

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 10, max = 10000, message = "Content must be between 10 and 10000 characters")
    private String content;

    @Pattern(regexp = "^(GENERAL|EMERGENCY|EVENT|MEETING)$", message = "Type must be GENERAL, EMERGENCY, EVENT, or MEETING")
    private String announcementType;

    @Pattern(regexp = "^(LOW|NORMAL|HIGH|URGENT)$", message = "Priority must be LOW, NORMAL, HIGH, or URGENT")
    private String priority;

    private String attachmentUrl;

    private Instant expiresAt;
}
