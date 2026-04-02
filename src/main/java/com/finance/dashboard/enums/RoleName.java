package com.finance.dashboard.enums;

/**
 * Defines the three access tiers in the RBAC model.
 * VIEWER  → read-only dashboard access
 * ANALYST → read financial records + dashboard
 * ADMIN   → full CRUD access
 */
public enum RoleName {
    VIEWER,
    ANALYST,
    ADMIN
}