package com.ctrlwe.quaero.investigation.entity;

/**
 * Lifecycle status of an {@link InvestigationSession}.
 *
 * <p>A session begins in the {@link #ACTIVE} state when a user first starts
 * investigating a case. It transitions to {@link #SUBMITTED} when the user
 * finalises their reasoning through the (future) Submission module. Once
 * {@link #SUBMITTED}, no further AI conversation turns may be added to the
 * session.</p>
 *
 * <p>Valid transitions: {@code ACTIVE → SUBMITTED} (one-way only).</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public enum SessionStatus {

    /**
     * The session is live and accepting new conversation turns.
     * This is the only state in which the AI Mentor will respond
     * to user messages.
     */
    ACTIVE,

    /**
     * The user has finalised their investigation by submitting a verdict
     * through the Submission module. No further conversation turns are
     * accepted in this state.
     */
    SUBMITTED
}
