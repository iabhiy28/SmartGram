package com.gramconnect.modules.announcement.entity;

import com.gramconnect.common.entity.BaseAuditableEntity;
import com.gramconnect.modules.hierarchy.entity.Village;
import jakarta.persistence.*;
import lombok.*;

/**
 * EmergencyContact Entity mapping `emergency_contacts`.
 * Village-level emergency contacts (Police, Fire, Ambulance, PHC).
 */
@Entity
@Table(name = "emergency_contacts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContact extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "village_id")
    private Village village; // null = district/state level

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "designation", length = 100)
    private String designation;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "alternate_phone", length = 15)
    private String alternatePhone;

    @Column(name = "service_type", nullable = false, length = 50)
    private String serviceType; // POLICE, FIRE, AMBULANCE, PHC, VETERINARY, ELECTRICITY

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;
}
