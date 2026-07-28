package com.ctrlwe.quaero.investigation.exception;

import com.ctrlwe.quaero.exception.ErrorCode;
import com.ctrlwe.quaero.exception.ResourceNotFoundException;

/**
 * Thrown when an {@link com.ctrlwe.quaero.investigation.entity.InvestigationSession}
 * cannot be found for the given user/case pair.
 *
 * <p><strong>Exception-handler wiring:</strong> This exception extends
 * {@link ResourceNotFoundException}, which is already handled by the
 * platform-wide {@code GlobalExceptionHandler} via
 * {@code @ExceptionHandler(ResourceNotFoundException.class)}, mapping it
 * to HTTP 404. No additional handler method needs to be added —
 * polymorphism covers this class automatically.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public class SessionNotFoundException extends ResourceNotFoundException {

    /**
     * Constructs a {@code SessionNotFoundException} for a specific user/case pair.
     *
     * @param userId the ID of the requesting user
     * @param caseId the ID of the case
     */
    public SessionNotFoundException(Long userId, Long caseId) {
        super(
            "No investigation session found for user " + userId
                + " on case " + caseId + ".",
            ErrorCode.RESOURCE_NOT_FOUND
        );
    }
}
