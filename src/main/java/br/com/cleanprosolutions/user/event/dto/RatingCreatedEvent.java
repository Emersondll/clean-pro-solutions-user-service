package br.com.cleanprosolutions.user.event.dto;

import java.time.Instant;

/**
 * Immutable event DTO for the {@code RatingCreated} message from the {@code rating-service}.
 *
 * <p>Contains the pre-calculated aggregated rating to avoid re-computation.</p>
 *
 * @param eventId       unique event identifier (idempotency key)
 * @param contractorId  the rated contractor's user ID
 * @param avgRating     updated average rating (1.0–5.0)
 * @param totalRatings  updated total number of ratings
 * @param timestamp     event creation timestamp (UTC)
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
public record RatingCreatedEvent(
        String eventId,
        String contractorId,
        double avgRating,
        int totalRatings,
        Instant timestamp
) {}
