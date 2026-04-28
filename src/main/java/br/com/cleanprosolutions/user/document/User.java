package br.com.cleanprosolutions.user.document;

import br.com.cleanprosolutions.user.enumerations.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB document representing a user (CLIENT or CONTRACTOR) in the platform.
 *
 * <p>Stores user profile data, geolocation for proximity search, and aggregated rating.
 * Authentication credentials are stored separately in the {@code auth-service}.</p>
 *
 * <p>The {@code location} field uses a {@link GeoJsonPoint} with a 2dsphere index,
 * enabling MongoDB to perform efficient proximity queries.</p>
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User {

    /** MongoDB generated unique identifier. */
    @Id
    private String id;

    /** User full name. */
    private String name;

    /** User email — unique across the platform. */
    @Indexed(unique = true)
    private String email;

    /** User phone number. */
    private String phone;

    /** User role in the platform (CLIENT or CONTRACTOR). */
    private UserType type;

    /** Physical address. */
    private Address address;

    /**
     * Geographic coordinates stored as GeoJSON Point.
     *
     * <p>Format: {@code [longitude, latitude]} — MongoDB GeoJSON standard.
     * The 2dsphere index enables {@code $nearSphere} and {@code $geoWithin} queries.</p>
     */
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    /**
     * Average rating score (1.0–5.0).
     *
     * <p>Updated asynchronously when the {@code rating-service} publishes a
     * {@code RatingCreated} event.</p>
     */
    private double avgRating;

    /** Total number of ratings received. */
    private int totalRatings;

    /** Reference to the corresponding credential in {@code auth-service}. */
    private String authId;

    /** Whether the account is active. Inactive accounts are soft-deleted. */
    private boolean active;

    /** Profile creation timestamp (UTC). */
    private Instant createdAt;

    /** Last update timestamp (UTC). */
    private Instant updatedAt;
}
