package com.ctrlwe.quaero.investigation.service;

import com.ctrlwe.quaero.investigation.dto.ChatMessageResponse;
import com.ctrlwe.quaero.investigation.dto.SessionStatusResponse;
import com.ctrlwe.quaero.investigation.entity.InvestigationSession;

/**
 * Service contract for the Investigation module.
 *
 * <p>This interface is the <strong>only</strong> permitted entry point into
 * Investigation logic from outside the module (e.g. from the controller).
 * The cross-module access rule for this codebase states that no module may
 * reach into another module's repository or entity directly — all inter-module
 * calls must go through the owning module's service interface.</p>
 *
 * <p>The three methods map precisely to the three use-cases this module
 * must serve in v1:</p>
 * <ol>
 *   <li>{@link #startOrResumeSession(Long, Long)} — find-or-create an ACTIVE
 *       session for a user/case pair, never duplicating.</li>
 *   <li>{@link #postMessage(Long, Long, String)} — run one Socratic exchange:
 *       persist the user turn, call the AI, persist the AI turn, return the
 *       reply.</li>
 *   <li>{@link #getSessionStatus(Long, Long)} — expose a lightweight status
 *       read-model for the future Submission module's existence check.</li>
 * </ol>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public interface InvestigationService {

    /**
     * Finds an existing {@code ACTIVE} investigation session for the given
     * user/case pair, or creates a new one if none exists.
     *
     * <p>This method is idempotent: calling it twice with the same arguments
     * will always return the same session entity (not create a duplicate).
     * The uniqueness invariant is enforced both here and by the database-level
     * unique constraint on {@code (user_id, case_id)}.</p>
     *
     * @param userId the ID of the authenticated user
     * @param caseId the ID of the case to investigate
     * @return the existing or newly created {@link InvestigationSession}
     */
    InvestigationSession startOrResumeSession(Long userId, Long caseId);

    /**
     * Processes one user message through the AI Mentor and returns the reply.
     *
     * <p>The full flow is:</p>
     * <ol>
     *   <li>Resolve or create the session via {@link #startOrResumeSession}.</li>
     *   <li>Reject the message if the session is {@code SUBMITTED}.</li>
     *   <li>Validate the case exists and is published (via {@code CaseService}).</li>
     *   <li>Persist the user's turn.</li>
     *   <li>Assemble the prompt from case context + conversation history.</li>
     *   <li>Call the AI Mentor.</li>
     *   <li>Persist the AI's reply.</li>
     *   <li>Increment the session's turn count.</li>
     *   <li>Return a {@link ChatMessageResponse}.</li>
     * </ol>
     *
     * @param userId      the ID of the authenticated user
     * @param caseId      the ID of the case being investigated
     * @param userMessage the user's raw message text
     * @return the AI Mentor's reply and updated turn count
     */
    ChatMessageResponse postMessage(Long userId, Long caseId, String userMessage);

    /**
     * Returns the current session status for the given user/case pair.
     *
     * <p>Returns a {@link SessionStatusResponse} with {@code sessionExists = false}
     * if the user has not yet started investigating this case. This method
     * exists today so the future Submission module can call it as an existence
     * check without depending on any investigation internals.</p>
     *
     * @param userId the ID of the authenticated user
     * @param caseId the ID of the case
     * @return a {@link SessionStatusResponse} describing the current session state
     */
    SessionStatusResponse getSessionStatus(Long userId, Long caseId);
}
