package com.gramconnect.modules.complaint.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateComplaintRequest {

    @NotNull(message = "Complaint category is required")
    private UUID categoryId;

    @NotNull(message = "Village is required")
    private UUID villageId;

    @NotBlank(message = "Complaint title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @NotBlank(message = "Complaint description is required")
    @Size(min = 20, max = 5000, message = "Description must be between 20 and 5000 characters")
    private String description;

    @Size(max = 300, message = "Location description must not exceed 300 characters")
    private String locationDescription;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @Pattern(regexp = "^(LOW|MEDIUM|HIGH|CRITICAL)$", message = "Priority must be LOW, MEDIUM, HIGH, or CRITICAL")
    private String priority;

    /** URLs of pre-uploaded attachments */
    private List<String> attachmentUrls;
}
