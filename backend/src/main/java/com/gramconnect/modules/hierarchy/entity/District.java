package com.gramconnect.modules.hierarchy.entity;

import com.gramconnect.common.entity.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * District entity mapping the `districts` table.
 */
@Entity
@Table(name = "districts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class District extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "code", length = 20)
    private String code;
}
