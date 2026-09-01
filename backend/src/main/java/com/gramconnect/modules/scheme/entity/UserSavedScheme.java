package com.gramconnect.modules.scheme.entity;

import com.gramconnect.common.entity.BaseAuditableEntity;
import com.gramconnect.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * UserSavedScheme Entity mapping `user_saved_schemes`.
 * Bookmarked/saved schemes for a user.
 */
@Entity
@Table(name = "user_saved_schemes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "scheme_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSavedScheme extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scheme_id", nullable = false)
    private GovernmentScheme scheme;
}
