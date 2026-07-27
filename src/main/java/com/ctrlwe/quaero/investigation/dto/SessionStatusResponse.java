package com.ctrlwe.quaero.investigation.dto;

import com.ctrlwe.quaero.investigation.entity.SessionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Read-model returned by the "get session status" endpoint.
 *
 * <p>Carries just enough information for the frontend and the future
 * Submission module to understand the current state of a user's
 * investigation for a given case:</p>
 * <ul>
 *   <li>{@code sessionExists} — tells the caller whether the user has
 *       ever started investigating this case.</li>
 *   <li>{@code status} — the lifecycle status of the session if one
 *       exists, or {@code null} if {@code sessionExists} is {@code false}.</li>
 *   <li>{@code turnCount} — the total number of completed exchanges so
 *       far, or {@code 0} if no session exists.</li>
 * </ul>
 *
 * @param sessionExists {@code true} if a session record exists for this
 *                      user/case pair (regardless of status)
 * @param status        the current {@link SessionStatus}, or {@code null}
 *                      if no session exists
 * @param turnCount     the number of completed conversation exchanges;
 *                      {@code 0} if no session exists
 * @author Quaero Engineering
 * @since 1.0
 */
@Schema(description = "Read-model for a user's investigation session state on a given case.")
public record SessionStatusResponse(

        @Schema(
            description = "Whether the authenticated user has a session record " +
                          "(ACTIVE or SUBMITTED) for this case.",
            example = "true"
        )
        boolean sessionExists,

        @Schema(
            description = "The lifecycle status of the session (ACTIVE or SUBMITTED), " +
                          "or null if sessionExists is false.",
            example = "ACTIVE",
            nullable = true
        )
        SessionStatus status,

        @Schema(
            description = "Total number of completed conversation exchanges. " +
                          "0 if no session exists.",
            example = "4"
        )
        int turnCount
) {}
