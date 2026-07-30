package com.ctrlwe.quaero.user;

import com.ctrlwe.quaero.reputation.dto.ProfileStatistics;
import com.ctrlwe.quaero.user.dto.CreateUserRequest;
import com.ctrlwe.quaero.user.dto.UserProfileResponse;
import com.ctrlwe.quaero.user.dto.UserSummaryResponse;
import org.springframework.stereotype.Component;

/**
 * Maps between {@link User} entities and user-related DTOs.
 *
 * <p>All mapping is performed manually, field-by-field. No generic
 * object mapper (ModelMapper, MapStruct, BeanUtils) is used — this
 * ensures that sensitive fields such as {@code password} are never
 * accidentally included in any response DTO.</p>
 *
 * <p>If you add a field to a response DTO, you <strong>must</strong>
 * add a corresponding mapping line in this class. There is no
 * automatic copy path that could silently include a field.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Component
public class UserMapper {

    /**
     * Maps a {@link User} entity to a {@link UserProfileResponse}.
     *
     * <p>Includes all user-visible profile fields. The {@code password}
     * field is intentionally excluded.</p>
     *
     * @param user the user entity to map
     * @return a fully populated {@link UserProfileResponse}
     */
    public UserProfileResponse toProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .bio(user.getBio())
                .role(user.getRole())
                .accountStatus(user.getAccountStatus())
                .reputationScore(user.getReputationScore())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                // Progression fields persisted on the entity:
                .totalXp(user.getTotalXp())
                .credibility(user.getCredibility())
                .completedInvestigations(user.getCompletedInvestigations())
                .successfulSubmissions(user.getSuccessfulSubmissions())
                // rankTier and leaderboardPosition require a full user-list scan;
                // they are populated by toProfileResponseWithStats() when requested
                // from the profile endpoint.
                .build();
    }

    /**
     * Maps a {@link User} entity to a {@link UserProfileResponse}, enriched with
     * the two computed fields ({@code rankTier}, {@code leaderboardPosition}) that
     * cannot be derived from the entity alone.
     *
     * <p>Called exclusively from {@link com.ctrlwe.quaero.user.UserController#getMyProfile()}
     * where the controller also calls
     * {@link com.ctrlwe.quaero.reputation.service.ReputationService#getProfileStatistics(Long)}
     * and passes the result here.
     *
     * @param user  the user entity
     * @param stats the profile statistics (rank tier + leaderboard position)
     * @return a fully populated {@link UserProfileResponse} including all 6 new fields
     */
    public UserProfileResponse toProfileResponseWithStats(User user, ProfileStatistics stats) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .bio(user.getBio())
                .role(user.getRole())
                .accountStatus(user.getAccountStatus())
                .reputationScore(user.getReputationScore())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .totalXp(stats.getTotalXp())
                .credibility(stats.getCredibility())
                .completedInvestigations(stats.getCompletedInvestigations())
                .successfulSubmissions(stats.getSuccessfulSubmissions())
                .rankTier(stats.getRankTier())
                .leaderboardPosition(stats.getLeaderboardPosition())
                .build();
    }

    /**
     * Maps a {@link User} entity to a lightweight {@link UserSummaryResponse}.
     *
     * <p>Contains only the minimal fields needed for display in lists,
     * cards, and cross-module references.</p>
     *
     * @param user the user entity to map
     * @return a populated {@link UserSummaryResponse}
     */
    public UserSummaryResponse toSummaryResponse(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .profilePictureUrl(user.getProfilePictureUrl())
                .reputationScore(user.getReputationScore())
                .build();
    }

    /**
     * Maps a {@link CreateUserRequest} to a new {@link User} entity.
     *
     * <p>The {@code password} field in the returned entity contains
     * the <strong>plain-text</strong> password from the request.
     * Callers are responsible for encoding it before persistence.</p>
     *
     * <p>Default values for {@code role} ({@link Role#USER}),
     * {@code accountStatus} ({@link AccountStatus#ACTIVE}), and
     * {@code reputationScore} (0) are set by the {@link User} entity's
     * {@code @Builder.Default} annotations.</p>
     *
     * @param request the creation request DTO
     * @return a new, unpersisted {@link User} entity
     */
    public User toEntity(CreateUserRequest request) {
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(request.getPassword())
                .fullName(request.getFullName())
                .build();
    }
}
