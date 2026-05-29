# Static Theme JSP Parity Remediation (Round 3) — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Close the round-3 JSP-vs-static-theme parity gaps found by an independent fresh audit (rounds 1–2 closed the rest). Scope = all **Major** functional gaps (#1–#11) plus the agreed high-impact **Minor** items. Intentional static-theme *improvements* are preserved (cache blob-sandbox/CSP, a11y, responsive facets, markdown img-strip); only the result-affecting query divergences (#4 timestamp, #6 none-words/filetype) are aligned to JSP/server behavior.

**Architecture:** The bootstrap theme is a build-step-free vanilla-JS SPA in `src/main/webapp/themes/bootstrap/`. There is **no JS test runner**; verification is by Java tests that assert source files *contain* markers (`BundledBootstrapThemeTest`) and that all 16 i18n bundles share an identical key set (`theme/LabelMessageThemeParityTest#test_allLocalesExactlyMatchEnKeys`). TDD loop: add a failing marker assertion to `BundledBootstrapThemeTest`, run it, implement the marker in `index.html`/`*.js`/JSON/Java, re-run, commit.

**Tech Stack:** Vanilla ES modules, Bootstrap 5, Font Awesome, JSON i18n bundles; JUnit + Maven for verification; `node --check` for JS syntax.

**Conventions (verified):**
- i18n interpolation: `t(key, [positional])` → `{0}` placeholders (`i18n.js:68-72`). JSP `chat_phase_*` uses a `__keywords__` token (different convention) — when porting, convert to a `{0}` placeholder.
- 16 locale bundles: `de en es fr hi id it ja ko nl pl pt-BR ru tr zh-CN zh-TW`.
- `theme/LabelMessageThemeParityTest` requires **every** bundle to have the **exact** same key set as `messages.en.json`. Any new key MUST be added to all 16.
- Reusable option renderers in `search.js`: `renderSortOptions`, `renderNumOptions`, `renderLangOptions`, `renderLabelOptions`, invoked from `renderSearchOptions`.
- Run targeted Java tests: `mvn -o test -Dtest='BundledBootstrapThemeTest,org.codelibs.fess.theme.LabelMessageThemeParityTest,ThemeViewActionTest,StaticThemeFilterTest'` (drop `-o` if offline cache lacks deps).
- JS syntax: `node --check <file>`; JSON validity: `node -e "JSON.parse(require('fs').readFileSync('<file>'))"`.

**Locale translation policy:** New keys go in **all 16** bundles. Provide proper translations for the 8 core locales (`ja de es fr ko pt-BR zh-CN` + en); use the English value for the 8 partial locales (`hi id it nl pl ru tr zh-TW`). For error-wording alignment (#11b), pull the already-localized strings from `src/main/resources/fess_label_<loc>.properties` per locale (translations already exist there).

**Execution model:** Shared files (`index.html`, `BundledBootstrapThemeTest.java`, the 16 i18n bundles) are touched by most tasks → **implement tasks SEQUENTIALLY**, one focused subagent per task, in the order below (Task 0 first). Do not parallelize. After each task: `node --check` changed JS + JSON validity, then the targeted Java test. After all tasks: full theme suite + `mvn formatter:format && mvn license:format`.

**Amendments from adversarial re-verification (authoritative — override any conflicting task text below):**
- **A1 [CRITICAL]:** Chat is **permanently hidden** today — `app.js:249`, `chat.js:878,1016` read `cfg.features.rag_chat_enabled`, but `UiConfigHandler` (features map, ~lines 260-276) never emits it (`undefined`→`false`). New **Task 0** adds it server-side, sequenced BEFORE Task 5, else all chat-UX work is unreachable.
- **A2 [#2 related-on-zero]:** The theme does NOT read `env.related_query`; related queries/content come from `loadRelated()` (search.js:~532), which already runs **un-gated** after `renderResults`. The bug is only the **synchronous clear + early-return** in the zero-result branch (search.js:~427-428). Fix = stop clearing there; do NOT add an `env.related_query` render path.
- **A3 [#5 advance prefill]:** Lossless reverse-mapping of a flattened `q` is **impossible** (no field markers for all/any/exact). Re-scope to **best-effort partial prefill**: recover `site:`, `filetype:"…"`, `NOT x`, occt prefix, quoted phrases; dump the remainder into all-words. DROP the "round-trip restores fields" acceptance.
- **A4 [#4 timestamp]:** Reuse existing labels `labels.facet_timestamp_{1day,1week,1month,1year}` (no new keys); DROP the static's `3month`/`6month` options; the current date-math `[now/d-1d TO *]` is also wrong-ordered — emit JSP's `timestamp:[now-1d/d TO *]` etc.
- **A5 [#10 cache hq]:** Also fix the cache-link fallback `&hl.q=`→`&hq=` (search.js:~245); the v2 CacheHandler only honors `hq`. Prefer **suppressing the SPA's own banner** over regex-stripping the in-`content` banner (avoids clobbering `{{{hl_cache}}}`/`<base>`).
- **A6 [#11 busy wording]:** `busy`→**429** (computeErrorStatus + error.js). Put "Service Temporarily Unavailable" on `error.title_429`/`error.body_429`, NOT the existing 503 keys.
- **A7 [Task 3]:** Two distinct popular-words loaders exist — `app.js renderHomePopularWords`→`#home-popular-words` (slice(0,5)) and `search.js loadPopularWords`→`#popular-words`. Unify into one parameterized renderer; verify home is not regressed when removing the slice.
- **A8 [Task 2]:** Include advance-link paging-query forwarding (header.jsp:119 `fe:pagingQuery(null)` equivalent).

**Verified backend contracts (do not re-derive):**
- `/ui/config` exposes: `sort_options, num_options, lang_options, label_options, filetype_options, facet_views, notifications, features{search_log_enabled,user_favorite,popular_word,thumbnail_enabled,display_label_type,login_link,eoled,development_mode,...}, csrf_token` (UiConfigHandler:360-376). It does **NOT** expose `rag_chat_enabled`, `highlight_params`, or an osdd link.
- `/api/v2/search` returns `related_query` (singular key), `related_contents`, `highlight_params`, `record_count`, `exec_time`, `partial`, `facet_field`, `facet_query`, `page_numbers` … (SearchHandler:133-165). `related_query`/`related_contents` are returned **regardless of hit count**. Popular words are **NOT** here — separate endpoint already called by `app.js loadPopularWords`.
- `/api/v2/cache/{docId}` reads multi-valued `hq` param → highlight (CacheHandler:150-151); returns `{doc_id, mimetype:"text/html", content, [url], [created raw string], charset}`.
- Advanced "last update" = field **`timestamp`** with `[now-1d/d TO *]`,`[now-1w/d TO *]`,`[now-1M/d TO *]`,`[now-1y/d TO *]` (QueryStringBuilder:242-244, advance.jsp:241-251). Both `timestamp` and `last_modified` exist (doc.json:523,553).
- Error route: `ThemeViewAction.computeErrorStatus` maps notfound→404/badrequest→400/busy→429/else→500, sets `X-Fess-Error-Code` header + `httpStatus`, injects only `<meta name="x-fess-error-detail-key">` (no code meta). Header is NOT readable by client JS post-navigation; `error.js codeFromPath` defaults to 404.

---

## File Structure

| File | Responsibility | Tasks |
|------|----------------|-------|
| `i18n/messages.*.json` (×16) | UI strings; add new keys (chat phases, fallback reasons, popular-word/label-on-home, error wording) | all |
| `assets/search.js` | popular words on results, related-on-zero, home label/lang options, sort/label renderers | 1,2,3 |
| `assets/app.js` | home popular-words loader reuse, home flash region, advance-link forwarding | 2,3 |
| `assets/advance.js` | timestamp field fix, full prefill from URL, none-words/filetype quoting | 4 |
| `assets/chat.js` | phase narration + progress message, fallback reasons, focus restore, filter badges, reset-on-new-chat | 5 |
| `assets/markdown.js` | nested lists/blockquotes | 5 |
| `assets/cache.js` | forward `hq`, banner double-render dedup, format `created` | 6 |
| `assets/error.js` | read code meta, default→500, wording from new keys | 7 |
| `index.html` | SPA shell markup (results popular-words slot, home label/flash, error meta read) | 1,2,3,7 |
| `app/web/theme/ThemeViewAction.java` | inject `<meta name="x-fess-error-code">` | 7 |
| `test/.../theme/BundledBootstrapThemeTest.java` | marker assertions (test contract) | all |
| `test/.../theme/ThemeViewActionTest.java` | assert code meta injected | 7 |

---

## Task 0 — Backend: expose `rag_chat_enabled` so chat is reachable (CRITICAL, do first)

**Files:** modify `src/main/java/org/codelibs/fess/api/v2/handlers/UiConfigHandler.java`, add assertion to its handler test (e.g. `UiConfigHandlerTest`), `BundledBootstrapThemeTest.java` (optional marker).

Steps:
- [ ] In `UiConfigHandler`'s `features` map (~lines 260-276), add `features.put("rag_chat_enabled", ...)` sourced to mirror the JSP gate `FessSearchAction.java:199` (`chatClient.isAvailable()`), e.g. `ComponentUtil.getComponent(ChatClient.class).isAvailable()` or `fessConfig.isRagChatEnabled()` — verify which the JSP path actually uses and match it exactly.
- [ ] Add a test asserting `/ui/config` features contains `rag_chat_enabled`.
- [ ] `mvn -o test -Dtest=UiConfigHandlerTest` (or the relevant handler test).

Acceptance: `/ui/config` response `features.rag_chat_enabled` reflects server chat availability; the SPA chat nav link / view can now be shown.

## Task 1 — Search results: popular words on non-empty page + related queries on zero-result (#1, #2)

**Files:** modify `assets/search.js`, `assets/app.js`, `index.html`, 16 i18n bundles (if any new key), `BundledBootstrapThemeTest.java`.

Steps:
- [ ] Add a popular-words slot to the results header in `index.html` (a container near the result-status banner, e.g. `#results-popular-words`), distinct from the home/empty `#popular-words`.
- [ ] In `search.js renderResults`, when `features.popular_word` is enabled, fetch+render popular words into the results slot (reuse the existing popular-word loader from `app.js` — extract a shared function if needed). Match JSP layout: label + first 3 inline always, remainder `d-sm-inline-block` (responsive). (#1)
- [ ] In `search.js`, STOP clearing related queries/content on empty results. On a zero-result render, still call `renderRelatedQueries(env.related_query)` and `renderRelatedContent(env.related_contents)` and place them above the no-result block (per search.jsp:111-136 ordering). (#2)
- [ ] Verify the no-result path still shows the did-not-match icon + suggestion (parity-r2 #1) and now ALSO shows related queries/popular words when present.
- [ ] Add `BundledBootstrapThemeTest` markers: results-popular-words container id; assertion that `renderResults`/empty path references `related_query` without unconditionally clearing.
- [ ] `node --check search.js app.js`; run targeted Java test.

Acceptance: results page (with hits) shows popular words; zero-result page shows related queries/contents + popular words when configured.

## Task 2 — Home options: Label selector + multi-select lang + flash region (#3, minor)

**Files:** modify `index.html`, `assets/search.js`, `assets/app.js`, 16 bundles (reuse existing label/lang keys; add a flash key only if needed), `BundledBootstrapThemeTest.java`.

Steps:
- [ ] Add a Label multi-select to the home `#home-options` panel, populated from `cfg.label_options`, gated on `features.display_label_type` and non-empty options (mirror results-view `#label-dropdown`). Carry selected labels into the executed search (`applyHomeOptions` → `fields.label`). (#3)
- [ ] Make the home lang select **multi-select** (remove the `multiple`-stripping for the home lang clone) to match `searchOptions.jsp:75-84`. Ensure multiple `lang` params are forwarded. (minor)
- [ ] Add a home-level flash/message region in `index.html` (an `aria-live` alert slot) and render config/notification or front-end validation messages there (the notification slot already exists; add a generic message slot for parity with `index.jsp:123-130`). Keep minimal. (minor)
- [ ] Optional (cheap parity): wire suggest/autocomplete on the home search box the same way the header box is wired in `search.js`. (minor)
- [ ] Add `BundledBootstrapThemeTest` markers (home label-select id; home lang `multiple`; flash region id).
- [ ] `node --check`; targeted Java test.

Acceptance: home options panel offers sort/num/lang(multi)/label; selections flow into `/search`.

## Task 3 — Popular-words alignment (count/label/responsive) (minor, folds with Task 1)

**Files:** `assets/app.js`, `assets/search.js`, 16 bundles (label wording only — values, not keys).

Steps:
- [ ] Align popular-words rendering to JSP: first 3 always visible, remainder `d-sm-inline-block`; remove the hard slice-to-5. Apply to both home/empty and results slots (shared function). (minor)
- [ ] Confirm the label text semantics match JSP `labels.search_popular_word_word` ("Popular Word:") — adjust the existing key's value if it diverges (value only; key set unchanged). Pull localized values from `fess_label_<loc>.properties` where they differ.
- [ ] `node --check`; targeted Java test.

## Task 4 — Advanced search: query-assembly parity (#4, #5, #6)

**Files:** modify `assets/advance.js`, `index.html` (timestamp option values), 16 bundles (timestamp option labels only if changed), `BundledBootstrapThemeTest.java`.

Steps:
- [ ] **#4 timestamp:** change the "last update" control to emit `timestamp:<value>` as a query condition (append to `q`/conditions exactly like the server does), NOT `last_modified:` in `ex_q`. Use the four JSP date-math values verbatim: `[now-1d/d TO *]`, `[now-1w/d TO *]`, `[now-1M/d TO *]`, `[now-1y/d TO *]` mapped to day/week/month/year options (align option set to JSP's four). Source of truth: `advance.jsp:241-251`, `QueryStringBuilder.java:242-244`.
- [ ] **#6 none-words:** match the server's space-split — split `none of these words` on whitespace, each token → `NOT <token>` (drop the quote-aware grouping). Source: `QueryStringBuilder.java:230-235`.
- [ ] **#6 filetype:** emit `filetype:"<value>"` (quoted) to match `QueryStringBuilder.java:236-238`.
- [ ] **#5 prefill:** on `/advance` load, parse the incoming query (`?q=`) and repopulate ALL advance fields it can reconstruct (all-words/exact-phrase/any/none/site/filetype/occt/timestamp/lang/sort/num) by reversing the compose() mapping. Where lossless reconstruction is impossible, prefill the all-words box with the raw remainder (current behavior) so nothing is dropped. Forward `lang`/`sort`/`num`/`label` from the URL into the matching selects. Source intent: `advance.jsp` POST-back repopulation.
- [ ] Keep `occt` prefixing, OR-grouping for any-words, site mapping, suggest on all-words, 3s submit-disable (already at parity).
- [ ] Add `BundledBootstrapThemeTest` markers: `timestamp:[now-1` literal present (and `last_modified:[now` absent in advance compose), `filetype:\"` quoting, `NOT ` none-words split, prefill of exact/any/none fields.
- [ ] `node --check advance.js`; targeted Java test.

Acceptance: advanced search produces the same `q` string the JSP/server would for equivalent inputs; round-tripping `/search`→`/advance` restores fields.

## Task 5 — Chat: phase narration, fallback reasons, focus, filter UX, markdown (#7, #8, #9, minor)

**Files:** modify `assets/chat.js`, `assets/markdown.js`, 16 bundles (new keys), `BundledBootstrapThemeTest.java`.

Steps:
- [ ] **#7 phase narration:** add the `chat_phase_intent/search/evaluate/fetch/answer` status messages (longer text) as new i18n keys, porting the JSP strings from `fess_label.properties:1170-1174` and converting `__keywords__` → `{0}`. On each `phase` SSE event, render the interpolated phase message into a dedicated progress-message element (add `#chat-progress-message` to the standalone chat view) AND keep the existing step badges. Substitute the user's keywords for `{0}`. Source: `chat.jsp:181-186`, legacy `js/chat.js:492-505`.
- [ ] **#8 fallback:** add keys `chat_fallback_no_results`, `chat_fallback_no_relevant_results` (port from legacy `js/chat.js:577-584` semantics — "refining query and searching again…"); on a `fallback` event, key the message off `data.reason` instead of the generic model-fallback string.
- [ ] **#9 focus restore:** on `done`, restore focus to the chat input (`chatInput.focus()`), matching legacy `js/chat.js:546`.
- [ ] **minor filter UX:** add per-group filter badges (legacy `updateGroupBadge`) and reset active filters on new chat (legacy `resetFilters`).
- [ ] **minor markdown:** add nested list and nested blockquote support in `markdown.js`. Keep `<img>` stripped (intentional improvement, per scope decision). Optionally add `rel="nofollow"` to rendered links to match legacy.
- [ ] **verify (separate):** confirm how chat nav/view is gated given `/ui/config` lacks `rag_chat_enabled`; if the current gate references a non-existent flag, fix to a working signal or document. Do not regress the disabled-state alert.
- [ ] Add `BundledBootstrapThemeTest` markers: `chat_phase_search` key used in chat.js; `#chat-progress-message`; `chat_fallback_no_relevant_results`; `chatInput.focus` on done; nested-list handling marker.
- [ ] `node --check chat.js markdown.js`; targeted Java test.

Acceptance: live per-phase narration appears during streaming; fallback shows the correct reason text; input refocuses on completion.

## Task 6 — Cache: highlight passthrough + banner/date polish (#10, minor)

**Files:** modify `assets/cache.js`, `assets/search.js` (cache link carries highlight terms), `index.html` (if a route param is needed), 16 bundles (none expected), `BundledBootstrapThemeTest.java`.

Steps:
- [ ] **#10 highlight:** thread highlight terms to the cache view. In `search.js`, build the cache link to include `hq` terms (from `env.highlight_params` / the query terms, mirroring server `appendHighlightParams`). In `cache.js`, read `hq` from the route URL and forward as one/more `hq` query params on `GET /api/v2/cache/{docId}?hq=…` (CacheHandler:150 supports multi-valued). Source: cache.hbs `{{{hl_cache}}}`, ViewHelper:742-743.
- [ ] **minor banner dedup:** the API `content` already contains the cache.hbs banner + `<base>`; suppress the SPA's duplicate out-of-iframe banner OR strip the in-`content` banner — pick one so the notice renders once. Keep the metadata block (improvement).
- [ ] **minor date:** format the raw `created` string for display in the SPA banner/metadata (it arrives unformatted; CacheHandler:174). Use `format.js` date helper.
- [ ] Add `BundledBootstrapThemeTest` markers: `hq` forwarded in cache.js; cache link builds `hq` in search.js.
- [ ] `node --check cache.js search.js`; targeted Java test.

Acceptance: opening a cached page from a search highlights the search terms; the cached-copy notice appears exactly once; the date is human-readable.

## Task 7 — Error pages: authoritative code + wording alignment (#11, minor)

**Files:** modify `app/web/theme/ThemeViewAction.java`, `assets/error.js`, `index.html` (read meta), 16 bundles (error wording values from `fess_label_*.properties`), `BundledBootstrapThemeTest.java`, `ThemeViewActionTest.java`.

Steps:
- [ ] **server:** in `ThemeViewAction`, inject `<meta name="x-fess-error-code" content="<status>">` alongside the existing detail-key meta (so the code is client-readable). Add `ThemeViewActionTest` assertion.
- [ ] **client:** in `error.js`, prefer the `x-fess-error-code` meta when present; fall back to `codeFromPath`. Change the `codeFromPath` default from 404 → **500** to match `computeErrorStatus`. (#11)
- [ ] **minor wording:** align error title/body strings to the JSP labels (busy "Service Temporarily Unavailable", badRequest "Invalid Request Format." / "Your request to the URL is invalid.", notFound "Page Not Found." / "Please check the URL.", system "…contact the site administrator"). Pull the localized values from `fess_label_<loc>.properties` for each of the 16 locales (values only; keys unchanged). Preserve the SPA-only improvements (home link, contact-admin, 503) — additive, not removed.
- [ ] Add `BundledBootstrapThemeTest` markers: error.js reads `x-fess-error-code` meta; default `"500"`.
- [ ] `node --check error.js`; run `ThemeViewActionTest` + targeted suite.

Acceptance: `/error/*` rendered page always matches its HTTP status; an unknown `/error/xyz` renders 500; error wording matches JSP semantics across locales.

---

## Final verification (after all tasks)

- [ ] `for f in src/main/webapp/themes/bootstrap/assets/*.js; do node --check "$f"; done`
- [ ] `for f in src/main/webapp/themes/bootstrap/i18n/messages.*.json; do node -e "JSON.parse(require('fs').readFileSync('$f'))"; done`
- [ ] `mvn -o test -Dtest='BundledBootstrapThemeTest,org.codelibs.fess.theme.LabelMessageThemeParityTest,org.codelibs.fess.i18n.LabelMessageThemeParityTest,ThemeViewActionTest,StaticThemeFilterTest'`
- [ ] `mvn formatter:format && mvn license:format`
- [ ] Update `docs/superpowers/reports/` with a round-3 closure note; update README if the module list changed.

## Out of scope (intentional, per scope decision)
- Cache blob-sandbox/CSP hardening, a11y enhancements, responsive facet reflow (vs JSP mobile offcanvas), markdown `<img>` stripping, `session.invalidate()` — all kept as deliberate improvements / architectural N/A.
- Pure-cosmetic-only differences not affecting behavior (avatars, message timestamps, header options cog affordance) unless trivially folded in.
