package com.gramconnect.modules.job.entity;

/**
 * Lifecycle states for a Job Application.
 *
 * Allowed Transitions:
 *   APPLIED     -> SHORTLISTED | ACCEPTED | REJECTED | WITHDRAWN
 *   SHORTLISTED -> ACCEPTED | REJECTED | WITHDRAWN
 *   ACCEPTED    -> COMPLETED | WITHDRAWN
 *   REJECTED    -> (terminal)
 *   WITHDRAWN   -> (terminal)
 *   COMPLETED   -> (terminal - opens rating eligibility)
 */
public enum ApplicationStatus {
    APPLIED,
    SHORTLISTED,
    ACCEPTED,
    REJECTED,
    WITHDRAWN,
    COMPLETED;

    public boolean canTransitionTo(ApplicationStatus nextStatus) {
        return switch (this) {
            case APPLIED -> nextStatus == SHORTLISTED || nextStatus == ACCEPTED || nextStatus == REJECTED || nextStatus == WITHDRAWN;
            case SHORTLISTED -> nextStatus == ACCEPTED || nextStatus == REJECTED || nextStatus == WITHDRAWN;
            case ACCEPTED -> nextStatus == COMPLETED || nextStatus == WITHDRAWN;
            case REJECTED, WITHDRAWN, COMPLETED -> false;
        };
    }
}
