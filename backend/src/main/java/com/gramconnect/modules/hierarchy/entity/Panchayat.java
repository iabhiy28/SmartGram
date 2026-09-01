package com.gramconnect.modules.hierarchy.entity;

import com.gramconnect.common.entity.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Gram Panchayat entity mapping the `panchayats` table.
 */
@Entity
@Table(name = "panchayats")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Panchayat extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "office_address", columnDefinition = "TEXT")
    private String officeAddress;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;
}
