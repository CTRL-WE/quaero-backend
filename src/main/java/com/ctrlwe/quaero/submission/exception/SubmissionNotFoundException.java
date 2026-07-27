package com.ctrlwe.quaero.submission.exception;

import com.ctrlwe.quaero.exception.ErrorCode;
import com.ctrlwe.quaero.exception.ResourceNotFoundException;

/**
 * Thrown when a requested {@link com.ctrlwe.quaero.submission.entity.Submission}
 * cannot be found in the system.
 *
 * <p>This exception extends {@link ResourceNotFoundException} and is
 * mapped to HTTP 404 by the global exception handler. It is raised by
 * service-layer look-ups when a submission ID does not correspond to
 * any persisted record.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * throw new SubmissionNotFoundException(
 *         "Submission with id 42 does not exist");
 * }</pre>
 *
 * @author Quaero Engineering
 * @since 1.0
 * @see ResourceNotFoundException
 * @see ErrorCode#RESOURCE_NOT_FOUND
 */
public class SubmissionNotFoundException extends ResourceNotFoundException {

    /**
     * Constructs a new {@code SubmissionNotFoundException} with the
     * given detail message. The error code is automatically set to
     * {@link ErrorCode#RESOURCE_NOT_FOUND}.
     *
     * @param message a human-readable description of the missing
     *                submission (e.g. "Submission with id 42 does not exist")
     */
    public SubmissionNotFoundException(String message) {
        super(message, ErrorCode.RESOURCE_NOT_FOUND);
    }
}
