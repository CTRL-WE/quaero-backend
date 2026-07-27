package com.ctrlwe.quaero.user;

import com.ctrlwe.quaero.common.response.ApiErrorResponse;
import com.ctrlwe.quaero.common.response.ApiResponse;
import com.ctrlwe.quaero.user.dto.UpdateProfileRequest;
import com.ctrlwe.quaero.user.dto.UserProfileResponse;
import com.ctrlwe.quaero.security.CurrentUserResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for user profile operations.
 *
 * <p>Exposes exactly two endpoints:</p>
 * <ul>
 *   <li>{@code GET /api/users/me} — retrieve the authenticated user's profile</li>
 *   <li>{@code PUT /api/users/me} — update the authenticated user's profile</li>
 * </ul>
 *
 * <p>Both endpoints require a valid JWT in the
 * {@code Authorization: Bearer <token>} header. Authentication is
 * enforced by the platform-wide {@code SecurityConfig} — no security
 * code lives in this controller.</p>
 *
 * <p>No business logic is performed in this controller. All operations
 * are delegated to {@link UserService}. Responses are wrapped in
 * {@link ApiResponse} for consistency with the rest of the platform.</p>
 *
 * <p>No admin endpoints are exposed here.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile management endpoints.")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final CurrentUserResolver currentUserResolver;

    /**
     * Returns the authenticated user's full profile.
     *
     * <p>The user is resolved from the Spring Security context.
     * No path variable or query parameter is needed — the endpoint
     * always returns the profile of the currently authenticated user.</p>
     *
     * @return HTTP 200 with the user's profile
     */
    @GetMapping("/me")
    @Operation(
        summary = "Get current user profile",
        description = "Returns the full profile of the currently authenticated user, " +
                      "including username, email, full name, bio, profile picture URL, " +
                      "role, account status, reputation score, and timestamps."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Profile returned successfully.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserProfileResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Missing or invalid JWT.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found — the authenticated principal does not map to an existing user.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile() {
        Long userId = resolveCurrentUserId();
        log.debug("GET /api/users/me — userId={}", userId);

        UserProfileResponse profile = userService.getProfile(userId);

        return ResponseEntity.ok(
                ApiResponse.success(profile, "Profile retrieved successfully"));
    }

    /**
     * Updates the authenticated user's profile.
     *
     * <p>Only non-{@code null} fields in the request body are applied,
     * allowing partial updates. Sensitive fields (email, username,
     * password) are not modifiable through this endpoint.</p>
     *
     * @param request the update payload
     * @return HTTP 200 with the updated profile
     */
    @PutMapping("/me")
    @Operation(
        summary = "Update current user profile",
        description = "Updates the profile of the currently authenticated user. " +
                      "Only the fields provided in the request body are updated " +
                      "(partial update). Supported fields: fullName, profilePictureUrl, bio."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Profile updated successfully.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserProfileResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation failed on one or more fields.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Missing or invalid JWT.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found — the authenticated principal does not map to an existing user.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request) {

        Long userId = resolveCurrentUserId();
        log.debug("PUT /api/users/me — userId={}", userId);

        UserProfileResponse updatedProfile = userService.updateProfile(userId, request);

        return ResponseEntity.ok(
                ApiResponse.success(updatedProfile, "Profile updated successfully"));
    }

    // ----------------------------------------------------------------
    // Utility — delegated to shared CurrentUserResolver
    // ----------------------------------------------------------------

    private Long resolveCurrentUserId() {
        return currentUserResolver.resolveCurrentUserId();
    }
}
