package br.com.cleanprosolutions.user.service.impl;

import br.com.cleanprosolutions.user.document.User;
import br.com.cleanprosolutions.user.dto.UserRequest;
import br.com.cleanprosolutions.user.dto.UserResponse;
import br.com.cleanprosolutions.user.enumerations.UserType;
import br.com.cleanprosolutions.user.exception.UserAlreadyExistsException;
import br.com.cleanprosolutions.user.exception.UserNotFoundException;
import br.com.cleanprosolutions.user.mapper.UserMapper;
import br.com.cleanprosolutions.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UserServiceImpl}.
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest request;
    private User user;
    private UserResponse response;

    @BeforeEach
    void setUp() {
        request = new UserRequest(
                "John Doe", "john@example.com", "+5511999999999",
                UserType.CONTRACTOR, "auth-id-001", null, -23.5, -46.6);

        user = User.builder()
                .id("user-id-001")
                .name("John Doe")
                .email("john@example.com")
                .phone("+5511999999999")
                .type(UserType.CONTRACTOR)
                .authId("auth-id-001")
                .active(true)
                .avgRating(0.0)
                .totalRatings(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        response = new UserResponse(
                "user-id-001", "John Doe", "john@example.com", "+5511999999999",
                UserType.CONTRACTOR, null, -23.5, -46.6, 0.0, 0, true, Instant.now());
    }

    @Test
    @DisplayName("shouldCreateUserWhenEmailIsNotTaken")
    void shouldCreateUserWhenEmailIsNotTaken() {
        when(repository.existsByEmail(request.email())).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(user);
        when(repository.save(any(User.class))).thenReturn(user);
        when(mapper.toResponse(user)).thenReturn(response);

        final UserResponse result = userService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("john@example.com");
        verify(repository).save(any(User.class));
    }

    @Test
    @DisplayName("shouldThrowUserAlreadyExistsWhenEmailIsTaken")
    void shouldThrowUserAlreadyExistsWhenEmailIsTaken() {
        when(repository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("john@example.com");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("shouldReturnUserWhenFoundById")
    void shouldReturnUserWhenFoundById() {
        when(repository.findById("user-id-001")).thenReturn(Optional.of(user));
        when(mapper.toResponse(user)).thenReturn(response);

        final UserResponse result = userService.findById("user-id-001");

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("user-id-001");
    }

    @Test
    @DisplayName("shouldThrowUserNotFoundWhenIdDoesNotExist")
    void shouldThrowUserNotFoundWhenIdDoesNotExist() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById("nonexistent-id"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("shouldReturnUserWhenFoundByEmail")
    void shouldReturnUserWhenFoundByEmail() {
        when(repository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(mapper.toResponse(user)).thenReturn(response);

        final UserResponse result = userService.findByEmail("john@example.com");

        assertThat(result.email()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("shouldThrowUserNotFoundWhenEmailDoesNotExist")
    void shouldThrowUserNotFoundWhenEmailDoesNotExist() {
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail("unknown@example.com"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("shouldUpdateUserWhenFound")
    void shouldUpdateUserWhenFound() {
        when(repository.findById("user-id-001")).thenReturn(Optional.of(user));
        when(repository.save(any(User.class))).thenReturn(user);
        when(mapper.toResponse(user)).thenReturn(response);

        final UserResponse result = userService.update("user-id-001", request);

        assertThat(result).isNotNull();
        verify(mapper).updateEntity(user, request);
        verify(repository).save(user);
    }

    @Test
    @DisplayName("shouldThrowUserNotFoundWhenUpdatingNonexistentUser")
    void shouldThrowUserNotFoundWhenUpdatingNonexistentUser() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update("nonexistent-id", request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("shouldDeactivateUserWhenFound")
    void shouldDeactivateUserWhenFound() {
        when(repository.findById("user-id-001")).thenReturn(Optional.of(user));

        userService.deactivate("user-id-001");

        assertThat(user.isActive()).isFalse();
        verify(repository).save(user);
    }

    @Test
    @DisplayName("shouldThrowUserNotFoundWhenDeactivatingNonexistentUser")
    void shouldThrowUserNotFoundWhenDeactivatingNonexistentUser() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deactivate("nonexistent-id"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("shouldUpdateRatingWhenContractorExists")
    void shouldUpdateRatingWhenContractorExists() {
        when(repository.findById("user-id-001")).thenReturn(Optional.of(user));

        userService.updateRating("user-id-001", 4.5, 10);

        assertThat(user.getAvgRating()).isEqualTo(4.5);
        assertThat(user.getTotalRatings()).isEqualTo(10);
        verify(repository).save(user);
    }

    @Test
    @DisplayName("shouldSilentlyIgnoreRatingUpdateWhenContractorNotFound")
    void shouldSilentlyIgnoreRatingUpdateWhenContractorNotFound() {
        when(repository.findById("nonexistent-id")).thenReturn(Optional.empty());

        userService.updateRating("nonexistent-id", 4.5, 10);

        verify(repository, never()).save(any());
    }
}
