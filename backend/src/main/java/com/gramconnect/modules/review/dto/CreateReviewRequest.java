package com.gramconnect.modules.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {

    @NotNull(message = "Overall rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer rating;

    @Min(1) @Max(5)
    private Integer punctualityRating;

    @Min(1) @Max(5)
    private Integer qualityRating;

    @Min(1) @Max(5)
    private Integer pricingRating;

    @Min(1) @Max(5)
    private Integer behaviorRating;

    private String reviewTitle;
    private String reviewComment;
}
