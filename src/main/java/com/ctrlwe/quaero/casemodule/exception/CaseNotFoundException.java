package com.ctrlwe.quaero.casemodule.exception;

import com.ctrlwe.quaero.exception.ErrorCode;
import com.ctrlwe.quaero.exception.ResourceNotFoundException;

/**
 * Thrown when a {@link com.ctrlwe.quaero.casemodule.entity.Case}
 * cannot be found by the supplied ID, or when the found case is not
 * in a state accessible to the caller (e.g. it exists but is not
 * {@link com.ctrlwe.quaero.casemodule.entity.CaseStatus#PUBLISHED}).
 *
 * <p><strong>Exception-handler wiring:</strong> This exception extends
 * {@link ResourceNotFoundException}, which is already handled by the
 * platform-wide {@code GlobalExceptionHandler} with a mapping to HTTP 404
 * and the standard {@code ApiErrorResponse} shape. No additional
 * {@code @ExceptionHandler} method needs to be added to the global handler —
 * the existing {@code handleResourceNotFound} method catches this class
 * through polymorphism because it declares
 * {@code @ExceptionHandler(ResourceNotFoundException.class)}.</p>
 *
 * <p>This is the zero-touch approach: the global handler requires no
 * modification, and no module-local exception handler is created.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * throw new CaseNotFoundException(caseId);
 * }</pre>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public class CaseNotFoundException extends ResourceNotFoundException {

    /**
     * Constructs a {@code CaseNotFoundException} for a specific case ID.
     *
     * @param caseId the ID of the case that could not be found
     */
    public CaseNotFoundException(Long caseId) {
        super(
            "Case with id " + caseId + " does not exist or is not published.",
            ErrorCode.RESOURCE_NOT_FOUND
        );
    }
}
