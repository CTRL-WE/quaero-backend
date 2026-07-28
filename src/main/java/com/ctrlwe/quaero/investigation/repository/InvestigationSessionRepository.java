package com.ctrlwe.quaero.investigation.repository;

import com.ctrlwe.quaero.investigation.entity.InvestigationSession;
import com.ctrlwe.quaero.investigation.entity.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link InvestigationSession} entities.
 *
 * <p>All queries in this repository scope by {@code userId} to enforce
 * session ownership at the data-access layer. It is never permissible to
 * load a session by {@code caseId} alone — doing so would allow one user to
 * access another user's conversation.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public interface InvestigationSessionRepository
        extends JpaRepository<InvestigationSession, Long> {

    /**
     * Finds the single {@link SessionStatus#ACTIVE} session for a given
     * user/case pair, if one exists.
     *
     * <p>Because the business rule is "one active session per user per case",
     * this query will return at most one result. An {@link Optional#empty()}
     * result means the user has not yet started investigating this case, and
     * a new session should be created.</p>
     *
     * @param userId the ID of the authenticated user
     * @param caseId the ID of the case being investigated
     * @return an {@link Optional} containing the active session, or
     *         {@link Optional#empty()} if none exists
     */
    @Query("""
            SELECT s FROM InvestigationSession s
            WHERE s.userId = :userId
              AND s.caseId = :caseId
              AND s.status = :status
            """)
    Optional<InvestigationSession> findActiveSessionByUserAndCase(
            @Param("userId") Long userId,
            @Param("caseId") Long caseId,
            @Param("status") SessionStatus status);

    /**
     * Finds any session (regardless of status) for a given user/case pair.
     *
     * <p>Used by {@code getSessionStatus} to check whether a session exists
     * at all, regardless of whether it is ACTIVE or SUBMITTED. This is the
     * read path consumed by the future Submission module's existence check.</p>
     *
     * @param userId the ID of the authenticated user
     * @param caseId the ID of the case
     * @return an {@link Optional} containing the most recent session, or
     *         {@link Optional#empty()} if the user has never started this case
     */
    Optional<InvestigationSession> findByUserIdAndCaseId(Long userId, Long caseId);
}
