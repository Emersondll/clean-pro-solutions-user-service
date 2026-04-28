package br.com.cleanprosolutions.user.mapper;

import br.com.cleanprosolutions.user.document.Address;
import br.com.cleanprosolutions.user.document.User;
import br.com.cleanprosolutions.user.dto.AddressRequest;
import br.com.cleanprosolutions.user.dto.UserRequest;
import br.com.cleanprosolutions.user.dto.UserResponse;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;

/**
 * Mapper responsible for converting between {@link User} entities and DTOs.
 *
 * <p>All conversions are explicit — no reflection frameworks are used,
 * ensuring compile-time safety and clear intent.</p>
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@Component
public class UserMapper {

    /**
     * Converts a {@link UserRequest} to a new {@link User} entity.
     *
     * <p>The ID, avgRating, totalRatings, active, createdAt and updatedAt
     * fields are NOT set here — they are managed by the service layer.</p>
     *
     * @param request source DTO
     * @return a new User entity (without ID and audit fields)
     */
    public User toEntity(final UserRequest request) {
        final User.UserBuilder builder = User.builder()
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .type(request.type())
                .authId(request.authId())
                .address(toAddressEntity(request.address()));

        if (request.latitude() != null && request.longitude() != null) {
            // MongoDB GeoJSON Point: [longitude, latitude]
            builder.location(new GeoJsonPoint(request.longitude(), request.latitude()));
        }

        return builder.build();
    }

    /**
     * Converts a {@link User} entity to a {@link UserResponse} DTO.
     *
     * @param user source entity
     * @return the response DTO
     */
    public UserResponse toResponse(final User user) {
        Double latitude = null;
        Double longitude = null;

        if (user.getLocation() != null) {
            // GeoJsonPoint stores [longitude, latitude]
            longitude = user.getLocation().getX();
            latitude = user.getLocation().getY();
        }

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getType(),
                toAddressRequest(user.getAddress()),
                latitude,
                longitude,
                user.getAvgRating(),
                user.getTotalRatings(),
                user.isActive(),
                user.getCreatedAt()
        );
    }

    /**
     * Updates a mutable {@link User} entity from a {@link UserRequest} in place.
     *
     * @param user    the entity to update
     * @param request the request with new values
     */
    public void updateEntity(final User user, final UserRequest request) {
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setType(request.type());
        user.setAddress(toAddressEntity(request.address()));

        if (request.latitude() != null && request.longitude() != null) {
            user.setLocation(new GeoJsonPoint(request.longitude(), request.latitude()));
        }
    }

    // --- Private helpers ---

    private Address toAddressEntity(final AddressRequest dto) {
        if (dto == null) {
            return null;
        }
        return Address.builder()
                .street(dto.street())
                .number(dto.number())
                .complement(dto.complement())
                .neighborhood(dto.neighborhood())
                .city(dto.city())
                .state(dto.state())
                .zipCode(dto.zipCode())
                .build();
    }

    private AddressRequest toAddressRequest(final Address address) {
        if (address == null) {
            return null;
        }
        return new AddressRequest(
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getZipCode()
        );
    }
}
