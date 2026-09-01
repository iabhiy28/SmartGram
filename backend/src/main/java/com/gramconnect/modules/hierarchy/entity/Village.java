package com.gramconnect.modules.hierarchy.entity;

import com.gramconnect.common.entity.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Village entity mapping the `villages` table.
 * Primary geographical scoping node for citizens, complaints, jobs, equipment, and services.
 */
@Entity
@Table(name = "villages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Village extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "panchayat_id", nullable = false)
    private Panchayat panchayat;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "pin_code", nullable = false, length = 6)
    private String pinCode;

    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "population")
    private Integer population;
}
