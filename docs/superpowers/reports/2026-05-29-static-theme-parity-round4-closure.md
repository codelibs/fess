# Static Theme JSP Parity — Round 4 Closure Report

**Date:** 2026-05-29
**Branch:** `feature/static-theme`
**Plan:** `docs/superpowers/plans/2026-05-29-static-theme-parity-round4.md`

## Summary

A fresh, fully independent audit of the bootstrap (Static) theme against **all 17 legacy JSP screens** (searchResults, search, searchNoResult, searchOptions, index, header, footer, help, advance, chat/chat, cache.hbs, and the six error/*.jsp) was run via six parallel audit agents. Rounds 1–3 had closed most parity; this round found a tail of **13 in-scope functional gaps** plus several items confirmed as already-at-parity, intentional improvements, or verified non-issues. All 13 in-scope items are implemented (subagent-driven, TDD with marker tests, per-task two-stage review), behaviorally re-verified, and green. **186 theme tests pass; BUILD SUCCESS.**

Scope was confirmed with the requester: **functional parity only** — the static theme's deliberate improvements are preserved (cache blob-sandbox/CSP, a11y/skip-nav, responsive facet reflow, markdown `<img>` stripping, link `rel` hardening, in-place AJAX re-search, active-filter chips, broader help localization, dismiss buttons).

## What changed (13 items)

| # | Area | Change | Key files |
|---|------|--------|-----------|
| 1 | Home | Home search box now has suggest/autocomplete (shared `attachSuggest`, lang-aware), matching the JSP `suggestor` wiring the header box already had. | `index.html`, `app.js` |
| 2 | Chat | Inline (results-sidebar) chat now narrates phases via a dedicated `#chat-inline-progress-message` element (standalone already did). | `chat.js` |
| 3 | Search results | Favorite POST now includes `query_id` (from the card's `dataset.queryId` = `env.query_id`), restoring the JSP click/query correlation. | `search.js` |
| 4 | Header | Chat nav link label is now "AI Search" (`nav.chat_ai_mode`), matching JSP `labels.chat_ai_mode`. | `app.js`, `index.html` |
| 5 | Home | Home options panel gained a "Clear" button (resets sort/num/lang/label), matching JSP `#searchOptionsClearButton`. | `index.html`, `app.js` |
| 6 | Chat | Phase narration only substitutes the keyword on the search phase when the server sends `data.keywords`; it no longer echoes the full question on every phase (legacy parity). | `chat.js` |
| 7 | Error | `error.js codeFromPath` JSDoc corrected to state the 500 default (matches `computeErrorStatus`). | `error.js` |
| 8 | Error | `error.js PATH_TO_CODE` 503 entries annotated as reserved (computeErrorStatus emits 429 for busy, never 503; direct `/error/503` still resolves to the localized 503 page). | `error.js` |
| A | Home | `#home-flash` is now wired: a query-param-driven flash (`?error=`/`?msg=`) renders an allowlisted `flash.*` message; `renderHomeFlash` was previously dead code. | `app.js`, 16 bundles |
| C | Chat | Standalone progress strip now hides on completion (ready/error), mirroring legacy `hideProgressIndicator()`. | `chat.js` |
| D | Chat | On error the assistant bubble is removed and the error shows in the banner only (3 sites: auth/generic/onError), matching legacy `handleError`. | `chat.js` |
| E | Chat | Markdown now renders `#`–`######` as h1–h6 (was h2–h4 only), matching legacy `marked`. | `markdown.js` |
| F | Header | On the chat page the nav link becomes a "Search" link (href "/", fa-search); restored on all 8 non-chat routes. | `app.js`, 16 bundles |

New i18n keys (added to **all 16** bundles, exact key-set parity preserved): `nav.search`, `flash.login_required`, `flash.session_expired`. Native translations for the 8 core locales (`en ja de es fr ko pt-BR zh-CN`); English value for the 8 partial locales.

## Verified NON-issues (audited, deliberately NOT changed)

- **occt (`allintitle:`/`allinurl:`) spacing.** `QueryContext.java:83-88` strips the prefix via `startsWith` + `substring`; the leading space the server leaves is ignored by the parser. The SPA's `"allintitle:" + q` satisfies `startsWith("allintitle:")` → functionally identical. (An audit agent initially flagged this MAJOR; backend verification downgraded it to a non-issue.)
- **Results-page popular-words feature gate.** `/popular-words` is server-gated and the SPA fetch fails silently when disabled → equivalent to JSP not rendering them.
- **Cache + all six error views** — confirmed at parity (banner-once, `hq` highlight passthrough, `<base>` injection, charset/mimetype, status codes, localized title/body wording, `redirect.jsp` branches incl. the badAuth→`message_key` bridge).
- **Tier-3 B** (remove no-op "More.." link) and **Tier-3 G** (advanced `(`/`)` / inner-quote escaping edge cases) — excluded by the requester.

## Process notes

- **Security gate caught a real issue.** An automated commit-review flagged the home-flash (#A) wiring: the comment claimed an allowlist that wasn't implemented, so any `?error=<key>` reflected (with a `flash.` prefix) into a styled alert — a weak phishing primitive (not XSS; `createTextNode`). Fixed by adding an explicit `ALLOWED_FLASH_KEYS` Set, so unknown keys render nothing (commit `4168be012`).
- **Baseline commit.** Round-3 work was uncommitted in the working tree at the start of this round. To keep per-task diffs clean (each round-4 task touches shared files that carried round-3 hunks), the round-3 working tree was committed once as a baseline (`dc7704551`) before tasks 2–12; the requester was informed (reversible via soft reset).

## Verification

- `node --check` on all `assets/*.js`: pass. JSON validity on all 16 `i18n/messages.*.json`: pass (uniform key set).
- `mvn -o test -Dtest='BundledBootstrapThemeTest, theme.LabelMessageThemeParityTest, i18n.LabelMessageThemeParityTest, ThemeViewActionTest, StaticThemeFilterTest, UiConfigHandlerTest'` → **186/186, BUILD SUCCESS**.
- `mvn formatter:format license:format` → clean (0 reformatted).
- Per-task spec + code-quality reviews (subagent-driven) and a final holistic integration review across the shared files (`app.js attachHomeView`/router, `chat.js submitQuestion`/builders, `index.html`): APPROVED.

## Not runtime-smoke-tested

As with rounds 1–3, changes are unit/marker/behavioral-review verified but **not exercised against a live Fess+OpenSearch instance**. Recommended manual smoke before release: home-box suggest dropdown; inline-chat phase narration during a real stream; favorite recorded with its query_id; "AI Search" header label + chat-page "Search" back-link; home options Clear; chat error showing banner-only; markdown `#` heading; advanced/cache/error flows unchanged.

## Known minor (SPA-only, non-blocking)

- The inline-chat progress message (`#chat-inline-progress-message`) is not auto-cleared on completion (the #C strip-hide was scoped standalone-only per plan); it is overwritten on the next phase/question and reset by `phaseReset`. The inline sidebar is an SPA construct with no JSP equivalent, so this is not a parity gap.
