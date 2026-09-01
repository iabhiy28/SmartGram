package com.gramconnect.modules.review.entity;

import com.gramconnect.common.entity.BaseAuditableEntity;
import com.gramconnect.modules.service.entity.ServiceBooking;
import com.gramconnect.modules.service.entity.ServiceProviderProfile;
import com.gramconnect.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Review Entity mapping `reviews`.
 * Strictly tied 1:1 to a completed ServiceBooking.
 */
@Entity
@Table(name = "reviews")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseAuditableEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private ServiceBooking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private ServiceProviderProfile provider;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "punctuality_rating")
    private Integer punctualityRating;

    @Column(name = "quality_rating")
    private Integer qualityRating;

    @Column(name = "pricing_rating")
    private Integer pricingRating;

    @Column(name = "behavior_rating")
    private Integer behaviorRating;

    @Column(name = "review_title", length = 150)
    private String reviewTitle;

    @Column(name = "review_comment", columnDefinition = "TEXT")
    private String reviewComment;

    @Column(name = "provider_reply", columnDefinition = "TEXT")
    private String providerReply;

    @Column(name = "replied_at")
    private Instant repliedAt;
}
