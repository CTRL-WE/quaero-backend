package com.ctrlwe.quaero.investigation.repository;

import com.ctrlwe.quaero.investigation.entity.ConversationTurn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link ConversationTurn} entities.
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public interface ConversationTurnRepository extends JpaRepository<ConversationTurn, Long> {

    /**
     * Returns all conversation turns for the given session, ordered by
     * {@code createdAt} ascending (oldest turn first).
     *
     * <p>This ordering is critical: the list is used to replay the full
     * conversation history in chronological order when assembling the
     * prompt sent to the AI Mentor.</p>
     *
     * @param sessionId the ID of the parent session
     * @return an ordered list of turns; empty list if none exist yet
     */
    List<ConversationTurn> findBySessionIdOrderByCreatedAt(Long sessionId);
}
