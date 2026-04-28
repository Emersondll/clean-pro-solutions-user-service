package br.com.cleanprosolutions.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user is not found in the database.
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a UserNotFoundException with the identifier that was not found.
     *
     * @param identifier the ID or email that was not found
     */
    public UserNotFoundException(final String identifier) {
        super("User not found: " + identifier);
    }
}
