package com.ctrlwe.quaero.user;

/**
 * Lifecycle status of a {@link User} account on the Quaero platform.
 *
 * <p>Only {@link #ACTIVE} accounts are permitted to authenticate and
 * interact with platform features. {@link #SUSPENDED} accounts are
 * temporarily locked by moderators or administrators.
 * {@link #DELETED} accounts are soft-deleted and excluded from all
 * active queries.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public enum AccountStatus {

    /** The account is fully operational and the user may authenticate. */
    ACTIVE,

    /** The account has been temporarily suspended by a moderator or admin. */
    SUSPENDED,

    /** The account has been soft-deleted and is no longer accessible. */
    DELETED
}
