package br.com.cleanprosolutions.user.dto;

import java.time.Instant;

/**
 * Standard error response DTO for all exception handlers in the user-service.
 *
 * @param timestamp     when the error occurred (UTC)
 * @param status        HTTP status code
 * @param message       human-readable error description
 * @param correlationId distributed tracing correlation ID (may be null)
 */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String message,
        String correlationId
) {

    /**
     * @param status        HTTP status code
     * @param message       error message
     * @param correlationId tracing correlation ID
     * @return a new ErrorResponse with current timestamp
     */
    public static ErrorResponse of(final int status, final String message, final String correlationId) {
        return new ErrorResponse(Instant.now(), status, message, correlationId);
    }
}
