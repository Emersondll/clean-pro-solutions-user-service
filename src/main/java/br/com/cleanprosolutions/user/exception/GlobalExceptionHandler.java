package br.com.cleanprosolutions.user.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Global exception handler for the user-service.
 *
 * <p>Maps domain and validation exceptions to RFC 7807 Problem Detail responses.</p>
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link UserNotFoundException} — HTTP 404.
     *
     * @param ex the exception
     * @return a Problem Detail with NOT_FOUND status
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(final UserNotFoundException ex) {
        log.warn("UserNotFoundException: {}", ex.getMessage());
        final ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setType(URI.create("https://cleanprosolutions.com.br/errors/user-not-found"));
        detail.setTitle("User Not Found");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    /**
     * Handles {@link UserAlreadyExistsException} — HTTP 409.
     *
     * @param ex the exception
     * @return a Problem Detail with CONFLICT status
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(final UserAlreadyExistsException ex) {
        log.warn("UserAlreadyExistsException: {}", ex.getMessage());
        final ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        detail.setType(URI.create("https://cleanprosolutions.com.br/errors/user-already-exists"));
        detail.setTitle("User Already Exists");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    /**
     * Handles bean validation errors — HTTP 400.
     *
     * @param ex the validation exception
     * @return a Problem Detail with BAD_REQUEST status listing all field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(final MethodArgumentNotValidException ex) {
        final String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validation error: {}", errors);
        final ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errors);
        detail.setType(URI.create("https://cleanprosolutions.com.br/errors/validation"));
        detail.setTitle("Validation Error");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    /**
     * Fallback handler for unexpected exceptions — HTTP 500.
     *
     * @param ex the exception
     * @return a Problem Detail with INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(final Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        final ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        detail.setType(URI.create("https://cleanprosolutions.com.br/errors/internal"));
        detail.setTitle("Internal Server Error");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }
}
