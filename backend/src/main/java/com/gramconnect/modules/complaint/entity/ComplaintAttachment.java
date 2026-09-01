package com.gramconnect.modules.complaint.entity;

import com.gramconnect.common.entity.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * ComplaintAttachment Entity mapping `complaint_attachments`.
 * Photos/documents attached to a complaint as evidence.
 */
@Entity
@Table(name = "complaint_attachments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintAttachment extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Column(name = "file_type", length = 50)
    private String fileType;

    @Column(name = "file_name", length = 200)
    private String fileName;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;
}
