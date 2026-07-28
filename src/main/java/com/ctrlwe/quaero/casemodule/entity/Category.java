package com.ctrlwe.quaero.casemodule.entity;

/**
 * Topical category of the claim in an Investigation Challenge.
 *
 * <p>This field is reserved now per the approved V2 architecture
 * (Section 5): "Add {@code category} as an additive, nullable enum
 * column exactly the way V1's presentation metadata fields were added —
 * no filtering logic, no recommendation logic, no leaderboard, no
 * achievement system implemented alongside it."</p>
 *
 * <p>Future uses documented in V2 Section 5:</p>
 * <ul>
 *   <li>Filtering the Investigation Feed by category</li>
 *   <li>Personalised recommendations</li>
 *   <li>Analytics on reasoning errors by category</li>
 *   <li>Category-scoped leaderboards</li>
 *   <li>Category-based achievements (e.g. "Science Investigator")</li>
 * </ul>
 *
 * <p><strong>Current status:</strong> field is nullable on the entity
 * and stored in the database, but no filtering or logic is built
 * on it yet.</p>
 *
 * @author Quaero Engineering
 * @since 2.0
 */
public enum Category {

    /** Political claims, election-related content, government actions. */
    POLITICS,

    /** Sports-related claims, athlete statements, event results. */
    SPORTS,

    /** Scientific claims, research findings, academic studies. */
    SCIENCE,

    /** Health and medical claims, treatment efficacy, public health. */
    HEALTH,

    /** Crime-related claims, legal proceedings, incident reports. */
    CRIME,

    /** Technology claims, product announcements, cybersecurity. */
    TECHNOLOGY,

    /** Entertainment industry claims, celebrity news, media events. */
    ENTERTAINMENT,

    /** Environmental claims, climate data, conservation reports. */
    ENVIRONMENT,

    /** Category not covered by the named constants above. */
    OTHER
}
