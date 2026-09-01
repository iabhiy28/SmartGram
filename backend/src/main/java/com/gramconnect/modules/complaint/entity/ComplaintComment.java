package com.gramconnect.modules.complaint.entity;

import com.gramconnect.common.entity.BaseAuditableEntity;
import com.gramconnect.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * ComplaintComment Entity mapping `complaint_comments`.
 * Progress notes and admin responses on a complaint.
 */
@Entity
@Table(name = "complaint_comments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintComment extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Column(name = "is_internal", nullable = false)
    private Boolean isInternal = false; // Admin-only internal notes
}
