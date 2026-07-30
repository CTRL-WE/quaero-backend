package com.ctrlwe.quaero.reputation.service;

import com.ctrlwe.quaero.reputation.dto.LeaderboardEntryResponse;
import com.ctrlwe.quaero.reputation.dto.ProfileStatistics;
import com.ctrlwe.quaero.reputation.dto.ProgressionUpdateResult;

import java.util.List;

/**
 * Service interface for the Reputation &amp; Progression system.
 *
 * <p>This interface is the <strong>sole owner</strong> of all XP and Credibility
 * calculation and write logic on the platform. No other module, service, or class
 * may calculate or write XP or Credibility — all such operations must go through
 * this interface.</p>
 *
 * <p>Persistence is delegated through
 * {@link com.ctrlwe.quaero.user.UserService} — this service never reaches
 * a repository directly.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public interface ReputationService {

    /**
     * Records the outcome of an accepted submission and updates the user's
     * progression state atomically within the caller's transaction.
     *
     * <h3>Steps performed</h3>
     * <ol>
     *   <li>Reads the user's current progression fields via
     *       {@link com.ctrlwe.quaero.user.UserService}.</li>
     *   <li>Computes the XP earned for this submission via
     *       {@link com.ctrlwe.quaero.reputation.ReputationCalculator#calculateXp(int)}.</li>
     *   <li>Computes the new credibility via
     *       {@link com.ctrlwe.quaero.reputation.ReputationCalculator#calculateCredibility}.</li>
     *   <li>Increments {@code completedInvestigations} and
     *       {@code successfulSubmissions} by 1.</li>
     *   <li>Persists all four updated fields via
     *       {@link com.ctrlwe.quaero.user.UserService#updateProgressionFields}.</li>
     *   <li>Returns a {@link ProgressionUpdateResult} for use by the
     *       submission response.</li>
     * </ol>
     *
     * <h3>Transaction contract</h3>
     * <p>This method runs under default ({@code REQUIRED}) propagation and
     * <strong>must</strong> join the caller's existing transaction. It must
     * <strong>never</strong> be annotated with {@code REQUIRES_NEW}. If the
     * enclosing submission transaction rolls back, this progression write
     * rolls back with it.</p>
     *
     * <h3>Error handling</h3>
     * <p>No exception is caught or suppressed here. Any failure propagates
     * to the platform's {@code GlobalExceptionHandler} — progression-write
     * failures are data-consistency problems, not degradable errors.</p>
     *
     * @param userId         the ID of the submitting user
     * @param reasoningScore the AI-assigned reasoning score for this submission
     * @return a {@link ProgressionUpdateResult} carrying xpEarned, newTotalXp,
     *         and updatedCredibility
     * @throws com.ctrlwe.quaero.exception.ResourceNotFoundException if the
     *         user does not exist
     */
    ProgressionUpdateResult recordSubmissionOutcome(Long userId, int reasoningScore);

    /**
     * Assembles the full progression statistics for a user's profile view.
     *
     * <p>Reads current progression fields via
     * {@link com.ctrlwe.quaero.user.UserService}, derives the
     * {@link com.ctrlwe.quaero.reputation.RankTier} from total XP, and
     * determines the leaderboard position by locating the user's index
     * within the ordered list from
     * {@link com.ctrlwe.quaero.user.UserService#getUsersOrderedByProgression()}.</p>
     *
     * @param userId the ID of the user whose statistics to assemble
     * @return a fully populated {@link ProfileStatistics}, never {@code null}
     * @throws com.ctrlwe.quaero.exception.ResourceNotFoundException if the
     *         user does not exist
     */
    ProfileStatistics getProfileStatistics(Long userId);

    /**
     * Returns the full leaderboard as an ordered list of entries.
     *
     * <p>Ordering: credibility DESC (nulls last), then XP DESC.
     * Positions are 1-indexed. No pagination, no filtering — the full list
     * is returned in one response.</p>
     *
     * @return the ordered list of {@link LeaderboardEntryResponse} entries;
     *         never {@code null}, may be empty
     */
    List<LeaderboardEntryResponse> getLeaderboard();
}
