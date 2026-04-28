package br.com.cleanprosolutions.user.dto;

import br.com.cleanprosolutions.user.enumerations.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for creating or updating a user profile.
 *
 * @param name      full name (required)
 * @param email     unique email address (required, valid format)
 * @param phone     phone number (required)
 * @param type      user type: CLIENT or CONTRACTOR (required)
 * @param authId    reference ID from auth-service (optional, set during registration flow)
 * @param address   physical address (optional)
 * @param latitude  geographic latitude — required for CONTRACTOR location
 * @param longitude geographic longitude — required for CONTRACTOR location
 */
public record UserRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Phone is required")
        String phone,

        @NotNull(message = "User type is required")
        UserType type,

        String authId,

        AddressRequest address,

        Double latitude,

        Double longitude
) {}
