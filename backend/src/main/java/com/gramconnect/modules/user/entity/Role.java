package com.gramconnect.modules.user.entity;

/**
 * Role-Based Access Control (RBAC) Granted Authorities for GramConnect.
 */
public enum Role {
    ROLE_VILLAGER,
    ROLE_SERVICE_PROVIDER,
    ROLE_EMPLOYER,
    ROLE_PANCHAYAT_ADMIN,
    ROLE_SUPER_ADMIN;

    public static Role fromString(String roleStr) {
        if (roleStr == null) return ROLE_VILLAGER;
        String normalized = roleStr.trim().toUpperCase();
        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }
        for (Role r : values()) {
            if (r.name().equals(normalized)) {
                return r;
            }
        }
        return ROLE_VILLAGER;
    }
}
