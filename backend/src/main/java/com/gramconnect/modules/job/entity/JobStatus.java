package com.gramconnect.modules.job.entity;

/**
 * Lifecycle states for a Job posting.
 */
public enum JobStatus {
    OPEN,
    FILLED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    EXPIRED;

    public boolean isOpenForApplications() {
        return this == OPEN;
    }
}
