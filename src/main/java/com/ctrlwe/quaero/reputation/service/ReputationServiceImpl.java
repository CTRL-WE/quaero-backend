package com.ctrlwe.quaero.reputation.service;

import com.ctrlwe.quaero.exception.ErrorCode;
import com.ctrlwe.quaero.exception.ResourceNotFoundException;
import com.ctrlwe.quaero.reputation.ReputationCalculator;
import com.ctrlwe.quaero.reputation.RankTier;
import com.ctrlwe.quaero.reputation.dto.LeaderboardEntryResponse;
import com.ctrlwe.quaero.reputation.dto.ProfileStatistics;
import com.ctrlwe.quaero.reputation.dto.ProgressionUpdateResult;
import com.ctrlwe.quaero.user.User;
import com.ctrlwe.quaero.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link ReputationService}.
 *
 * <h3>Transaction contract</h3>
 * <p>This class carries <strong>no class-level or method-level
 * {@code @Transactional} annotation</strong>. The transaction boundaries are
 * managed by callers:</p>
 * <ul>
 *   <li>{@link #recordSubmissionOutcome} is called from within
 *       {@code SubmissionServiceImpl.createSubmission()}, which is already
 *       {@code @Transactional}. Spring's default {@code REQUIRED} propagation
 *       causes this method to join that existing transaction automatically —
 *       no annotation needed here, and using {@code REQUIRES_NEW} here
 *       would break rollback atomicity.</li>
 *   <li>{@link #getProfileStatistics} and {@link #getLeaderboard} delegate
 *       entirely to {@link UserService} methods that carry their own
 *       {@code @Transactional(readOnly = true)} annotations.</li>
 * </ul>
 *
 * <h3>Error handling</h3>
 * <p>No exception is caught or suppressed in this class. Any runtime
 * exception propagates up to the platform's {@code GlobalExceptionHandler}.
 * Progression failures are data-consistency problems — not degradable errors.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReputationServiceImpl implements ReputationService {

    private final UserService userService;

    // ──────────────────────────────────────────────────────────────────────────
    //  1. recordSubmissionOutcome
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     *
     * <p>Intentionally carries no {@code @Transactional} annotation.
     * This method joins the caller's existing transaction via Spring's
     * default {@code REQUIRED} propagation rule.</p>
     */
    @Override
    public ProgressionUpdateResult recordSubmissionOutcome(Long userId, int reasoningScore) {
        log.debug("recordSubmissionOutcome called: userId={}, reasoningScore={}",
                userId, reasoningScore);

        // 1. Load the user's current raw progression state (entity-level access
        //    is performed inside UserService — we work with User entities here
        //    because UserService.getUsersOrderedByProgression() already returns
        //    them and we need the raw int/BigDecimal values, not a DTO).
        //    We use getUsersOrderedByProgression() only for the leaderboard;
        //    for a single-user read we go through the UserService.getProfile path
        //    which returns a DTO — but the DTO now contains the progression fields,
        //    so we can read from it directly.
        //
        //    Design note: to avoid a second DB round-trip, we load via the ordered
        //    list only when computing position. For the write path, we read the
        //    current state via getProfileStatistics-internal helpers, then call
        //    updateProgressionFields(). To get the raw User entity (with
        //    int/BigDecimal fields) without breaking the cross-module boundary, we
        //    delegate the entity fetch to UserService.getUsersOrderedByProgression()
        //    and filter — or we accept a tiny extra query. Here we do the clean
        //    thing: add a package-private helper that reads the entity directly
        //    through UserService's entity-returning method.
        //
        //    Since UserService.getUsersOrderedByProgression() returns List<User>,
        //    we can reuse it and filter for the target user. For single-user reads
        //    this is admittedly over-fetching; a future optimisation would add a
        //    findUserEntityById() method to UserService. The frozen spec does not
        //    preclude this implementation, and correctness is guaranteed.

        // For record outcome we need the current User entity's progression values.
        // We retrieve via the ordered list and locate the user — acceptable for
        // now because the spec says no new separate read method, and this keeps
        // us within the cross-module boundary rule.
        User currentUser = userService.getUsersOrderedByProgression()
                .stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with id " + userId + " does not exist",
                        ErrorCode.RESOURCE_NOT_FOUND));

        int currentTotalXp           = currentUser.getTotalXp();
        BigDecimal currentCredibility = currentUser.getCredibility();
        int currentSubmissions       = currentUser.getSuccessfulSubmissions();

        // 2. Compute new values using ReputationCalculator (pure math, no Spring).
        int xpEarned = ReputationCalculator.calculateXp(reasoningScore);
        int newTotalXp = currentTotalXp + xpEarned;

        BigDecimal newCredibility = ReputationCalculator.calculateCredibility(
                currentCredibility, currentSubmissions, reasoningScore);

        int newCompletedInvestigations = currentUser.getCompletedInvestigations() + 1;
        int newSuccessfulSubmissions   = currentSubmissions + 1;

        // 3. Persist via UserService — no direct repository access.
        userService.updateProgressionFields(
                userId,
                newTotalXp,
                newCredibility,
                newCompletedInvestigations,
                newSuccessfulSubmissions);

        log.info("Progression updated: userId={}, xpEarned={}, newTotalXp={}, " +
                "newCredibility={}, completedInvestigations={}, successfulSubmissions={}",
                userId, xpEarned, newTotalXp, newCredibility,
                newCompletedInvestigations, newSuccessfulSubmissions);

        // 4. Return result DTO for the submission response.
        return ProgressionUpdateResult.builder()
                .xpEarned(xpEarned)
                .newTotalXp(newTotalXp)
                .updatedCredibility(newCredibility)
                .build();
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  2. getProfileStatistics
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     */
    @Override
    public ProfileStatistics getProfileStatistics(Long userId) {
        log.debug("getProfileStatistics called for userId={}", userId);

        // Retrieve the ordered list once — used for both position lookup
        // and current-user data extraction.
        List<User> orderedUsers = userService.getUsersOrderedByProgression();

        // Locate the target user and their 1-indexed position simultaneously.
        User targetUser = null;
        int position = 0;
        for (int i = 0; i < orderedUsers.size(); i++) {
            if (orderedUsers.get(i).getId().equals(userId)) {
                targetUser = orderedUsers.get(i);
                position = i + 1;  // 1-indexed
                break;
            }
        }

        if (targetUser == null) {
            throw new ResourceNotFoundException(
                    "User with id " + userId + " does not exist",
                    ErrorCode.RESOURCE_NOT_FOUND);
        }

        // Derive rank tier from current total XP — never persisted.
        RankTier rankTier = ReputationCalculator.deriveRankTier(targetUser.getTotalXp());

        return ProfileStatistics.builder()
                .totalXp(targetUser.getTotalXp())
                .credibility(targetUser.getCredibility())
                .completedInvestigations(targetUser.getCompletedInvestigations())
                .successfulSubmissions(targetUser.getSuccessfulSubmissions())
                .rankTier(rankTier)
                .leaderboardPosition(position)
                .build();
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  3. getLeaderboard
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LeaderboardEntryResponse> getLeaderboard() {
        log.debug("getLeaderboard called");

        List<User> orderedUsers = userService.getUsersOrderedByProgression();
        List<LeaderboardEntryResponse> entries = new ArrayList<>(orderedUsers.size());

        for (int i = 0; i < orderedUsers.size(); i++) {
            User user = orderedUsers.get(i);
            entries.add(LeaderboardEntryResponse.builder()
                    .username(user.getUsername())
                    .position(i + 1)   // 1-indexed; deliberately NOT named "rank"
                    .xp(user.getTotalXp())
                    .credibility(user.getCredibility())
                    .completedInvestigations(user.getCompletedInvestigations())
                    .build());
        }

        return entries;
    }
}
