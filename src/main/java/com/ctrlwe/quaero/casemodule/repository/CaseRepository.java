package com.ctrlwe.quaero.casemodule.repository;

import com.ctrlwe.quaero.casemodule.entity.Case;
import com.ctrlwe.quaero.casemodule.entity.CaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Case} entities.
 *
 * <p>Exposes only the two read operations required by this module.
 * {@code findById(Long)} is inherited from {@link JpaRepository} and is
 * used by {@code CaseServiceImpl#getBrief} and
 * {@code CaseServiceImpl#getFullContext}.</p>
 *
 * <p>No save/update/delete methods are declared here beyond what
 * {@link JpaRepository} provides by default. Those inherited methods
 * exist solely to support the Flyway seed-data pathway (the
 * {@code CommandLineRunner} in {@code CaseSeedRunner}) and must never
 * be called from any public-facing service path.</p>
 *
 * <p><strong>Cross-module access rule:</strong> No module outside
 * {@code com.ctrlwe.quaero.casemodule} may inject or call this
 * repository directly. Downstream modules (Investigation,
 * Submission &amp; Evaluation) must use
 * {@code CaseService#getFullContext(Long)} exclusively.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Repository
public interface CaseRepository extends JpaRepository<Case, Long> {

    /**
     * Returns all cases whose status equals {@link CaseStatus#PUBLISHED},
     * ordered by creation date descending (newest first on the Feed).
     *
     * @param status the status to filter by — callers must pass
     *               {@link CaseStatus#PUBLISHED}
     * @return an unmodifiable list of published cases; never {@code null}
     */
    List<Case> findAllByStatusOrderByCreatedAtDesc(CaseStatus status);
}
