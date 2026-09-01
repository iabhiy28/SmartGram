package com.gramconnect.modules.complaint.entity;

/**
 * Lifecycle states for a Civic Complaint.
 *
 * Transitions:
 *   SUBMITTED    -> UNDER_REVIEW | REJECTED
 *   UNDER_REVIEW -> IN_PROGRESS | REJECTED
 *   IN_PROGRESS  -> RESOLVED | ESCALATED
 *   ESCALATED    -> IN_PROGRESS | RESOLVED
 *   RESOLVED     -> REOPENED
 *   REOPENED     -> IN_PROGRESS
 *   REJECTED     -> (terminal)
 */
public enum ComplaintStatus {
    SUBMITTED,
    UNDER_REVIEW,
    IN_PROGRESS,
    RESOLVED,
    ESCALATED,
    REOPENED,
    REJECTED;

    public boolean canTransitionTo(ComplaintStatus next) {
        return switch (this) {
            case SUBMITTED -> next == UNDER_REVIEW || next == REJECTED;
            case UNDER_REVIEW -> next == IN_PROGRESS || next == REJECTED;
            case IN_PROGRESS -> next == RESOLVED || next == ESCALATED;
            case ESCALATED -> next == IN_PROGRESS || next == RESOLVED;
            case RESOLVED -> next == REOPENED;
            case REOPENED -> next == IN_PROGRESS;
            case REJECTED -> false;
        };
    }

    public boolean isTerminal() {
        return this == REJECTED;
    }
}
