package com.ctrlwe.quaero.user;

/**
 * Enumeration of user roles within the Quaero platform.
 *
 * <p>Each role represents a distinct level of privilege. The default
 * role assigned during registration is {@link #USER}. Elevated roles
 * ({@link #MODERATOR}, {@link #ADMIN}) are granted through
 * administrative actions — never through self-assignment.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public enum Role {

    /** Standard platform user with access to investigations and submissions. */
    USER,

    /** Elevated user with content moderation capabilities. */
    MODERATOR,

    /** Full administrative access to all platform features and management tools. */
    ADMIN
}
