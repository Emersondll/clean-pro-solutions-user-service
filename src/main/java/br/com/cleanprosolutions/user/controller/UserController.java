package br.com.cleanprosolutions.user.controller;

import br.com.cleanprosolutions.user.dto.UserRequest;
import br.com.cleanprosolutions.user.dto.UserResponse;
import br.com.cleanprosolutions.user.enumerations.UserType;
import br.com.cleanprosolutions.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for user profile management.
 *
 * <p>Provides endpoints for CRUD operations and geospatial proximity search.</p>
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile management and proximity search")
public class UserController {

    private final UserService userService;

    /**
     * Creates a new user profile.
     *
     * @param request user data
     * @return 201 Created with the new user profile
     */
    @PostMapping
    @Operation(summary = "Create a new user profile")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    public ResponseEntity<UserResponse> create(@Valid @RequestBody final UserRequest request) {
        log.info("POST /users — email: {}", request.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    /**
     * Retrieves a user profile by ID.
     *
     * @param id user MongoDB ID
     * @return 200 OK with user profile
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> findById(@PathVariable final String id) {
        log.info("GET /users/{}", id);
        return ResponseEntity.ok(userService.findById(id));
    }

    /**
     * Retrieves a user profile by email.
     *
     * @param email user email address
     * @return 200 OK with user profile
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email address")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> findByEmail(@PathVariable final String email) {
        log.info("GET /users/email/{}", email);
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    /**
     * Updates an existing user profile.
     *
     * @param id      user MongoDB ID
     * @param request updated user data
     * @return 200 OK with updated profile
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update user profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<UserResponse> update(
            @PathVariable final String id,
            @Valid @RequestBody final UserRequest request) {
        log.info("PUT /users/{}", id);
        return ResponseEntity.ok(userService.update(id, request));
    }

    /**
     * Deactivates a user account (soft delete).
     *
     * @param id user MongoDB ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate user account (soft delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deactivated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deactivate(@PathVariable final String id) {
        log.info("DELETE /users/{}", id);
        userService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Finds active users near a geographic point.
     *
     * <p>Uses MongoDB 2dsphere index for efficient proximity search.
     * Results are sorted by distance (nearest first).</p>
     *
     * @param lat      center latitude
     * @param lng      center longitude
     * @param radiusKm search radius in kilometers (default: 10)
     * @param type     optional filter by user type (CLIENT or CONTRACTOR)
     * @return 200 OK with list of nearby users sorted by distance
     */
    @GetMapping("/nearby")
    @Operation(summary = "Find active users near a location",
            description = "Uses MongoDB 2dsphere geospatial index. Results sorted by distance.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nearby users returned"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    public ResponseEntity<List<UserResponse>> findNearby(
            @RequestParam final double lat,
            @RequestParam final double lng,
            @RequestParam(defaultValue = "10") final double radiusKm,
            @RequestParam(required = false) final UserType type) {
        log.info("GET /users/nearby — lat: {}, lng: {}, radius: {}km, type: {}", lat, lng, radiusKm, type);
        return ResponseEntity.ok(userService.findNearby(lat, lng, radiusKm, type));
    }
}
