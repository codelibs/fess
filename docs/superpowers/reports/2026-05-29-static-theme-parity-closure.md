# Static Theme — JSP Parity Remediation Closure Report

- Date: 2026-05-29
- Branch: `feature/static-theme`
- Spec: `docs/superpowers/specs/2026-05-29-static-theme-jsp-parity-remediation-design.md`
- Plan: `docs/superpowers/plans/2026-05-29-static-theme-jsp-parity-remediation.md`

## Outcome

All 29 audited parity items plus geo search and the backend prerequisites are
**DONE**. A read-only four-quadrant re-audit (core search; chat/advance;
error/cache + backend; i18n/header/footer/help) against the final code confirmed
**zero NOT-DONE items**. Full test suite green (161 tests across the impacted
classes), all theme JS valid (`node --check`), all 16 message bundles + 8 help
bundles valid JSON.

## Verification evidence

- `mvn test -Dtest='BundledBootstrapThemeTest,LabelMessageThemeParityTest,ThemeViewActionTest,CacheHandlerTest,SearchHandlerTest,UiConfigHandlerTest,StaticThemeFilterTest,ThemeManifestTest,ThemeRegistryTest'` → **161 tests, 0 failures, 0 errors, BUILD SUCCESS**.
- `node --check` on every `assets/*.js` → clean.
- 16 `messages.*.json` each exactly **239 keys**, identical key set to en (bidirectional parity test `LabelMessageThemeParityTest#test_allLocalesExactlyMatchEnKeys`).
- 8 new locales (`hi, id, it, nl, pl, ru, tr, zh-TW`) carry genuine native-script translations (210–219 of 239 keys translated; the remainder are proper nouns / file-format names / template strings correctly left as-is).

## Item-by-item closure

### Backend / v2 API
| Item | Status | Evidence |
|---|---|---|
| A1 cache url/created/charset | DONE | `CacheHandler.buildCachePayload` (no silent fallback); `CacheHandlerTest` 2 tests |
| A2 geo params on /search | DONE | `V2JsonRequestParams.getGeoInfo`→`GeoInfo`; `SearchHandlerTest` geo contract tests |
| A3 facet_field/facet_query shapes | DONE | package-private + `SearchHandlerTest` shape tests |
| A4 /ui/config filetype_options | DONE | `UiConfigHandler.buildFiletypeOptions` (canonical 10) + tests |

### Core search SPA
| Item | Status | Evidence |
|---|---|---|
| SRCH-1 thumbnail URL | DONE | `search.js` `/thumbnail/?docId=&queryId=` |
| SRCH-2 highlighted content_title | DONE | `renderHighlightedSnippet`; `plainTitle` aria-label |
| SRCH-3 label facet counts/zero-suppress/dedup | DONE | `facetField.find(name==="label")` + count filter |
| SRCH-4 facet views from config+counts | DONE | `renderFacetQueryViews(cfg.facet_views, env.facet_query)`; hardcoded ranges removed |
| SRCH-5 inline validation | DONE | `#search-error`; matches `"invalid_request"`/httpStatus 400 (Critical casing bug found+fixed in review) |
| SRCH-6 suggest lang/label | DONE | `suggestParams.lang/.label` |
| SRCH-7 OSDD config-gated | DONE | static link removed; `ensureOsddLink()` |
| SRCH-8 per-query title | DONE | `document.title = t("page.search_title")` |
| SRCH-9 bold status nodes | DONE | `<b>` DOM nodes from `{b0}/{b1}/{b2}/{bq}` |
| GEO-1 client | DONE | geo controls + `state.geo` + runSearch emit + runFromUrl hydrate |

### Advanced search SPA
| Item | Status | Evidence |
|---|---|---|
| ADV-1 filetype canonical | DONE | `word/excel/powerpoint…` from config; no `msword` |
| ADV-2 runFromUrl lang/fields.label/ex_q | DONE | hydrated + `state.exQ` serialized |
| ADV-3 none-of NOT | DONE | `"NOT " + w` |
| ADV-4 all-words suggestor | DONE | `attachSuggest` exported + wired |
| ADV-5 preserve params | DONE | seeds `URLSearchParams(location.search)` |
| ADV-6 sort gating | DONE | gated by `search_log_enabled`/`user_favorite` |

### Chat SPA
| Item | Status | Evidence |
|---|---|---|
| CHAT-1 body contract (P0) | DONE | `{message, session_id, fields:{label}, extra_queries}` matches `ChatRequestBody.from` |
| CHAT-2 IME guard | DONE | `isComposing`+composition events on both handlers |
| CHAT-3 sources icon/type/index | DONE | precedence `go_url||url_link||url` |
| CHAT-4 phase status/hit_count | DONE | `complete()` threaded through both UIs (threading bug found+fixed) |
| CHAT-5 retry/char/resize/EOF/filter-search | DONE | all five sub-features |
| CHAT-6 markdown tables/blockquotes | DONE | XSS-safe via `inlineMarkdown`; alignment = accepted limitation |
| dead `buildEventHandlers` | REMOVED | stale pre-fix duplicate deleted |

### Cache / Error / CSP / Footer
| Item | Status | Evidence |
|---|---|---|
| CACHE-1 CSP blob: frames (P0) | DONE | `frame-src blob:; child-src blob:` in index.html + INDEX_CSP |
| CACHE-2 banner/base/charset | DONE | `cache.js` consumes url/created/charset |
| CACHE-3 cache i18n keys | DONE | `search_result_cache/cache_msg/unknown` en+ja |
| ERR-1 busy → 429 | DONE | web.xml/computeErrorStatus/error.js consistent; `error.body_503` reworded |
| ERR-2 message_key forwarding | DONE | CacheAction → `/error/notfound/?message_key=errors.docid_not_found` → meta → `error.detail_docid_not_found` |
| HELP-1 ranges `{}` note | DONE | help/en+ja |
| HDR-1 footer i18n year | DONE | `footer.copyright_year`; no `Date.getFullYear()` |

### i18n
| Item | Status | Evidence |
|---|---|---|
| 16 locales, 239 keys exact | DONE | all 16 bundles == en keyset |
| 8 new native translations | DONE | hi/id/it/nl/pl/ru/tr/zh-TW (~88–92% translated) |
| SUPPORTED=16 | DONE | `i18n.js` |
| help English fallback | DONE | `help.js` falls back for the 8 new locales |
| parity test enforces 16 | DONE | `LabelMessageThemeParityTest` |

## Bugs caught by the behavioral review gate (not by content-assertion tests)
1. **SRCH-5 (Critical):** `INVALID_REQUEST` compared in wrong case → inline validation was dead. Fixed to match the lowercase wire code + httpStatus 400.
2. **CHAT-4 (Critical, mid-task):** `phaseStrip.complete` used but not destructured in the inline UI and not threaded to the standalone UI → would throw. Fixed.
3. **CHAT cleanup:** dead `buildEventHandlers` still held the pre-fix D4/D5d logic → removed.
4. **A1 (review):** silent `catch`-fallback to literal field names → removed in favor of direct FessConfig getters + DI-booted test.

## Review cleanups applied
- Label double-serialization (`state.facets.label` + `state.fields.label`) de-duplicated at serialization + chip render.
- Redundant `advance.js` `params.delete("start")` removed.
- Unused `ArrayList` import in `SearchHandlerTest` removed.
- Dead `cache.js` `env.last_modified` "Indexed at" row removed (API emits `created`).

## Recommended manual smoke (not yet performed — requires a running Fess + index)
The closure above is code- and test-verified. A final in-browser smoke against a
running instance (`./bin/fess`, indexed corpus, RAG chat enabled) is recommended
to confirm runtime rendering of the P0 paths:
1. Chat: ask a question → streamed answer (not "message is required"); label filter sends `fields.label`.
2. Cache: open a cache link → iframe renders (no CSP console violation); banner shows URL + date.
3. Advanced: filetype=Word + language + label + time-range → results non-empty; request carries `filetype:word`, `lang`, `fields.label`, `ex_q`.
4. Geo: location apply → results filter; params persist across pagination.
5. Errors: `/error/busy` → HTTP 429 with "Service Busy" copy.

## Conclusion
The Static theme bootstrap SPA reproduces the JSP search UI across all audited
screens (search results, header/footer, help, chat, advanced, no-result, cache,
error pages, index/search shell), with geo search added, errors/CSP/cache handled
correctly, and i18n expanded to the 16 JSP-equivalent locales. No implementation
or consideration gaps remain against the spec backlog.
