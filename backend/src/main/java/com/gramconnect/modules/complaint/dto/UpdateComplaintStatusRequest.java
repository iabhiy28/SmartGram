package com.gramconnect.modules.complaint.dto;

import com.gramconnect.modules.complaint.entity.ComplaintStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateComplaintStatusRequest {

    @NotNull(message = "Target status is required")
    private ComplaintStatus status;

    /** Admin who is being assigned (for UNDER_REVIEW / IN_PROGRESS) */
    private UUID assignedToUserId;

    @Size(max = 2000, message = "Resolution notes must not exceed 2000 characters")
    private String resolutionNotes;
}
