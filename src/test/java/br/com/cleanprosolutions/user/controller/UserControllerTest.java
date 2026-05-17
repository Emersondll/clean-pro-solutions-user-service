package br.com.cleanprosolutions.user.controller;

import br.com.cleanprosolutions.user.dto.ContractorProfileRequest;
import br.com.cleanprosolutions.user.dto.DeviceTokenRequest;
import br.com.cleanprosolutions.user.dto.UserRequest;
import br.com.cleanprosolutions.user.dto.UserResponse;
import br.com.cleanprosolutions.user.enumerations.UserType;
import br.com.cleanprosolutions.user.exception.GlobalExceptionHandler;
import br.com.cleanprosolutions.user.exception.UserNotFoundException;
import br.com.cleanprosolutions.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link UserController}.
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final UserResponse USER_RESPONSE = new UserResponse(
            "user-001", "John Doe", "john@example.com", "+5511999999999",
            UserType.CONTRACTOR, null, -23.5, -46.6, 0.0, 0, true, Instant.now(), null);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // ─── POST /users → 201 ───────────────────────────────────────────────────

    @Test
    @DisplayName("POST /users → 201 when valid request")
    void shouldCreateUser() throws Exception {
        final UserRequest req = new UserRequest(
                "John Doe", "john@example.com", "+5511999999999",
                UserType.CONTRACTOR, "auth-001", null, -23.5, -46.6);
        when(userService.create(any(UserRequest.class))).thenReturn(USER_RESPONSE);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("user-001"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @DisplayName("POST /users → 400 when required fields are missing")
    void shouldReturn400WhenUserRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ─── GET /users/{id} ─────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /users/{id} → 200 when user exists")
    void shouldFindUserById() throws Exception {
        when(userService.findById("user-001")).thenReturn(USER_RESPONSE);

        mockMvc.perform(get("/users/user-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user-001"));
    }

    @Test
    @DisplayName("GET /users/{id} → 404 when user not found")
    void shouldReturn404WhenUserNotFound() throws Exception {
        when(userService.findById("missing")).thenThrow(new UserNotFoundException("missing"));

        mockMvc.perform(get("/users/missing"))
                .andExpect(status().isNotFound());
    }

    // ─── GET /users/email/{email} ─────────────────────────────────────────────

    @Test
    @DisplayName("GET /users/email/{email} → 200 when user exists")
    void shouldFindUserByEmail() throws Exception {
        when(userService.findByEmail("john@example.com")).thenReturn(USER_RESPONSE);

        mockMvc.perform(get("/users/email/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    // ─── PUT /users/{id} ─────────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /users/{id} → 200 when user updated")
    void shouldUpdateUser() throws Exception {
        final UserRequest req = new UserRequest(
                "Jane Doe", "jane@example.com", "+5511888888888",
                UserType.CLIENT, null, null, null, null);
        when(userService.update(eq("user-001"), any(UserRequest.class))).thenReturn(USER_RESPONSE);

        mockMvc.perform(put("/users/user-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    // ─── DELETE /users/{id} ──────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /users/{id} → 204 when user deactivated")
    void shouldDeactivateUser() throws Exception {
        doNothing().when(userService).deactivate("user-001");

        mockMvc.perform(delete("/users/user-001"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /users/{id} → 404 when user not found")
    void shouldReturn404WhenDeactivatingNonexistentUser() throws Exception {
        doThrow(new UserNotFoundException("missing")).when(userService).deactivate("missing");

        mockMvc.perform(delete("/users/missing"))
                .andExpect(status().isNotFound());
    }

    // ─── PUT /users/{id}/contractor-profile ──────────────────────────────────

    @Test
    @DisplayName("PUT /users/{id}/contractor-profile → 200")
    void shouldUpdateContractorProfile() throws Exception {
        final ContractorProfileRequest req = new ContractorProfileRequest(
                "Expert cleaner", List.of("deep cleaning"), List.of(), List.of());
        when(userService.updateContractorProfile(eq("user-001"), any(ContractorProfileRequest.class)))
                .thenReturn(USER_RESPONSE);

        mockMvc.perform(put("/users/user-001/contractor-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    // ─── POST /users/{id}/device-tokens ──────────────────────────────────────

    @Test
    @DisplayName("POST /users/{id}/device-tokens → 200")
    void shouldAddDeviceToken() throws Exception {
        final DeviceTokenRequest req = new DeviceTokenRequest("device-token-xyz");
        doNothing().when(userService).addDeviceToken(eq("user-001"), anyString());

        mockMvc.perform(post("/users/user-001/device-tokens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    // ─── GET /users/nearby ────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /users/nearby → 200 with results")
    void shouldFindNearbyUsers() throws Exception {
        when(userService.findNearby(-23.5, -46.6, 10.0, null))
                .thenReturn(List.of(USER_RESPONSE));

        mockMvc.perform(get("/users/nearby")
                        .param("lat", "-23.5")
                        .param("lng", "-46.6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("user-001"));
    }

    @Test
    @DisplayName("GET /users/nearby → 200 filtered by type")
    void shouldFindNearbyUsersFilteredByType() throws Exception {
        when(userService.findNearby(-23.5, -46.6, 5.0, UserType.CONTRACTOR))
                .thenReturn(List.of(USER_RESPONSE));

        mockMvc.perform(get("/users/nearby")
                        .param("lat", "-23.5")
                        .param("lng", "-46.6")
                        .param("radiusKm", "5.0")
                        .param("type", "CONTRACTOR"))
                .andExpect(status().isOk());
    }
}
