package com.gramconnect.modules.announcement.entity;

import com.gramconnect.common.entity.BaseAuditableEntity;
import com.gramconnect.modules.hierarchy.entity.Village;
import com.gramconnect.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Announcement Entity mapping `announcements`.
 * Village-level broadcasts from Panchayat Admins or Super Admins.
 */
@Entity
@Table(name = "announcements")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Announcement extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "village_id")
    private Village village; // null = platform-wide announcement

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Column(name = "announcement_type", nullable = false, length = 30)
    private String announcementType = "GENERAL"; // GENERAL, EMERGENCY, EVENT, MEETING

    @Builder.Default
    @Column(name = "priority", nullable = false, length = 10)
    private String priority = "NORMAL"; // LOW, NORMAL, HIGH, URGENT

    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;
}
