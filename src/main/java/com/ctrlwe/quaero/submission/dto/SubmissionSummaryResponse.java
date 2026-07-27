package com.ctrlwe.quaero.submission.dto;

import com.ctrlwe.quaero.submission.entity.SubmissionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Lightweight response payload containing only the essential fields
 * of a submission.
 *
 * <p>Designed for list views and feed cards where the full submission
 * detail is not required. Reduces payload size and avoids exposing
 * unnecessary information in summary contexts.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionSummaryResponse {

    /**
     * The unique identifier of the submission.
     */
    private Long id;

    /**
     * A concise title summarising the submission's claim or argument.
     */
    private String title;

    /**
     * The name of the primary source referenced in the submission.
     */
    private String sourceName;

    /**
     * The current lifecycle status of this submission.
     */
    private SubmissionStatus status;
}
