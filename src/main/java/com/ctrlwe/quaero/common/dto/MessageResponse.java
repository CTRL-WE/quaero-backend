package com.ctrlwe.quaero.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Lightweight DTO that carries a single text message.
 *
 * <p>Useful for simple acknowledgement responses where no structured
 * data payload is required (e.g. "Password updated successfully").</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public class MessageResponse {

    /**
     * The response message text.
     */
    private final String message;
}
