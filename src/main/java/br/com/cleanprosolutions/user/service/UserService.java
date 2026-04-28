package br.com.cleanprosolutions.user.service;

import br.com.cleanprosolutions.user.dto.UserRequest;
import br.com.cleanprosolutions.user.dto.UserResponse;
import br.com.cleanprosolutions.user.enumerations.UserType;

import java.util.List;

/**
 * Service interface defining user profile management operations.
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
public interface UserService {

    /**
     * Creates a new user profile.
     *
     * @param request the user creation data
     * @return the created user profile
     * @throws br.com.cleanprosolutions.user.exception.UserAlreadyExistsException if email is taken
     */
    UserResponse create(UserRequest request);

    /**
     * Retrieves a user profile by ID.
     *
     * @param id the MongoDB user ID
     * @return the user profile
     * @throws br.com.cleanprosolutions.user.exception.UserNotFoundException if not found
     */
    UserResponse findById(String id);

    /**
     * Retrieves a user profile by email address.
     *
     * @param email the user email
     * @return the user profile
     * @throws br.com.cleanprosolutions.user.exception.UserNotFoundException if not found
     */
    UserResponse findByEmail(String email);

    /**
     * Updates an existing user profile.
     *
     * @param id      the user ID
     * @param request the update data
     * @return the updated user profile
     * @throws br.com.cleanprosolutions.user.exception.UserNotFoundException if not found
     */
    UserResponse update(String id, UserRequest request);

    /**
     * Deactivates a user account (soft delete).
     *
     * @param id the user ID to deactivate
     * @throws br.com.cleanprosolutions.user.exception.UserNotFoundException if not found
     */
    void deactivate(String id);

    /**
     * Finds active users (optionally filtered by type) near a geographic point.
     *
     * <p>Uses MongoDB 2dsphere index for efficient proximity search.
     * Results are sorted nearest-first.</p>
     *
     * @param latitude   center latitude
     * @param longitude  center longitude
     * @param radiusKm   maximum search radius in kilometers
     * @param type       optional filter by user type (null returns all active users)
     * @return list of nearby user profiles
     */
    List<UserResponse> findNearby(double latitude, double longitude, double radiusKm, UserType type);

    /**
     * Updates the rating aggregation for a contractor.
     *
     * <p>Called asynchronously when the {@code rating-service} publishes a
     * {@code RatingCreated} event.</p>
     *
     * @param contractorId the contractor user ID
     * @param avgRating    the new average rating score
     * @param totalRatings the new total number of ratings
     */
    void updateRating(String contractorId, double avgRating, int totalRatings);
}
