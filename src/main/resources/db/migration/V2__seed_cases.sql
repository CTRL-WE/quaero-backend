-- ============================================================
-- V2__seed_cases.sql
-- Inserts 3 sample PUBLISHED cases for immediate manual testing.
--
-- PURPOSE OF THIS SEED DATA
-- --------------------------
-- These three cases are intentionally crafted to let you verify
-- the public/internal split the moment the app boots:
--
--   1. GET /api/cases          — must return all 3 cases, each with
--      only: id, claim, evidenceTeaser (≤150 chars), alreadyCompleted.
--      No internal field (groundTruth, groundTruthExplanation,
--      trustedReferences, investigationHints, learningSummary) may
--      appear anywhere in the JSON.
--
--   2. GET /api/cases/{id}/brief — must return only: id, claim,
--      publicEvidenceSummary. Same internal-field constraint.
--
--   3. GET /api/cases/999/brief — must return HTTP 404 using the
--      ApiErrorResponse shape (status, error, message, path,
--      timestamp).
--
-- The internal fields below (groundTruth, etc.) are populated with
-- realistic content so you can confirm they exist in the database
-- but are absent from every public HTTP response.
-- ============================================================

INSERT INTO cases (
    claim,
    public_evidence_summary,
    ground_truth,
    ground_truth_explanation,
    trusted_references,
    investigation_hints,
    learning_summary,
    status,
    created_at
) VALUES

-- ---------------------------------------------------------------
-- Case 1 — Viral video of foreign leader
-- ---------------------------------------------------------------
(
    'A viral video circulating on social media shows a foreign head of state signing documents that directly contradict their publicly stated foreign policy positions.',

    'The video began circulating on major social-media platforms on 14 March 2024 and was shared over 200,000 times within 48 hours. Several international news organisations covered the story but did not independently verify the video''s origin. Reverse image search tools, including Google Images and TinEye, are freely available to the public. The office of the foreign leader in question has not issued a formal statement as of the time of publication. Media-literacy organisations such as First Draft and the Duke Reporters'' Lab have published open-access guides on verifying video authenticity using metadata and contextual analysis.',

    -- INTERNAL — never in public response
    'The video is a digitally manipulated deepfake. Independent forensic analysis by the Digital Forensic Research Lab (DFRLab) confirmed inconsistencies in facial lighting and audio waveform patterns inconsistent with the location claimed in the caption.',

    'Deepfake detection tools were able to identify artefacts around the subject''s jawline and eyes — telltale signs of AI-generated face-swapping. The audio track''s frequency spectrum did not match ambient noise expected at the alleged location. This case illustrates why geolocation and source-chain verification are essential first steps before accepting video evidence.',

    'https://www.atlanticcouncil.org/programs/digital-forensic-research-lab/
https://firstdraftnews.org/articles/a-guide-to-verifying-video-and-photos/
https://www.poynter.org/fact-checking/2023/how-to-detect-deepfakes/
https://tineye.com',

    'Ask the user: What is the original source of this video? Can you trace the earliest upload? What tools exist to verify video authenticity? What would you expect the lighting and audio to look like if the video were genuine?',

    'Deepfake videos are increasingly convincing but leave detectable artefacts. Reliable verification requires checking the video''s metadata, tracing its source chain back to the earliest upload, and running it through forensic tools. Emotional reactions to politically charged content often accelerate sharing before verification can occur — slowing down and asking "how do I know this is real?" is the first critical-thinking step.',

    'PUBLISHED',
    '2024-03-15 10:00:00'
),

-- ---------------------------------------------------------------
-- Case 2 — Misleading health statistic
-- ---------------------------------------------------------------
(
    'A widely shared infographic claims that a new dietary supplement reduces the risk of heart disease by 47% based on a peer-reviewed clinical trial.',

    'The infographic has been shared extensively on health and wellness social-media accounts. It cites "a 2023 peer-reviewed study" but does not name the journal or the authors. The supplement in question is sold by a private company. PubMed and Google Scholar are publicly searchable databases that index peer-reviewed medical literature. The Cochrane Library provides open-access systematic reviews on dietary interventions. The CONSORT reporting standards define what a rigorous clinical trial report must contain.',

    -- INTERNAL — never in public response
    'The 47% figure is statistically misleading. The cited study measured relative risk reduction, not absolute risk reduction. The absolute risk reduction was 0.8 percentage points (from 1.7% to 0.9%), and the study had a small sample size of 312 participants over 6 months with no blinding. No independent replication exists. The study was funded by the supplement manufacturer.',

    'Relative risk reduction figures appear far more dramatic than absolute risk reduction figures, even when they describe the same data. A 47% relative reduction sounds alarming; a 0.8 percentage point absolute reduction is far less impressive. Evaluating clinical evidence requires knowing: who funded the study, how large the sample was, whether it was blinded, and whether results have been replicated independently.',

    'https://pubmed.ncbi.nlm.nih.gov/
https://www.cochranelibrary.com/
https://www.consort-statement.org/
https://www.healthnewsreview.org/toolkit/tips-for-understanding-studies/absolute-vs-relative-risk/',

    'Ask the user: Is the study cited by name and author? What is the difference between relative and absolute risk reduction? Who funded the research? Has the finding been replicated? What does peer-reviewed actually mean?',

    'A single study, especially a small, manufacturer-funded one, is rarely sufficient to make health claims. The distinction between relative and absolute risk reduction is one of the most commonly exploited statistical gaps in health reporting. Strong evidence requires large, independently funded, pre-registered, blinded trials — and ideally a systematic review of multiple trials. Always ask who paid for the research.',

    'PUBLISHED',
    '2024-04-02 14:30:00'
),

-- ---------------------------------------------------------------
-- Case 3 — Out-of-context historical photograph
-- ---------------------------------------------------------------
(
    'A photograph shared on social media purports to show a prominent politician at a rally for an extremist organisation during the 1990s.',

    'The photograph has been shared widely on political commentary accounts. The caption provides a specific date and location claim: "City Hall Plaza, October 1994." Reverse image search tools are publicly accessible. Newspaper archives from major outlets are available through ProQuest, the Internet Archive, and the Wayback Machine. Local government event records are often accessible through freedom-of-information requests. Metadata embedded in digital images can contain date and device information, though this is not always reliable for scanned photographs.',

    -- INTERNAL — never in public response
    'The photograph is authentic but its caption is false. The image was originally taken at a non-partisan civic event in a different city in 1997. The politician in the photograph was one of several hundred attendees at a community clean-up day. The extremist organisation was not present at that event. The photograph was cropped to remove context and the caption was fabricated.',

    'This case demonstrates a common manipulation technique: using a real photograph with a completely fabricated caption. The original, uncropped image was located through the Internet Archive and shows a clearly non-partisan event backdrop with city-issued banners. Newspaper archives from the correct date and location corroborate the true context. The extremist organisation''s own archived records show no event in that city on that date.',

    'https://archive.org/
https://web.archive.org/
https://www.proquest.com/
https://firstdraftnews.org/articles/fake-news-complicated/
https://www.bellingcat.com/resources/how-tos/2019/12/26/guide-to-using-reverse-image-search-for-investigations/',

    'Ask the user: Can you find the original, uncropped version of this photograph? What do contemporaneous news archives say about events on that date in that location? Does the background of the photograph match the claimed location? What tools can verify the date a photograph was taken?',

    'Authentic photographs with fabricated captions are among the most effective misinformation vectors because people instinctively trust photographic evidence. Verification requires finding the original source of the image (not just the claim in the caption), checking contemporaneous reporting, and examining image metadata. The absence of a verifiable original source is itself a significant red flag.',

    'PUBLISHED',
    '2024-05-10 09:15:00'
);
