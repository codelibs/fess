# Static Theme — JSP Parity Remediation (Round 2)

- **Date:** 2026-05-29
- **Branch:** `feature/static-theme`
- **Status:** Design — awaiting approval

## 1. Context & Goal

The bootstrap **static theme** (`src/main/webapp/themes/bootstrap/`) is a vanilla-JS SPA
that must faithfully reproduce the legacy JSP search UI
(`src/main/webapp/WEB-INF/view/*.jsp` + `cache.hbs`).

A prior "parity closure report" claimed 100% completion. Per direction, that report is
**discarded**; parity was re-verified independently from source (each JSP read against the
actual static-theme code and backend handlers). This spec records only the gaps that were
**verified against the real code**, plus two product decisions, and defines the remediation.

## 2. Verified Findings Summary

### Real gaps (to fix)

| # | Area | Gap (JSP → static) | Severity |
|---|------|--------------------|----------|
| 1 | No-result screen | Static shows only generic `search.no_results` ("No results.") + popular words. Missing: search icon, query-interpolated `did_not_match` message, and the suggestion line. (`index.html:143-146`, `search.js:413`, `messages.en.json:4`) | P1 |
| 2 | Home view options | JSP `index.jsp` exposes num/sort/lang/label options + an "Options" button up-front; static home (`index.html:58-73`) has only the search box + popular words. | P1 |
| 3 | Home logo | Static home uses `<h2>Fess</h2>` (`index.html:63`) instead of JSP's product logo image (`/images/logo.png`). | P1 |
| 4 | Header input | `#search-input` (`index.html:24`) missing `maxlength="1000"` (JSP `header.jsp` has it). | P2 |
| 5 | Home input | `#home-search-input` (`index.html:65`) missing `maxlength="1000"` + autofocus. | P2 |
| 6 | Advanced link | Navbar/home "Advanced" link (`index.html:42`) does not forward the current `q`; advanced form does not prefill from it. | P2 |
| 7 | Facet not-found | JSP shows a per-group "not found" message for an all-zero facet group; static silently hides the group. | P2 |
| 8 | Submit disable | JSP disables the submit button for 3s after submit (`BUTTON_DISABLE_DURATION=3000`); static does not. | P2 |
| 9 | Chat i18n keys | Static uses `labels.chat_step_*`; JSP uses `labels.chat_phase_*` (naming drift, internal only). | P2 |

### Confirmed NOT gaps (do not touch — verified)

- **Chat CSRF**: `api.js:142` sends `X-Fess-CSRF-Token` on the SSE POST. Functional.
- **Help locales**: static ships 8 help locales (en/ja/ko/de/es/fr/pt-BR/zh-CN); JSP ships only 3
  (en/ja/ko). Static **exceeds** JSP; missing-locale help falls back to English (`help.js`).
- **i18n "60 missing keys"**: false positive — the static theme uses its own key namespace
  (`search.button`, `pagination.next`, `error.title_400`, …). The strings exist; only the key
  names differ. All 16 `messages.*.json` already pass `LabelMessageThemeParityTest` (239 keys each).
- **Error pages**: HTTP status mapping (400/404/429/500), `message_key` forwarding, and header/footer
  are present. JSP's server-side `session.invalidate()` on render exception is intentionally not
  replicated (the SPA error route is a stateless response; N/A).
- **Cache**: static renders cached HTML in a sandboxed `blob:` iframe with escaped `<base>` — a
  security improvement over the JSP raw-HTML stream; functionally complete.
- **Static enhancements** (keep): geo search, filetype facet, extra help topics.

## 3. Product Decisions

1. **Scope = everything**, including the lower-value #8 (3s disable) and #9 (chat key alignment).
2. **Home view = full parity**: add an "Options" button + collapsible panel (num/sort/lang/label)
   and the logo image, matching `index.jsp`.

## 4. Remediation Detail

### Item 1 — No-result screen
- New i18n keys (all 16 `messages.*.json`; translate the core 8 locales, English fallback for the rest):
  - `search.did_not_match` = `"Your search — {0} — did not match any documents."`
  - `search.did_not_match_suggestion` = `"Try different keywords or fewer keywords."`
- `index.html` empty-state (`#empty-state`): add a muted search icon (`fa fa-search fa-3x`),
  a `<p>` for the did-not-match message, and a `<p>` for the suggestion, above `#popular-words`.
- `search.js` (empty-state path ~line 413): render `search.did_not_match` with the current query
  (inserted via `textContent`, never `innerHTML`) and the suggestion line; keep popular words.
- **Acceptance:** a zero-hit search shows icon + "Your search — <q> — did not match…" + suggestion + popular words; query is HTML-safe.

### Item 2 + 3 — Home view parity (options + logo)
- `index.html` `#home-view`: replace `<h2>Fess</h2>` with `<img src="/images/logo.png">`
  (alt via `data-i18n-alt="labels.header_brand_name"`); add an "Options" toggle button and a
  collapsible panel containing sort/num/lang/label controls mirroring the results-view
  `#search-options`; add an "Advanced" link for parity.
- `search.js` / `app.js`: populate the home option controls using the existing
  `renderSortOptions/NumOptions/LangOptions/LabelOptions` logic (refactor to accept target element
  IDs, or add home-specific siblings), and read those values into the search params on home submit
  so the selection is applied to the executed search.
- **Acceptance:** from the home page a user can set sort/num/lang/label before the first search and
  those apply; the logo image renders; behavior matches `index.jsp`.

### Item 4 + 5 — Input attributes
- Add `maxlength="1000"` to `#search-input` and `#home-search-input`; add autofocus to the home input
  (set focus on home view activation).

### Item 6 — Advanced link carries `q`
- Make the "Advanced" link(s) forward the current query (`/advance?q=…`), and have `advance.js`
  prefill the "must contain words" field from an incoming `q` URL param.

### Item 7 — Facet "not found" message
- New i18n key `facet.not_found` (all 16 locales). In `search.js` `renderFacetQueryViews`, when a
  configured facet-query group resolves to all-zero counts, render the `facet.not_found` message for
  that group instead of omitting it (match `searchResults.jsp` `facetFound=='F'` semantics).

### Item 8 — Submit disable (3s)
- On submit of the header, home, and advanced forms, disable the submit button and re-enable after
  3000 ms (mirror JSP). Apply in `search.js` and `advance.js`.

### Item 9 — Chat i18n key alignment (conservative)
- Rename the chat-only `labels.chat_step_*` → `labels.chat_phase_*` across all 16 `messages.*.json`
  and update references in `chat.js`, to match JSP naming.
- **Do NOT** rename the shared `error.*` keys (used by both chat and the error pages) — renaming them
  would break error-page rendering. Naming alignment is limited to the unambiguously chat-specific keys.
- Key **count stays identical** (rename, not add) so `LabelMessageThemeParityTest` still passes.

## 5. i18n Strategy

- Every new key (`search.did_not_match`, `search.did_not_match_suggestion`, `facet.not_found`) must be
  added to **all 16** `messages.*.json` to keep key sets identical (enforced by
  `LabelMessageThemeParityTest`). Translate the 8 core locales (ja/de/es/fr/ko/pt-BR/zh-CN); use the
  English value for the remaining 8 (consistent with the existing native-but-partial approach).
- Item 9 is a pure rename — apply uniformly across all 16 files.

## 6. Verification Plan

1. `node --check` on every changed `assets/*.js`.
2. Validate all 16 `messages.*.json` parse and have identical key sets; run
   `LabelMessageThemeParityTest` and `BundledBootstrapThemeTest` (and theme/chat handler tests).
3. Independent re-audit (fresh sub-agent) of each changed screen vs its JSP, confirming the specific
   acceptance criteria above — no reliance on this document's own claims.
4. Manual smoke recommendations: zero-hit search; home-page option selection feeding a search;
   advanced link prefill; facet group with zero counts; chat phases still labelled.

## 7. Non-Goals

- No changes to backend search/query semantics beyond what the items require.
- No reverting of static-theme enhancements (geo, filetype facet, sandboxed cache, extra help).
- No new help-locale content (static already exceeds JSP).

## 8. Risks

- **File overlap**: `index.html`, `search.js`, and the 16 i18n files are touched by multiple items →
  implement in coordinated sequence (not uncoordinated parallel) to avoid merge conflicts.
- **Item 9** is low value and touches 16 files + `chat.js`; the conservative scope (chat-only keys)
  limits blast radius. Re-run the parity test to confirm no key drift.
- **Home options refactor** must not regress the results-view options (shared render logic).
