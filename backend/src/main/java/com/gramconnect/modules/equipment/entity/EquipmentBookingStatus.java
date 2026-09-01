package com.gramconnect.modules.equipment.entity;

/**
 * Lifecycle states for an Equipment Booking.
 *
 * Allowed Transitions:
 *   PENDING   -> CONFIRMED | REJECTED | CANCELLED
 *   CONFIRMED -> ACTIVE | CANCELLED
 *   ACTIVE    -> COMPLETED | CANCELLED
 *   COMPLETED -> (terminal)
 *   REJECTED  -> (terminal)
 *   CANCELLED -> (terminal)
 */
public enum EquipmentBookingStatus {
    PENDING,
    CONFIRMED,
    ACTIVE,
    COMPLETED,
    CANCELLED,
    REJECTED;

    public boolean isDateBlocking() {
        return this == PENDING || this == CONFIRMED || this == ACTIVE;
    }

    public boolean canTransitionTo(EquipmentBookingStatus next) {
        return switch (this) {
            case PENDING -> next == CONFIRMED || next == REJECTED || next == CANCELLED;
            case CONFIRMED -> next == ACTIVE || next == CANCELLED;
            case ACTIVE -> next == COMPLETED || next == CANCELLED;
            case COMPLETED, CANCELLED, REJECTED -> false;
        };
    }
}
