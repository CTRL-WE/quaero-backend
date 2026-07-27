package com.ctrlwe.quaero.ai.exception;

/**
 * Unchecked exception thrown when the AI subsystem encounters an
 * unrecoverable error.
 *
 * <p>Typical scenarios include:</p>
 * <ul>
 *   <li>Network failures when calling the upstream AI provider</li>
 *   <li>Non-2xx HTTP responses from the Gemini API</li>
 *   <li>Malformed or unparseable AI responses</li>
 *   <li>Timeout expiration while waiting for a response</li>
 * </ul>
 *
 * <p>Because this extends {@link RuntimeException}, callers are not
 * forced to handle it explicitly – but upstream layers (e.g. global
 * exception handlers) should map it to an appropriate HTTP status.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public class AiServiceException extends RuntimeException {

    /**
     * Constructs a new {@code AiServiceException} with the specified
     * detail message.
     *
     * @param message a human-readable description of the failure
     */
    public AiServiceException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code AiServiceException} with the specified
     * detail message and root cause.
     *
     * @param message a human-readable description of the failure
     * @param cause   the underlying throwable that triggered this exception
     */
    public AiServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
