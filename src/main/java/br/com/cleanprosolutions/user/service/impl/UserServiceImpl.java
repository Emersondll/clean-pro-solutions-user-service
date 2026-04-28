package br.com.cleanprosolutions.user.service.impl;

import br.com.cleanprosolutions.user.document.User;
import br.com.cleanprosolutions.user.dto.UserRequest;
import br.com.cleanprosolutions.user.dto.UserResponse;
import br.com.cleanprosolutions.user.enumerations.UserType;
import br.com.cleanprosolutions.user.exception.UserAlreadyExistsException;
import br.com.cleanprosolutions.user.exception.UserNotFoundException;
import br.com.cleanprosolutions.user.mapper.UserMapper;
import br.com.cleanprosolutions.user.repository.UserRepository;
import br.com.cleanprosolutions.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Implementation of {@link UserService}.
 *
 * <p>Handles user profile management and proximity search using
 * MongoDB 2dsphere geospatial indexing.</p>
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    /** {@inheritDoc} */
    @Override
    public UserResponse create(final UserRequest request) {
        log.info("Creating user profile — email: {}", request.email());

        if (repository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException(request.email());
        }

        final User user = mapper.toEntity(request);
        user.setActive(true);
        user.setAvgRating(0.0);
        user.setTotalRatings(0);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        final User saved = repository.save(user);
        log.info("User profile created — id: {}, email: {}", saved.getId(), saved.getEmail());
        return mapper.toResponse(saved);
    }

    /** {@inheritDoc} */
    @Override
    public UserResponse findById(final String id) {
        log.info("Finding user by id: {}", id);
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    /** {@inheritDoc} */
    @Override
    public UserResponse findByEmail(final String email) {
        log.info("Finding user by email: {}", email);
        return repository.findByEmail(email)
                .map(mapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException("email: " + email));
    }

    /** {@inheritDoc} */
    @Override
    public UserResponse update(final String id, final UserRequest request) {
        log.info("Updating user — id: {}", id);
        final User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        mapper.updateEntity(user, request);
        user.setUpdatedAt(Instant.now());

        final User updated = repository.save(user);
        log.info("User updated successfully — id: {}", updated.getId());
        return mapper.toResponse(updated);
    }

    /** {@inheritDoc} */
    @Override
    public void deactivate(final String id) {
        log.info("Deactivating user — id: {}", id);
        final User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setActive(false);
        user.setUpdatedAt(Instant.now());
        repository.save(user);
        log.info("User deactivated — id: {}", id);
    }

    /**
     * {@inheritDoc}
     *
     * <p><b>MongoDB ordering:</b> GeoJSON uses {@code [longitude, latitude]}.
     * The {@link Point} constructor also expects {@code (longitude, latitude)}.</p>
     */
    @Override
    public List<UserResponse> findNearby(final double latitude, final double longitude,
                                         final double radiusKm, final UserType type) {
        log.info("Proximity search — lat: {}, lng: {}, radius: {}km, type: {}",
                latitude, longitude, radiusKm, type);

        // MongoDB GeoJSON: Point(longitude, latitude)
        final Point center = new Point(longitude, latitude);
        final Distance distance = new Distance(radiusKm, Metrics.KILOMETERS);

        final List<User> results = (type != null)
                ? repository.findByLocationNearAndActiveTrueAndType(center, distance, type)
                : repository.findByLocationNearAndActiveTrue(center, distance);

        log.info("Proximity search returned {} results", results.size());
        return results.stream().map(mapper::toResponse).toList();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Called by {@code RatingCreatedConsumer} when a new rating is published.
     * If the contractor doesn't exist, the update is silently ignored.</p>
     */
    @Override
    public void updateRating(final String contractorId, final double avgRating, final int totalRatings) {
        log.info("Updating rating — contractorId: {}, avgRating: {}, totalRatings: {}",
                contractorId, avgRating, totalRatings);

        repository.findById(contractorId).ifPresentOrElse(user -> {
            user.setAvgRating(avgRating);
            user.setTotalRatings(totalRatings);
            user.setUpdatedAt(Instant.now());
            repository.save(user);
            log.info("Rating updated for contractor: {}", contractorId);
        }, () -> log.warn("Contractor not found for rating update — id: {}", contractorId));
    }
}
