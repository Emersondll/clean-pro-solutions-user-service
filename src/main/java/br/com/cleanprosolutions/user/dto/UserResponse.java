package br.com.cleanprosolutions.user.dto;

import br.com.cleanprosolutions.user.enumerations.UserType;

import java.time.Instant;

/**
 * Response DTO representing a user profile.
 *
 * @param id           MongoDB generated unique identifier
 * @param name         full name
 * @param email        email address
 * @param phone        phone number
 * @param type         user type (CLIENT or CONTRACTOR)
 * @param address      physical address
 * @param latitude     geographic latitude (null if not set)
 * @param longitude    geographic longitude (null if not set)
 * @param avgRating    average rating score (0.0 if no ratings yet)
 * @param totalRatings total number of ratings received
 * @param active       whether the account is active
 * @param createdAt    profile creation timestamp
 */
public record UserResponse(
        String id,
        String name,
        String email,
        String phone,
        UserType type,
        AddressRequest address,
        Double latitude,
        Double longitude,
        double avgRating,
        int totalRatings,
        boolean active,
        Instant createdAt
) {}
