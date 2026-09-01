package com.gramconnect.modules.equipment.entity;

import com.gramconnect.common.entity.BaseAuditableEntity;
import com.gramconnect.modules.hierarchy.entity.Village;
import com.gramconnect.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Agricultural Equipment Entity mapping `equipment`.
 * Tractors, rotavators, harvesters, and irrigation pumps available for rental.
 */
@Entity
@Table(name = "equipment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Equipment extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "village_id", nullable = false)
    private Village village;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private EquipmentCategory category;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "make", length = 100)
    private String make;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "year_of_purchase")
    private Integer yearOfPurchase;

    @Column(name = "horse_power")
    private Integer horsePower;

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "daily_rate", precision = 10, scale = 2)
    private BigDecimal dailyRate;

    @Column(name = "photo_urls", columnDefinition = "JSONB")
    private String photoUrls;

    @Builder.Default
    @Column(name = "is_operational", nullable = false)
    private Boolean isOperational = true;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    @Builder.Default
    @Column(name = "service_radius_km", nullable = false)
    private Integer serviceRadiusKm = 15;

    @Builder.Default
    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EquipmentBooking> bookings = new ArrayList<>();
}
