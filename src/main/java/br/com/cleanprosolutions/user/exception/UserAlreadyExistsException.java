package br.com.cleanprosolutions.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when attempting to register an email that is already in use.
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a UserAlreadyExistsException for the given email.
     *
     * @param email the conflicting email address
     */
    public UserAlreadyExistsException(final String email) {
        super("User already exists with email: " + email);
    }
}
