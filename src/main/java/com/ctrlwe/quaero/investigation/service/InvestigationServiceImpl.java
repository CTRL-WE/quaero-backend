package com.ctrlwe.quaero.investigation.service;

import com.ctrlwe.quaero.ai.dto.AiPromptResult;
import com.ctrlwe.quaero.ai.dto.AiRequest;
import com.ctrlwe.quaero.ai.service.AiClientService;
import com.ctrlwe.quaero.ai.util.PromptBuilder;
import com.ctrlwe.quaero.casemodule.dto.CaseInternalContext;
import com.ctrlwe.quaero.casemodule.service.CaseService;
import com.ctrlwe.quaero.investigation.dto.ChatMessageResponse;
import com.ctrlwe.quaero.investigation.dto.SessionStatusResponse;
import com.ctrlwe.quaero.investigation.entity.ConversationTurn;
import com.ctrlwe.quaero.investigation.entity.InvestigationSession;
import com.ctrlwe.quaero.investigation.entity.SessionStatus;
import com.ctrlwe.quaero.investigation.entity.TurnSender;
import com.ctrlwe.quaero.investigation.exception.SessionAlreadySubmittedException;
import com.ctrlwe.quaero.investigation.repository.ConversationTurnRepository;
import com.ctrlwe.quaero.investigation.repository.InvestigationSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of {@link InvestigationService}.
 *
 * <h3>Scope — Investigation v1</h3>
 * <p>This implementation deliberately covers only the v1 scope: session
 * find-or-create, single-turn Socratic exchange, turn counting, and a
 * temporary stopgap for {@link AiServiceException}. The following features
 * are explicitly deferred to Investigation v2 and must NOT be added here
 * until their upstream dependencies are confirmed ready:</p>
 * <ul>
 *   <li>Soft-nudge signal ({@code nudgeSubmission=true}) — depends on
 *       Vishwa's AI Client mode discriminator + degraded-result contract.</li>
 *   <li>Hard turn ceiling enforcement — same dependency.</li>
 *   <li>Real fallback prompt library — belongs in {@code ai/}, not here.</li>
 * </ul>
 *
 * <h3>PromptBuilder usage — 3-layer architecture</h3>
 * <p>{@link PromptBuilder#buildSocraticRequest} builds a fully-formed
 * {@link AiRequest} with Gemini's native {@code system_instruction}
 * field, a case context block as the first {@code user} message, and
 * the conversation history as alternating {@code user}/{@code model}
 * turns in the {@code contents[]} array. This method maps the
 * Investigation module's {@link ConversationTurn} entities to
 * {@link PromptBuilder.TurnEntry} records to keep PromptBuilder
 * decoupled from Investigation entities.</p>
 *
 * <p><strong>Ground truth isolation:</strong> Only {@code claim} and
 * {@code investigationHints} from {@link CaseInternalContext} are included
 * in the prompt. {@code groundTruth}, {@code groundTruthExplanation},
 * {@code trustedReferences}, and {@code learningSummary} are intentionally
 * excluded. If you are modifying this method and are tempted to include
 * additional fields, stop and consult the MIL Platform Backend Handbook
 * (Module 2: Information Integrity) before proceeding.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InvestigationServiceImpl implements InvestigationService {

    // ----------------------------------------------------------------
    // Investigation v1 constants
    // ----------------------------------------------------------------

    /**
     * Hard turn ceiling. When a session reaches this many turns the AI
     * Client is no longer called; instead a gentle closing message is
     * returned and persisted. The ceiling is silent — no error, no
     * visible limit indicator is shown to the user.
     */
    private static final int MAX_TURNS = 12;

    /**
     * Nudge threshold. Once {@code turnCount >= NUDGE_THRESHOLD} the
     * response DTO sets {@code nudgeSubmission=true} to signal the
     * frontend to prompt the user to submit their verdict.
     */
    private static final int NUDGE_THRESHOLD = 4;

    /**
     * Graceful closing message returned when the hard ceiling is reached.
     * Written in an encouraging, Socratic tone — does not state a verdict,
     * does not reveal that a limit was reached.
     */
    private static final String CEILING_REPLY =
            "You've explored this topic thoroughly. It sounds like you've built a solid "
            + "foundation for your conclusion. When you're ready, I'd encourage you to "
            + "put your thoughts together and submit your verdict — you can do so from "
            + "the case page.";

    // ----------------------------------------------------------------
    // Dependencies
    // ----------------------------------------------------------------

    private final InvestigationSessionRepository sessionRepository;
    private final ConversationTurnRepository turnRepository;
    private final CaseService caseService;
    private final AiClientService aiClientService;

    // ----------------------------------------------------------------
    // Service methods
    // ----------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * <p>Looks for an ACTIVE session for the given user/case pair. If one
     * exists, it is returned as-is. If none exists, a new session is created,
     * persisted, and returned. The find-or-create is performed within a single
     * transaction to prevent a race-condition window where two concurrent
     * requests could each observe "no session exists" and both proceed to
     * insert — the DB unique constraint on {@code (user_id, case_id)} is
     * the final safety net in that edge case.</p>
     */
    @Override
    @Transactional
    public InvestigationSession startOrResumeSession(Long userId, Long caseId) {
        log.debug("startOrResumeSession: userId={}, caseId={}", userId, caseId);

        Optional<InvestigationSession> existing = sessionRepository
                .findActiveSessionByUserAndCase(userId, caseId, SessionStatus.ACTIVE);

        if (existing.isPresent()) {
            log.debug("Resuming existing session id={}", existing.get().getId());
            return existing.get();
        }

        InvestigationSession newSession = new InvestigationSession(
                userId, caseId, SessionStatus.ACTIVE, LocalDateTime.now());
        InvestigationSession saved = sessionRepository.save(newSession);
        log.info("Created new investigation session id={} for userId={}, caseId={}",
                saved.getId(), userId, caseId);
        return saved;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Runs one complete Socratic exchange:</p>
     * <ol>
     *   <li>Resolve or create the session.</li>
     *   <li>Reject if SUBMITTED.</li>
     *   <li>Load case context (validates case exists + is published).</li>
     *   <li>Persist the user's turn.</li>
     *   <li>Load conversation history in chronological order.</li>
     *   <li>Assemble the prompt (see class-level Javadoc for the
     *       PromptBuilder limitation note).</li>
     *   <li>Call the AI Mentor — with a stopgap catch for
     *       {@link AiServiceException}.</li>
     *   <li>Persist the AI reply.</li>
     *   <li>Increment and save the turn count.</li>
     *   <li>Return the response DTO.</li>
     * </ol>
     */
    @Override
    @Transactional
    public ChatMessageResponse postMessage(Long userId, Long caseId, String userMessage) {
        log.debug("postMessage: userId={}, caseId={}", userId, caseId);

        // 1. Resolve or create the session.
        InvestigationSession session = startOrResumeSession(userId, caseId);

        // 2. Reject if the session has already been submitted.
        if (session.getStatus() == SessionStatus.SUBMITTED) {
            throw new SessionAlreadySubmittedException(session.getId());
        }

        // 3. HARD TURN CEILING — check before calling the AI.
        //    When MAX_TURNS is reached, return a graceful closing message
        //    without calling the AI. The reply is still persisted and the
        //    turn count is still incremented, so the session record is coherent.
        //    The ceiling is silent — no error, no visible limit indicator.
        if (session.getTurnCount() >= MAX_TURNS) {
            log.info("Turn ceiling ({}) reached for sessionId={} — returning closing message",
                    MAX_TURNS, session.getId());
            ConversationTurn ceilingTurn = new ConversationTurn(
                    session.getId(), TurnSender.AI, CEILING_REPLY, LocalDateTime.now());
            turnRepository.save(ceilingTurn);
            session.setTurnCount(session.getTurnCount() + 1);
            sessionRepository.save(session);
            boolean nudge = session.getTurnCount() >= NUDGE_THRESHOLD;
            return new ChatMessageResponse(CEILING_REPLY, session.getTurnCount(), nudge);
        }

        // 4. Load case context — also validates that the case exists and is
        //    published. CaseNotFoundException propagates to GlobalExceptionHandler
        //    (404) if the case does not exist. CaseService owns this check.
        CaseInternalContext caseContext = caseService.getFullContext(caseId);

        // 5. Persist the user's conversation turn.
        ConversationTurn userTurn = new ConversationTurn(
                session.getId(), TurnSender.USER, userMessage, LocalDateTime.now());
        turnRepository.save(userTurn);
        log.debug("Persisted USER turn for sessionId={}", session.getId());

        // 6. Load the full conversation history in chronological order.
        List<ConversationTurn> history =
                turnRepository.findBySessionIdOrderByCreatedAt(session.getId());

        // 7. Build the multi-turn AI request via PromptBuilder.
        List<PromptBuilder.TurnEntry> turnEntries = toTurnEntries(history);
        String categoryName = caseContext.getCategory() != null
                ? caseContext.getCategory().name() : null;
        String difficultyName = caseContext.getVerificationDifficulty() != null
                ? caseContext.getVerificationDifficulty().name() : null;

        AiRequest aiRequest = PromptBuilder.buildSocraticRequest(
                caseContext.getClaim(),
                caseContext.getInvestigationHints(),
                categoryName,
                difficultyName,
                turnEntries,
                session.getTurnCount() + 1,  // 1-indexed current turn
                MAX_TURNS
        );

        // 8. Call the AI Mentor (never throws — returns AiPromptResult).
        AiPromptResult result = callAi(aiRequest, session.getId());
        String aiReply = result.responseText();

        // 9. Persist the AI reply.
        ConversationTurn aiTurn = new ConversationTurn(
                session.getId(), TurnSender.AI, aiReply, LocalDateTime.now());
        turnRepository.save(aiTurn);
        log.debug("Persisted AI turn for sessionId={} (degraded={})",
                session.getId(), result.degraded());

        // 10. Increment and persist the turn count.
        session.setTurnCount(session.getTurnCount() + 1);
        sessionRepository.save(session);

        // 11. Compute the nudge signal.
        //     nudgeSubmission = true when turnCount >= NUDGE_THRESHOLD (4).
        boolean nudgeSubmission = session.getTurnCount() >= NUDGE_THRESHOLD;

        return new ChatMessageResponse(aiReply, session.getTurnCount(), nudgeSubmission);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Performs a read-only lookup. Returns a "no session" response if the
     * user has never started investigating this case.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public SessionStatusResponse getSessionStatus(Long userId, Long caseId) {
        log.debug("getSessionStatus: userId={}, caseId={}", userId, caseId);

        Optional<InvestigationSession> sessionOpt =
                sessionRepository.findByUserIdAndCaseId(userId, caseId);

        if (sessionOpt.isEmpty()) {
            return new SessionStatusResponse(false, null, 0);
        }

        InvestigationSession session = sessionOpt.get();
        return new SessionStatusResponse(true, session.getStatus(), session.getTurnCount());
    }

    // ----------------------------------------------------------------
    // Private helpers
    // ----------------------------------------------------------------

    /**
     * Maps the Investigation module's {@link ConversationTurn} entities
     * to {@link PromptBuilder.TurnEntry} records.
     *
     * <p>This conversion decouples PromptBuilder from Investigation's
     * entity model. The sender is mapped as the string "USER" or "AI"
     * matching {@link TurnSender#name()}.</p>
     *
     * @param history the ordered conversation turns for this session
     * @return a list of turn entries suitable for PromptBuilder
     */
    private List<PromptBuilder.TurnEntry> toTurnEntries(List<ConversationTurn> history) {
        return history.stream()
                .map(turn -> new PromptBuilder.TurnEntry(
                        turn.getSender().name(), turn.getContent()))
                .toList();
    }

    /**
     * Calls the AI Mentor and returns a typed {@link AiPromptResult}.
     *
     * <p>Never throws. Gemini failures and blank responses are surfaced
     * as {@code degraded=true} results by {@link com.ctrlwe.quaero.ai.service.GeminiAiClientService}.
     * This method simply logs the degraded flag for observability.</p>
     *
     * @param request   the multi-turn AI request built by PromptBuilder
     * @param sessionId the current session ID (for log context only)
     * @return the AI reply as an {@link AiPromptResult}, never {@code null}
     */
    private AiPromptResult callAi(AiRequest request, Long sessionId) {
        AiPromptResult result = aiClientService.getSocraticResponse(request);
        if (result.degraded()) {
            log.warn("Degraded AI response for sessionId={} — fallback text returned", sessionId);
        }
        return result;
    }
}
