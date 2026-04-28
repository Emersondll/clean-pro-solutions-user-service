package br.com.cleanprosolutions.user.repository;

import br.com.cleanprosolutions.user.document.User;
import br.com.cleanprosolutions.user.enumerations.UserType;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link User} persistence and geospatial queries.
 *
 * <p>Extends {@link MongoRepository} with custom finders for email lookup
 * and MongoDB 2dsphere proximity search.</p>
 *
 * <p><b>Geospatial notes:</b></p>
 * <ul>
 *   <li>The {@code location} field must be indexed with {@code GEO_2DSPHERE}.</li>
 *   <li>{@link Point} uses {@code (longitude, latitude)} order — MongoDB convention.</li>
 *   <li>{@link Distance} with {@code Metrics.KILOMETERS} triggers spherical calculation.</li>
 * </ul>
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Finds a user by email address.
     *
     * @param email the email to search
     * @return an {@link Optional} containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether an email is already registered.
     *
     * @param email the email to check
     * @return {@code true} if the email exists
     */
    boolean existsByEmail(String email);

    /**
     * Finds active contractors near a geographic point within a given distance.
     *
     * <p>Uses MongoDB {@code $nearSphere} operator via Spring Data naming convention.
     * Results are automatically sorted by distance (nearest first).</p>
     *
     * @param location   the center point {@code (longitude, latitude)}
     * @param maxDistance the maximum search radius
     * @return list of nearby active contractors sorted by distance
     */
    List<User> findByLocationNearAndActiveTrueAndType(Point location, Distance maxDistance, UserType type);

    /**
     * Finds all active users near a geographic point — regardless of type.
     *
     * @param location    the center point
     * @param maxDistance the maximum search radius
     * @return list of nearby active users sorted by distance
     */
    List<User> findByLocationNearAndActiveTrue(Point location, Distance maxDistance);
}
