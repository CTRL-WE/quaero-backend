package com.ctrlwe.quaero.submission.validator;

import com.ctrlwe.quaero.common.util.UrlValidationUtil;
import com.ctrlwe.quaero.exception.BadRequestException;
import com.ctrlwe.quaero.exception.ErrorCode;
import com.ctrlwe.quaero.submission.dto.CreateSubmissionRequest;
import com.ctrlwe.quaero.submission.dto.UpdateSubmissionRequest;
import org.springframework.stereotype.Component;

/**
 * Performs business-level validation for submission operations.
 *
 * <p>This validator enforces domain rules that go beyond what
 * Bean Validation annotations on the DTOs can express — for
 * example, URL format validation using {@link UrlValidationUtil}.
 * It is invoked by the service layer <em>before</em> any
 * persistence operation.</p>
 *
 * <p>All checks are stateless and read-only; this class never
 * interacts with a repository.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Component
public class SubmissionValidator {

    /** Maximum length for the submission title (mirrors entity column length). */
    private static final int TITLE_MAX_LENGTH = 255;

    /** Minimum length for the submission title (mirrors DTO constraint). */
    private static final int TITLE_MIN_LENGTH = 3;

    /** Maximum length for the submission description (mirrors DTO constraint). */
    private static final int DESCRIPTION_MAX_LENGTH = 5000;

    /** Minimum length for the submission description (mirrors DTO constraint). */
    private static final int DESCRIPTION_MIN_LENGTH = 10;

    /** Maximum length for the source name (mirrors entity column length). */
    private static final int SOURCE_NAME_MAX_LENGTH = 255;

    /** Maximum length for the source URL (mirrors entity column length). */
    private static final int SOURCE_URL_MAX_LENGTH = 2048;

    /**
     * Validates all fields of a {@link CreateSubmissionRequest}.
     *
     * <p>Checks that mandatory fields (title, description,
     * evidenceType, confidenceLevel) are present and within
     * acceptable bounds, and that the optional {@code sourceUrl}
     * is a valid HTTP/HTTPS URL when supplied.</p>
     *
     * @param request the creation request to validate
     * @throws BadRequestException if any validation rule is violated
     */
    public void validateCreate(CreateSubmissionRequest request) {
        validateTitle(request.getTitle());
        validateDescription(request.getDescription());
        validateSourceName(request.getSourceName());
        validateSourceUrl(request.getSourceUrl());

        if (request.getEvidenceType() == null) {
            throw new BadRequestException(
                    "Evidence type is required",
                    ErrorCode.BAD_REQUEST);
        }
        if (request.getConfidenceLevel() == null) {
            throw new BadRequestException(
                    "Confidence level is required",
                    ErrorCode.BAD_REQUEST);
        }
    }

    /**
     * Validates the non-{@code null} fields of an
     * {@link UpdateSubmissionRequest}.
     *
     * <p>Only fields that the caller has supplied (non-null) are
     * checked, supporting partial-update semantics. The same
     * constraints apply as for creation.</p>
     *
     * @param request the update request to validate
     * @throws BadRequestException if any supplied field violates
     *         a validation rule
     */
    public void validateUpdate(UpdateSubmissionRequest request) {
        if (request.getTitle() != null) {
            validateTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            validateDescription(request.getDescription());
        }
        if (request.getSourceName() != null) {
            validateSourceName(request.getSourceName());
        }
        if (request.getSourceUrl() != null) {
            validateSourceUrl(request.getSourceUrl());
        }
    }

    // ──────────────────────────────────────────────
    //  Private helpers
    // ──────────────────────────────────────────────

    /**
     * Validates that the title is not blank and within length bounds.
     */
    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new BadRequestException(
                    "Title cannot be blank",
                    ErrorCode.BAD_REQUEST);
        }
        if (title.length() < TITLE_MIN_LENGTH || title.length() > TITLE_MAX_LENGTH) {
            throw new BadRequestException(
                    "Title must be between " + TITLE_MIN_LENGTH
                            + " and " + TITLE_MAX_LENGTH + " characters",
                    ErrorCode.BAD_REQUEST);
        }
    }

    /**
     * Validates that the description is not blank and within length bounds.
     */
    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new BadRequestException(
                    "Description cannot be blank",
                    ErrorCode.BAD_REQUEST);
        }
        if (description.length() < DESCRIPTION_MIN_LENGTH
                || description.length() > DESCRIPTION_MAX_LENGTH) {
            throw new BadRequestException(
                    "Description must be between " + DESCRIPTION_MIN_LENGTH
                            + " and " + DESCRIPTION_MAX_LENGTH + " characters",
                    ErrorCode.BAD_REQUEST);
        }
    }

    /**
     * Validates that the source name, when supplied, does not exceed
     * the maximum length.
     */
    private void validateSourceName(String sourceName) {
        if (sourceName != null && sourceName.length() > SOURCE_NAME_MAX_LENGTH) {
            throw new BadRequestException(
                    "Source name must not exceed " + SOURCE_NAME_MAX_LENGTH + " characters",
                    ErrorCode.BAD_REQUEST);
        }
    }

    /**
     * Validates that the source URL, when supplied, is a valid
     * HTTP or HTTPS URL and does not exceed the maximum length.
     */
    private void validateSourceUrl(String sourceUrl) {
        if (sourceUrl == null || sourceUrl.isBlank()) {
            return;
        }
        if (sourceUrl.length() > SOURCE_URL_MAX_LENGTH) {
            throw new BadRequestException(
                    "Source URL must not exceed " + SOURCE_URL_MAX_LENGTH + " characters",
                    ErrorCode.BAD_REQUEST);
        }
        if (!UrlValidationUtil.isValidHttpUrl(sourceUrl)) {
            throw new BadRequestException(
                    "Source URL must be a valid HTTP or HTTPS URL",
                    ErrorCode.BAD_REQUEST);
        }
    }
}
