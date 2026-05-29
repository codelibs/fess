# Static Theme JSP Parity — Round 3 Closure Report

**Date:** 2026-05-29
**Branch:** `feature/static-theme`
**Plan:** `docs/superpowers/plans/2026-05-29-static-theme-parity-round3.md`

## Summary

An independent fresh audit of the bootstrap (Static) theme against all 17 legacy JSP screens found that rounds 1–2 had closed most parity, but **11 Major functional gaps + a tail of high-impact Minor items remained** (plus one critical pre-existing bug). All in-scope items are now implemented, behaviorally reviewed, and verified. **171 theme tests pass; BUILD SUCCESS.**

Scope decisions (confirmed with the requester):
- Fix all Major gaps + agreed high-impact Minor items; exclude pure-cosmetic-only diffs.
- **Preserve** the static theme's deliberate improvements (cache blob-sandbox/CSP, a11y, responsive facets, markdown `<img>` strip); align only the result-affecting query divergences (#4 timestamp, #6 none-words/filetype) to JSP/server behavior.

## What changed

| # | Area | Change | Key files |
|---|------|--------|-----------|
| 0 | **Backend (CRITICAL)** | `/ui/config` now emits `features.rag_chat_enabled` (mirrors `chatClient.isAvailable()`). Chat was **permanently hidden** before — the SPA read a flag the server never sent. | `UiConfigHandler.java` |
| 1 | Search results | Popular words now render on **non-empty** results pages (gated on `features.popular_word`), via a unified renderer. | `search.js`, `app.js`, `index.html` |
| 2 | Search results | Related queries/content no longer wiped on **zero-result** pages (stopped the synchronous clear in the zero branch; `loadRelated` populates). | `search.js` |
| 3 | Home options | Added **Label multi-select** + made **lang multi-select**; added a home flash/message region; advance-link now forwards paging (`num/sort/lang/label`). | `index.html`, `search.js`, `app.js` |
| 4 | Advanced | "Last update" now emits server-faithful **`timestamp:[now-1d/d TO *]`** (was `last_modified:` in `ex_q`); dropped 3/6-month options; filetype quoted `filetype:"x"`; none-words whitespace-split; best-effort field prefill from URL. | `advance.js` |
| 5 | Chat | **Phase narration** (`chat_phase_*` + `{0}` keyword) into a progress-message element; reason-specific **fallback** messages; input **focus restore** on done; per-group filter badges + reset-on-new-chat; nested lists/blockquotes in markdown. | `chat.js`, `markdown.js`, `index.html` |
| 6 | Cache | Forward `hq` highlight terms to `/api/v2/cache` (cached pages now highlight); fixed cache-link `&hl.q=`→`&hq=`; single banner (suppressed SPA duplicate); formatted `created` date. | `cache.js`, `search.js` |
| 7 | Error pages | Server injects `<meta name="x-fess-error-code">`; `error.js` prefers it, default 404→**500**; error title/body wording aligned to JSP labels across all 16 locales. | `ThemeViewAction.java`, `error.js`, 16 bundles |

New i18n keys (all 16 bundles, exact key-set parity preserved — 251 keys each): `labels.search_popular_word_word`, `labels.chat_phase_{intent,search,evaluate,fetch,answer}`, `labels.chat_fallback_{no_results,no_relevant_results}`.

## Behavioral review gate (bugs caught beyond marker tests)

The Java tests are `contains()` marker checks and cannot catch runtime control-flow bugs. A dedicated behavioral review of the diff found and fixed:
- **Blocker** — `chat.js`: empty filter-panel path omitted `resetFilters`, so **New Chat threw** `TypeError` whenever no labels/facets were configured. Fixed (no-op added).
- **Major** — `advance.js`: none-words prefill had a dead `tok === "NOT"` branch producing `"NOT NOT"` and misfiling words. Fixed (removed branch; second-pass pairing handles it).
- **Minor** — popular-word anchors double-fired a search (inline handler + `data-spa` router). Fixed (rely on router).
- **Minor** — markdown `rel="nofollow"` was stripped by the sanitizer. Fixed at the sanitizer (`format.js`).

## Verification

- `node --check` on all 14 `assets/*.js`: pass.
- JSON validity on all 16 `i18n/messages.*.json`: pass; uniform 251 keys.
- `mvn -o test -Dtest='BundledBootstrapThemeTest, theme.LabelMessageThemeParityTest, i18n.LabelMessageThemeParityTest, ThemeViewActionTest, StaticThemeFilterTest, UiConfigHandlerTest'` → **171/171, BUILD SUCCESS**.
- `mvn formatter:format license:format` → clean (0 reformatted).

## Out of scope (intentional)

Static-theme improvements kept (cache blob-sandbox/CSP, a11y, responsive facet reflow, markdown `<img>` strip, `session.invalidate()` N/A) and pure-cosmetic-only diffs (avatars, message timestamps, header options-cog affordance).

## Not runtime-smoke-tested

As with rounds 1–2, changes are unit/marker/behavioral-review verified but **not exercised against a live Fess+OpenSearch instance**. Recommended manual smoke before release: chat phase narration during a real stream; cache highlight on a cached page opened from results; advanced timestamp filter returns expected docs; zero-result page showing related queries; New Chat with no configured filters; an `/error/*` route rendering the matching status/wording.
