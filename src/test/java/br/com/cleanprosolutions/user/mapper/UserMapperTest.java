package br.com.cleanprosolutions.user.mapper;

import br.com.cleanprosolutions.user.document.Address;
import br.com.cleanprosolutions.user.document.User;
import br.com.cleanprosolutions.user.dto.AddressRequest;
import br.com.cleanprosolutions.user.dto.UserRequest;
import br.com.cleanprosolutions.user.dto.UserResponse;
import br.com.cleanprosolutions.user.enumerations.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link UserMapper}.
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserMapper();
    }

    // ─── toEntity ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("shouldMapRequestToEntityWithLocation")
    void shouldMapRequestToEntityWithLocation() {
        final AddressRequest addr = new AddressRequest("Rua A", "100", null, "Centro", "SP", "SP", "01000-000");
        final UserRequest req = new UserRequest(
                "John", "john@test.com", "+5511999", UserType.CONTRACTOR, "auth-1", addr, -23.5, -46.6);

        final User user = mapper.toEntity(req);

        assertThat(user.getName()).isEqualTo("John");
        assertThat(user.getEmail()).isEqualTo("john@test.com");
        assertThat(user.getType()).isEqualTo(UserType.CONTRACTOR);
        assertThat(user.getLocation()).isNotNull();
        assertThat(user.getAddress()).isNotNull();
        assertThat(user.getAddress().getCity()).isEqualTo("SP");
    }

    @Test
    @DisplayName("shouldMapRequestToEntityWithoutLocation")
    void shouldMapRequestToEntityWithoutLocation() {
        final UserRequest req = new UserRequest(
                "Jane", "jane@test.com", "+5511888", UserType.CLIENT, null, null, null, null);

        final User user = mapper.toEntity(req);

        assertThat(user.getLocation()).isNull();
        assertThat(user.getAddress()).isNull();
    }

    @Test
    @DisplayName("shouldMapRequestToEntityWithPartialLocation")
    void shouldMapRequestToEntityWithPartialLocation() {
        final UserRequest req = new UserRequest(
                "Bob", "bob@test.com", "+5511777", UserType.CLIENT, null, null, -23.5, null);

        final User user = mapper.toEntity(req);

        assertThat(user.getLocation()).isNull();
    }

    // ─── toResponse ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("shouldMapEntityToResponseWithLocation")
    void shouldMapEntityToResponseWithLocation() {
        final User user = User.builder()
                .id("u-1")
                .name("John")
                .email("john@test.com")
                .phone("+5511999")
                .type(UserType.CONTRACTOR)
                .location(new GeoJsonPoint(-46.6, -23.5))
                .avgRating(4.5)
                .totalRatings(10)
                .active(true)
                .createdAt(Instant.now())
                .build();

        final UserResponse response = mapper.toResponse(user);

        assertThat(response.id()).isEqualTo("u-1");
        assertThat(response.latitude()).isEqualTo(-23.5);
        assertThat(response.longitude()).isEqualTo(-46.6);
        assertThat(response.avgRating()).isEqualTo(4.5);
    }

    @Test
    @DisplayName("shouldMapEntityToResponseWithoutLocation")
    void shouldMapEntityToResponseWithoutLocation() {
        final User user = User.builder()
                .id("u-2")
                .name("Jane")
                .email("jane@test.com")
                .phone("+5511888")
                .type(UserType.CLIENT)
                .active(true)
                .createdAt(Instant.now())
                .build();

        final UserResponse response = mapper.toResponse(user);

        assertThat(response.latitude()).isNull();
        assertThat(response.longitude()).isNull();
    }

    @Test
    @DisplayName("shouldMapEntityToResponseWithAddress")
    void shouldMapEntityToResponseWithAddress() {
        final Address address = Address.builder()
                .street("Rua B").number("200").city("SP").state("SP").zipCode("02000-000").build();
        final User user = User.builder()
                .id("u-3").name("Ana").email("ana@test.com").phone("+5511").type(UserType.CLIENT)
                .address(address).active(true).createdAt(Instant.now()).build();

        final UserResponse response = mapper.toResponse(user);

        assertThat(response.address()).isNotNull();
        assertThat(response.address().city()).isEqualTo("SP");
    }

    // ─── updateEntity ────────────────────────────────────────────────────────

    @Test
    @DisplayName("shouldUpdateEntityFieldsFromRequest")
    void shouldUpdateEntityFieldsFromRequest() {
        final User user = User.builder().id("u-1").name("Old").email("old@test.com")
                .phone("+5511000").type(UserType.CLIENT).active(true).createdAt(Instant.now()).build();
        final UserRequest req = new UserRequest(
                "New Name", "new@test.com", "+5511111", UserType.CONTRACTOR, null, null, -23.5, -46.6);

        mapper.updateEntity(user, req);

        assertThat(user.getName()).isEqualTo("New Name");
        assertThat(user.getEmail()).isEqualTo("new@test.com");
        assertThat(user.getType()).isEqualTo(UserType.CONTRACTOR);
        assertThat(user.getLocation()).isNotNull();
    }

    @Test
    @DisplayName("shouldUpdateEntityWithoutLocationWhenCoordinatesAreNull")
    void shouldUpdateEntityWithoutLocationWhenCoordinatesAreNull() {
        final User user = User.builder().id("u-1").name("Old").email("old@test.com")
                .phone("+5511000").type(UserType.CLIENT).active(true).createdAt(Instant.now()).build();
        final UserRequest req = new UserRequest(
                "New Name", "new@test.com", "+5511111", UserType.CLIENT, null, null, null, null);

        mapper.updateEntity(user, req);

        assertThat(user.getLocation()).isNull();
    }
}
