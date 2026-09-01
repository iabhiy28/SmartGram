package com.gramconnect.modules.review.dto;

import com.gramconnect.modules.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private UUID id;
    private UUID bookingId;
    private UUID reviewerId;
    private String reviewerName;
    private UUID providerId;
    private Integer rating;
    private Integer punctualityRating;
    private Integer qualityRating;
    private Integer pricingRating;
    private Integer behaviorRating;
    private String reviewTitle;
    private String reviewComment;
    private String providerReply;
    private Instant repliedAt;
    private Instant createdAt;

    public static ReviewResponse fromEntity(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .bookingId(review.getBooking().getId())
                .reviewerId(review.getReviewer().getId())
                .reviewerName(review.getReviewer().getFullName())
                .providerId(review.getProvider().getId())
                .rating(review.getRating())
                .punctualityRating(review.getPunctualityRating())
                .qualityRating(review.getQualityRating())
                .pricingRating(review.getPricingRating())
                .behaviorRating(review.getBehaviorRating())
                .reviewTitle(review.getReviewTitle())
                .reviewComment(review.getReviewComment())
                .providerReply(review.getProviderReply())
                .repliedAt(review.getRepliedAt())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
