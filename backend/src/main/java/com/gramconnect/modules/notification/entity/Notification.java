package com.gramconnect.modules.notification.entity;

import com.gramconnect.common.entity.BaseAuditableEntity;
import com.gramconnect.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Notification Entity mapping `notifications`.
 * In-app notification for a specific user.
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "notification_type", nullable = false, length = 30)
    private String notificationType; // BOOKING, COMPLAINT, JOB, SCHEME, ANNOUNCEMENT, SYSTEM

    @Column(name = "reference_type", length = 50)
    private String referenceType; // e.g. "EQUIPMENT_BOOKING", "COMPLAINT", "JOB_APPLICATION"

    @Column(name = "reference_id")
    private java.util.UUID referenceId;

    @Builder.Default
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "read_at")
    private Instant readAt;
}
