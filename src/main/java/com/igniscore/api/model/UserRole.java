package com.igniscore.api.model;

/**
 * Enumeration representing the different roles a user can have within the system.
 *
 * <p>These roles are typically used for authorization and access control,
 * defining what actions a user is allowed to perform.
 *
 * <p>Available roles:
 * <ul>
 *     <li>OWNER    - Full control over company resources and settings</li>
 *     <li>ADMIN    - Elevated privileges for management operations</li>
 *     <li>EMPLOYEE - Standard user with limited permissions</li>
 * </ul>
 *
 * <p>Design notes:
 * <ul>
 *     <li>Each enum value contains a string representation used in persistence or security layers</li>
 *     <li>This abstraction allows decoupling between internal role representation and external usage</li>
 * </ul>
 */
public enum UserRole {

    OWNER("owner"),
    EMPLOYEE("employee"),
    ADMIN("admin");

    /**
     * String representation of the role.
     * <p>Used for serialization, persistence, or integration with security frameworks.
     */
    private final String role;

    /**
     * Enum constructor.
     *
     * @param role string value representing the role
     */
    UserRole(String role) {
        this.role = role;
    }

    /**
     * Returns the string representation of the role.
     *
     * @return role as string
     */
    public String getRole() {
        return role;
    }
}