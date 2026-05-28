# Static Theme — Full JSP Parity Remediation (Design)

- Date: 2026-05-29
- Branch: `feature/static-theme`
- Theme: `src/main/webapp/themes/bootstrap/` (SPA shell + ES modules over `/api/v2`)
- Status: approved design, pending implementation plan

## 1. Background

The `bootstrap` static theme is a SPA reference implementation intended to fully
reproduce the legacy JSP search UI. Prior rounds (commits `00f0703d6` →
`54e364fea`) claimed parity and locked a parity test, but a fresh audit against
the 17 target JSP files found **confirmed functional defects** — including
several P0 features that do not work at all. This spec captures the audit
findings (each verified against code with file:line evidence) and the approved
remediation plan.

### Target JSP files audited
`searchResults.jsp`, `header.jsp`, `help.jsp`, `footer.jsp`, `chat/chat.jsp`,
`searchOptions.jsp`, `index.jsp`, `advance.jsp`, `searchNoResult.jsp`,
`cache.hbs`, `error/{error,badRequest,notFound,busy,redirect,system}.jsp`,
`search.jsp`.

### Important path correction
The v2 API handlers live at `org/codelibs/fess/api/v2/handlers/` (NOT
`app/web/api/v2/`). All references below use the verified path.

## 2. Approved scope & decisions

| Decision | Choice |
|---|---|
| Remediation scope | **All** — P0 + P1 + P2 |
| Geo search | **In scope** — implement `geoForm`/`geoQuery` equivalent in the SPA |
| Cache reproduction | **Full** — extend `/api/v2/cache` to return `url`/`created`/`charset` |
| `busy` HTTP status | **Unify on 429** across `web.xml`, `ThemeViewAction`, `error.js`, i18n |
| Locale coverage | **JSP-equivalent** — expand SPA i18n to the 16 `fess_label_*` locales |

### Locale target
JSP ships labels/messages for 16 locales: `de en es fr hi id it ja ko nl pl
pt_BR ru tr zh_CN zh_TW`. SPA currently has 8 (`en ja de es fr ko pt-BR zh-CN`).
Add the missing **8**: `hi, id, it, nl, pl, ru, tr, zh-TW` (using BCP-47 file
names, e.g. `messages.zh-TW.json`, mapped from properties `zh_TW`).

### Cross-cutting design choices
- **Chat is fixed frontend-side** to send the contract the backend already
  enforces (`message` / `extra_queries` / nested `fields.label`). Backend is NOT
  loosened.
- **Filetype options come from `/ui/config`**, not a hardcoded JS list, to
  prevent future drift from the index mapping.
- **Advance keeps client-side query composition**, but `search.js runFromUrl()`
  is extended to honor `lang` / `fields.label` / `ex_q` / geo params.
- **Virtual host** is server-resolved from the `Host` header (confirmed for
  suggest/popular/config). Planning must verify `/api/v2/search` does the same;
  if so it is N/A on the client.

## 3. Confirmed findings (remediation backlog)

### P0 — Feature functionally broken
- **CHAT-1** Chat cannot send any message. SPA posts `{question, extra_q,
  fields:[…]}` (`chat.js:752-754`); backend requires `message`,
  `extra_queries`, nested `fields.label` (`ChatRequestBody.java:146`,
  `ChatStreamHandler.java:236-238` → "message is required").
- **CACHE-1** Cache view blocked by CSP. `index.html:5` /
  `ThemeViewAction.INDEX_CSP:65-66` lack `frame-src`/`child-src`; `blob:` iframe
  (`cache.js:170-175`) falls back to `default-src 'self'` and is blocked.
- **ADV-1** Advanced filetype matches zero docs. `advance.js:219-221` emits
  `msword/msexcel/mspowerpoint`; index uses `word/excel/powerpoint`
  (`fess_config.properties:652-654, 976-978`).
- **ADV-2** Advanced filters silently dropped. `advance.js` appends `lang`,
  `fields.label`, `ex_q`; `search.js runFromUrl()` (`789-801`) reads only
  `q/start/num/sort`.

### P1 — Visibly wrong / degraded
- **SRCH-1** Broken thumbnails — `search.js:147-152` uses raw `d.thumbnail` as
  `<img src>`; JSP builds `/thumbnail/?docId=…&queryId=…`
  (`searchResults.jsp:108-109`). `thumbnail` is a keyword field, not a URL.
- **SRCH-2** Title highlighting lost — `search.js:167` uses plain `d.title`; JSP
  uses highlighted `content_title` (`searchResults.jsp:101`). Response exposes
  both.
- **CHAT-2** CJK input broken — no `isComposing` guard on Enter
  (`chat.js:959, 1192`).
- **SRCH-3** Label facet has no counts / no zero-suppression — built from
  `/labels` not facet response (`search.js:1064-1067`); risks duplication with
  server-returned label facet.
- **SRCH-4** Facet query views hardcoded — ignores server `facet_views`
  (`UiConfigHandler`) + `facet_query` counts (`SearchHandler`)
  (`search.js:32-56`).
- **SRCH-5** No inline validation feedback — `INVALID_REQUEST` (query too long,
  offset exceeded) renders a generic error instead of the specific message.

### P2 — Polish
- **SRCH-6** Suggest doesn't forward `lang`/`label` (`search.js:558`).
- **SRCH-7** OSDD `<link rel=search>` not config-gated (`index.html:13-14`).
- **SRCH-8** Per-query `<title>` not updated.
- **SRCH-9** Results status line drops `<b>` emphasis (cosmetic).
- **HDR-1** Footer copyright hardcoded client-year, not i18n
  (`app.js:205-217`); `footer.copyright` key effectively dead.
- **CHAT-3** Sources lack file-type icon / type label / index; precedence
  `url||go_url` vs legacy `go_url||url_link||url`.
- **CHAT-4** Phase status model collapsed — ignores `status:start|complete` and
  `hit_count` timing (`chat.js:760-766` vs `ChatStreamHandler:503-528`).
- **CHAT-5** Missing: retry payload interpolation (`attempt/max/seconds`),
  char-counter 80%/95% warning, textarea auto-resize, premature-EOF recovery,
  per-dropdown filter search box.
- **CHAT-6** Markdown subset narrower than server (`marked`+DOMPurify): no
  tables, limited blockquote/headings. Acceptable but note.
- **ADV-3** None-of uses `-word`; server path uses `NOT word`
  (`QueryStringBuilder.java:230-235`). Align.
- **ADV-4** No suggestor on advanced all-words field (`advance.jsp:323`).
- **ADV-5** advance.js doesn't preserve unrelated existing query params.
- **ADV-6** Sort options click/favorite gating must come from config.
- **CACHE-2** Missing `<base href>` + "cache of X as of Y" banner + charset
  (needs API `url`/`created`/`charset`).
- **CACHE-3** Missing i18n key `labels.search_result_cache` → raw key shown as
  iframe title (`cache.js:182`).
- **ERR-1** `busy` status divergence — unify on **429** (see decision); wire
  `_429` i18n, drop unreachable `_503` path or repurpose.
- **ERR-2** `<la:errors>`/`<la:info>` saved messages not forwarded — extend
  `ErrorAction`/`CacheAction` redirects to add `?message_key=…` (client keys
  like `error.detail_docid_not_found` already exist).
- **HELP-1** Help `ranges` section omits the `{}` exclusive-bound note that the
  JSP documents.

### Geo (in scope per decision)
- **GEO-1** SPA has no geo search at all; JSP wires `geoForm`/`geoQuery` into the
  search form and carries it across pagination/facets. Implement SPA equivalent
  + ensure `/api/v2/search` accepts geo params and `runFromUrl` propagates them.

### Confirmed N/A-by-architecture (no action)
- Server error dispatch (`web.xml:253-280` → `redirect.jsp` →
  `ThemeViewAction` status emission) — correctly preserved server-side.
- Mobile facet offcanvas → responsive column reflow.
- User dropdown profile/admin — already implemented (`auth.js:84-106`).
- GSA-compat params — separate v1 API surface.
- jQuery/popper/suggestor script-tag model → replaced by ES modules.

## 4. Phase plan

Dependency order: **A → (B, C, D, E in parallel) → F → G**.

### Phase A — Backend / v2 API
- A1. `CacheHandler`: return `url`, `created`, `charset` (and mimetype) for the
  cache view banner / `<base href>` / charset. (CACHE-2)
- A2. Verify + enable geo params on `/api/v2/search`; document param names.
  (GEO-1 backend)
- A3. Confirm `/api/v2/search` applies virtual-host from Host header; confirm
  `facet_views` (`/ui/config`) and `facet_query`/`facet_field` counts are
  exposed (audit says yes — verify and document for Phase B).
- A4. Confirm filetype option list is available via `/ui/config` (or add it) so
  ADV-1/ADV-6 can source from config.

### Phase B — Core search SPA
`search.js`, `format.js`, `index.html`, `styles.css`
- B1 thumbnail URL (SRCH-1) · B2 content_title highlight (SRCH-2) · B3 label
  facet counts + zero-suppress + de-dup (SRCH-3) · B4 facet views from server
  config + counts (SRCH-4) · B5 inline validation errors (SRCH-5) · B6 suggest
  forwards lang/label (SRCH-6) · B7 per-query `<title>` (SRCH-8) · B8 OSDD gate
  (SRCH-7) · B9 status-line emphasis (SRCH-9) · **B10 geo search form + param
  propagation (GEO-1 client)**.

### Phase C — Advance SPA
`advance.js`, `search.js`
- C1 filetype values from config (ADV-1) · C2 `runFromUrl` honors
  `lang`/`fields.label`/`ex_q`/geo + applies to `/search` request (ADV-2) · C3
  none-of `NOT` (ADV-3) · C4 suggestor on all-words (ADV-4) · C5 preserve
  existing params (ADV-5) · C6 sort gating from config (ADV-6).

### Phase D — Chat SPA
`chat.js`, `api.js`, `markdown.js`
- D1 **body contract** `message`/`extra_queries`/`fields.label` (CHAT-1) · D2
  IME guard (CHAT-2) · D3 sources icon/type/index + precedence (CHAT-3) · D4
  phase start/complete + hit_count timing (CHAT-4) · D5 retry interpolation,
  char warning, auto-resize, EOF recovery, per-dropdown filter search (CHAT-5) ·
  D6 note/raise markdown gaps if cheap (CHAT-6).

### Phase E — Cache + Error + CSP + security
`index.html`, `ThemeViewAction.java`, `cache.js`, `error.js`, error redirects,
`help/*.json`, footer
- E1 CSP `frame-src`/`child-src` allow `blob:` in BOTH `index.html` and
  `ThemeViewAction.INDEX_CSP` (CACHE-1) · E2 cache banner + `<base href>` +
  charset from new API fields (CACHE-2) · E3 add `labels.search_result_cache`
  (CACHE-3) · E4 busy → 429 everywhere + wording/i18n (ERR-1) · E5 `message_key`
  forwarding from `ErrorAction`/`CacheAction` (ERR-2) · E6 help `{}` ranges
  (HELP-1) · E7 footer copyright i18n + release year (HDR-1).

### Phase F — i18n (16 locales)
`i18n/messages.*.json`, `help/*.json`, `i18n.js` `SUPPORTED`
- F1 add `hi, id, it, nl, pl, ru, tr, zh-TW` to messages bundles · F2 same for
  help bundles · F3 add ALL new keys introduced in B–E to every one of the 16
  locales · F4 update `SUPPORTED` and any locale-mapping (`zh_TW`→`zh-TW`).

### Phase G — Verification
- G1 extend the locked parity test to cover new behaviors / locales · G2 `mvn
  formatter:format && mvn license:format` then compile + run unit tests · G3
  re-run the gap audit (the four parity reports) to confirm closure; produce a
  closing report.

## 5. Risks & notes
- CSP change must keep `frame-ancestors 'none'`; only add `frame-src blob:` (and
  `child-src blob:` for older engines). Verify cache iframe renders in a browser.
- Chat contract fix is a release blocker — prioritize and add a regression test.
- i18n expansion to 8 new locales is the largest mechanical effort; new keys
  from B–E must be back-filled to ALL 16 to satisfy the repo i18n rule.
- Geo backend support is unverified; A2 may surface that `/api/v2/search` needs a
  new param — that turns GEO-1 into a backend+frontend item.
- Keep the theme's XSS discipline: no `innerHTML` with dynamic data; build DOM
  with `createElement`/`textContent`.

## 6. Verification of completeness (the user's re-check requirement)
Phase G re-runs the same four-quadrant audit (core search; header/footer/help;
chat/advance; error/cache) used to produce §3, asserting every P0/P1/P2 item is
DONE or explicitly N/A, and that all 16 locales carry every key. The closing
report is the artifact proving "実装漏れ・考慮漏れなし".
