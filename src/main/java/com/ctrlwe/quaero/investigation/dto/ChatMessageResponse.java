package com.ctrlwe.quaero.investigation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response body returned by the "post a message" endpoint after a
 * successful AI Mentor exchange.
 *
 * <h3>nudgeSubmission</h3>
 * <p>{@code nudgeSubmission} is {@code true} when the session's
 * {@code turnCount} reaches or exceeds the nudge threshold of
 * <strong>4 turns</strong>. When {@code true}, the frontend should
 * prompt the user to consider submitting their verdict. It does not
 * force submission — the user can continue the conversation.</p>
 *
 * @param aiReply         the AI Mentor's Socratic response text
 * @param turnCount       the total number of completed exchanges so far
 *                        (incremented by the service before this is returned)
 * @param nudgeSubmission {@code true} when {@code turnCount >= 4}, signalling
 *                        that the user has had enough exchanges to form a verdict
 * @author Quaero Engineering
 * @since 1.0
 */
@Schema(description = "Response from the AI Mentor after processing a user message.")
public record ChatMessageResponse(

        @Schema(
            description = "The AI Mentor's Socratic reply. The AI never states a verdict — " +
                          "it only asks guiding questions to help the user reason through the claim.",
            example = "What does the scientific consensus say about this topic, " +
                      "and where would you look to find that information?"
        )
        String aiReply,

        @Schema(
            description = "The total number of completed conversation exchanges in this session " +
                          "(one USER message + one AI reply counts as 1 turn).",
            example = "3"
        )
        int turnCount,

        @Schema(
            description = "True when turnCount >= 4, indicating the user has had sufficient " +
                          "exchanges to form a verdict. The frontend should show a soft prompt " +
                          "to submit. Does not prevent the user from continuing the conversation. " +
                          "Always false before the 4th turn.",
            example = "false"
        )
        boolean nudgeSubmission
) {}
