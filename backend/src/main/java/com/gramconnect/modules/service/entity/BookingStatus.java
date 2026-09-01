package com.gramconnect.modules.service.entity;

/**
 * Service Booking Lifecycle States.
 *
 * Allowed Transitions:
 *   REQUESTED   -> ACCEPTED | DECLINED | CANCELLED
 *   ACCEPTED    -> IN_PROGRESS | CANCELLED
 *   IN_PROGRESS -> COMPLETED | CANCELLED
 *   COMPLETED   -> (terminal - opens verified review eligibility)
 *   DECLINED    -> (terminal)
 *   CANCELLED   -> (terminal)
 */
public enum BookingStatus {
    REQUESTED,
    ACCEPTED,
    DECLINED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    public boolean canTransitionTo(BookingStatus nextStatus) {
        return switch (this) {
            case REQUESTED -> nextStatus == ACCEPTED || nextStatus == DECLINED || nextStatus == CANCELLED;
            case ACCEPTED -> nextStatus == IN_PROGRESS || nextStatus == CANCELLED;
            case IN_PROGRESS -> nextStatus == COMPLETED || nextStatus == CANCELLED;
            case COMPLETED, DECLINED, CANCELLED -> false;
        };
    }
}
