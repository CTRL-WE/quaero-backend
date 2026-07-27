package com.ctrlwe.quaero.submission.repository;

import com.ctrlwe.quaero.submission.entity.Submission;
import com.ctrlwe.quaero.submission.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Submission} entities.
 *
 * <p>Provides standard CRUD operations inherited from
 * {@link JpaRepository} plus custom finder methods for the most
 * common query patterns in the Submission module.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    /**
     * Retrieves all submissions for the given case, ordered by
     * creation date descending (newest first).
     *
     * @param caseId the ID of the case to filter by
     * @return an ordered list of submissions for the specified case
     */
    List<Submission> findAllByCaseEntityIdOrderByCreatedAtDesc(Long caseId);

    /**
     * Retrieves all submissions by the given user, ordered by
     * creation date descending (newest first).
     *
     * @param userId the ID of the user to filter by
     * @return an ordered list of submissions for the specified user
     */
    List<Submission> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Retrieves all submissions with the specified lifecycle status.
     *
     * @param status the {@link SubmissionStatus} to filter by
     * @return a list of submissions matching the given status
     */
    List<Submission> findByStatus(SubmissionStatus status);
}
