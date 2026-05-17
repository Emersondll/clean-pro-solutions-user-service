package br.com.cleanprosolutions.user.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for registering a device token for push notifications.
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
public record DeviceTokenRequest(

        @NotBlank(message = "token is required")
        String token
) {}
