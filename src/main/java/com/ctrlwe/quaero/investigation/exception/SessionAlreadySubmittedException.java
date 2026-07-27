package com.ctrlwe.quaero.investigation.exception;

import com.ctrlwe.quaero.exception.ConflictException;
import com.ctrlwe.quaero.exception.ErrorCode;

/**
 * Thrown when a user attempts to post a message into an investigation session
 * whose status is {@link com.ctrlwe.quaero.investigation.entity.SessionStatus#SUBMITTED}.
 *
 * <p>Once a user has submitted their verdict through the Submission module,
 * the investigation session is closed to further AI conversation. This
 * exception enforces that rule at the service layer.</p>
 *
 * <p><strong>Exception-handler wiring:</strong> This exception extends
 * {@link ConflictException}, which is already handled by the platform-wide
 * {@code GlobalExceptionHandler} via
 * {@code @ExceptionHandler(ConflictException.class)}, mapping it to
 * HTTP 409 Conflict. No additional handler method needs to be added.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public class SessionAlreadySubmittedException extends ConflictException {

    /**
     * Constructs a {@code SessionAlreadySubmittedException} for a specific
     * session.
     *
     * @param sessionId the ID of the session that has already been submitted
     */
    public SessionAlreadySubmittedException(Long sessionId) {
        super(
            "Investigation session " + sessionId
                + " has already been submitted and cannot accept new messages.",
            ErrorCode.CONFLICT
        );
    }
}
