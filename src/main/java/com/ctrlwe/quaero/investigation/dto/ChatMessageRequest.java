package com.ctrlwe.quaero.investigation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for the "post a message" endpoint.
 *
 * <p>The {@code caseId} is supplied in the URL path by the controller;
 * it is included here as a record component solely to allow the service
 * layer to receive a single, self-contained object. The controller sets it
 * from the path variable before delegating to the service.</p>
 *
 * <p>Bean Validation ({@code @Valid}) is applied at the controller layer.
 * The only constraint is that {@code message} must not be blank — length
 * limits and content validation are intentionally not applied here; that
 * is handled conversationally by the AI Mentor.</p>
 *
 * @param caseId  the ID of the case the user is investigating (from path)
 * @param message the user's message to the AI Mentor (must not be blank)
 * @author Quaero Engineering
 * @since 1.0
 */
@Schema(description = "Request body for sending a message to the AI Mentor.")
public record ChatMessageRequest(

        @Schema(description = "The ID of the case being investigated.", example = "1")
        Long caseId,

        @NotBlank(message = "message must not be blank")
        @Schema(
            description = "The user's message to the AI Mentor. Must not be blank.",
            example = "What kind of sources should I look for to evaluate this claim?"
        )
        String message
) {}
