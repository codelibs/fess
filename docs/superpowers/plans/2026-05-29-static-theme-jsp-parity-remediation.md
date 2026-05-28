# Static Theme — Full JSP Parity Remediation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Bring the bundled `bootstrap` static theme (SPA over `/api/v2`) to full feature parity with the legacy JSP search UI, closing all 17 audited parity defects (P0–P2) plus geo search, across 16 i18n locales.

**Architecture:** The theme is an SPA shell (`index.html` + ES modules in `assets/`) consuming `/api/v2`. Server-side error dispatch (`web.xml` → `redirect.jsp` → `ThemeViewAction`) is preserved; the SPA reproduces presentation. Fixes span backend handlers (`api/v2/handlers/`), SPA JS, CSP (theme + `ThemeViewAction`), and i18n bundles.

**Tech Stack:** Java 21 + LastaFlute + DBFlute/OpenSearch (backend); vanilla ES modules + Bootstrap 5 (SPA); JUnit (UTFlute/Mockito) tests; no JS test runner.

**Spec:** `docs/superpowers/specs/2026-05-29-static-theme-jsp-parity-remediation-design.md`

---

## Preamble — read before executing

### Test classes (authoritative — the spec's "StaticThemeParityTest" does NOT exist)
- **`src/test/java/org/codelibs/fess/theme/BundledBootstrapThemeTest.java`** — reads bundled theme files via `Files.readString(THEME_DIR.resolve(...))` and asserts `contains(...)`/`!contains(...)`. **ALL JS/HTML/CSS/JSON content assertions in Phases B–E go here.** (If a phase section below says "StaticThemeParityTest", use `BundledBootstrapThemeTest` instead — do not create a new class.)
- **`src/test/java/org/codelibs/fess/theme/LabelMessageThemeParityTest.java`** — i18n key-parity across locales. Extended in Phase F to enforce all 16 locales.
- **`src/test/java/org/codelibs/fess/app/web/theme/ThemeViewActionTest.java`** — server-side CSP / HTTP status / meta-injection. Extended in Phases A/E.
- Backend handler tests: `src/test/java/org/codelibs/fess/api/v2/...` (path mirrors `org/codelibs/fess/api/v2/handlers/`).

### Dependency order
`A → (B, C, D, E in parallel) → F → G`. Phase F must run after B–E because it back-fills the new i18n keys those phases introduce. Within B–E, each task is independently committable.

### Verified facts that shaped the plan (do not re-derive)
- **Geo (A2/B10/C2)** is already backend-supported: `/api/v2/search` parses `geo.{field}.point=<lat>,<lon>` + `geo.{field}.distance=<value>` per field in `query.geo.fields` (default `location`) via `V2JsonRequestParams.getGeoInfo()` → `GeoInfo`; invalid points → `INVALID_REQUEST` (surfaced inline by B5). GEO-1 is **client-only** + contract tests.
- **Virtual host (A3)** is applied server-side for JSON search requests (`QueryHelper.buildVirtualHostQuery`) — N/A on the client.
- **`facet_views` / `facet_field` / `facet_query` (A3)** are already emitted; shapes locked by A3 tests, consumed by B3/B4.
- **`filetype_options` (A4)** is the only missing config piece; A4 adds it. C1 ships a correct fallback so it is independently correct.
- **i18n net-new keys:** only **8** are genuinely new (`footer.copyright_org`, `footer.copyright_year`, `labels.chat_source_fallback`, `labels.chat_source_type_document`, `labels.chat_filter_search_placeholder`, `labels.search_result_cache`, `labels.search_cache_msg`, `labels.search_unknown`). Three (`search_result_cache`, `search_cache_msg`, `search_unknown`) have authoritative `fess_label_*.properties` translations for all 16 locales. The other keys each phase introduces (e.g. `page.search_title`, `search.geo*`, retokenized `labels.search_result_status*`) are added to en+ja in their phase and back-filled in F3.

### Conventions
- XSS discipline: build DOM with `createElement`/`textContent`; the only sanctioned `innerHTML` is server-highlighted fields routed through `format.js renderHighlightedSnippet` (B2/B9) and the sandboxed cache iframe (E2).
- Run `mvn formatter:format && mvn license:format` before each phase's final commit (CLAUDE.md).
- Every new user-facing string is added to `messages.en.json` AND `messages.ja.json` in its phase (the en/ja parity test fails otherwise); Phase F back-fills the other 14 locales.

---
## Phase A — Backend / v2 API

This section delivers the backend prerequisites for Phases B–E. All four tasks operate on the **verified** handler path `org/codelibs/fess/api/v2/handlers/`. Findings folded inline:

- **GEO-1 (A2) is already wired end-to-end.** `V2JsonRequestParams.getGeoInfo()` overrides `SearchRequestParams.createGeoInfo`, constructing `GeoInfo(request)`; `SearchHandler.handle` calls `searchHelper.search(params, …)`. A2 is **document + prove**, not implement.
- **A3 virtual host is already applied** (`QueryHelper.buildVirtualHostQuery` for non-`ADMIN_SEARCH`; `/api/v2/search` is `SearchRequestType.JSON`); `facet_field`/`facet_query` (`SearchHandler`) and `facet_views` (`UiConfigHandler`) already emitted. A3 is **document + lock with a test**.
- **A4 sort gating already exists** (`UiConfigHandler.buildSortOptions`). Missing is the filetype option list — A4 adds it.

**Files:**

| Path | Change |
|---|---|
| `src/main/java/org/codelibs/fess/api/v2/handlers/CacheHandler.java` | A1 — add `url`/`created`/`charset` via extracted `buildCachePayload` |
| `src/test/java/org/codelibs/fess/api/v2/handlers/CacheHandlerTest.java` | A1 — unit test for `buildCachePayload` |
| `src/test/java/org/codelibs/fess/api/v2/handlers/SearchHandlerTest.java` | A2 — geo contract; A3 — facet shape lock |
| `src/main/java/org/codelibs/fess/api/v2/handlers/UiConfigHandler.java` | A4 — `filetype_options` via `buildFiletypeOptions` |
| `src/test/java/org/codelibs/fess/api/v2/handlers/UiConfigHandlerTest.java` | A4 — unit test |

### Task A1 — Extend `/api/v2/cache` with `url`, `created`, `charset` (CACHE-2)

The `doc` map from `getDocumentByDocId(...)` already contains `url`/`created`/`mimetype` (`QueryFieldConfig` cache response fields); `createCacheContent` injects `url_link`. There is no charset index field (the hbs view hardcodes UTF-8), so derive `charset` from `mimetype`. Extract a pure package-private `buildCachePayload(docId, content, doc)` for testability.

- [ ] **Write the failing test** in `CacheHandlerTest.java`:
```java
    @Test
    public void test_buildCachePayload_includesUrlCreatedCharset() {
        final java.util.Map<String, Object> doc = new java.util.HashMap<>();
        doc.put("url", "https://example.com/doc.html");
        doc.put("url_link", "https://example.com/doc.html");
        doc.put("created", "2026-05-29T10:15:30.000+0000");
        doc.put("mimetype", "text/html; charset=Shift_JIS");
        final java.util.Map<String, Object> payload = new CacheHandler().buildCachePayload("abc123", "<html>cached</html>", doc);
        assertEquals("abc123", payload.get("doc_id"));
        assertEquals("text/html", payload.get("mimetype"));
        assertEquals("<html>cached</html>", payload.get("content"));
        assertEquals("https://example.com/doc.html", payload.get("url"));
        assertEquals("2026-05-29T10:15:30.000+0000", payload.get("created"));
        assertEquals("Shift_JIS", payload.get("charset"));
    }

    @Test
    public void test_buildCachePayload_charsetDefaultsToUtf8WhenAbsent() {
        final java.util.Map<String, Object> doc = new java.util.HashMap<>();
        doc.put("url", "https://example.com/a.pdf");
        doc.put("mimetype", "application/pdf");
        final java.util.Map<String, Object> payload = new CacheHandler().buildCachePayload("d1", "x", doc);
        assertEquals("UTF-8", payload.get("charset"));
        assertEquals("https://example.com/a.pdf", payload.get("url"));
        assertNull(payload.get("created"));
    }
```
- [ ] **Run, confirm red:** `mvn test -Dtest=CacheHandlerTest#test_buildCachePayload_includesUrlCreatedCharset` → compile failure (`cannot find symbol: buildCachePayload`).
- [ ] **Implement.** Replace the inline payload assembly in `CacheHandler.java` (`payload.put("doc_id"...)` block) with `V2EnvelopeWriter.writeSuccess(res, buildCachePayload(docId, content, doc));` and add:
```java
    Map<String, Object> buildCachePayload(final String docId, final String content, final Map<String, Object> doc) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("doc_id", docId);
        payload.put("mimetype", "text/html");
        payload.put("content", content);
        final String urlLink = DocumentUtil.getValue(doc, fessConfig.getResponseFieldUrlLink(), String.class);
        final String url = StringUtil.isNotBlank(urlLink) ? urlLink : DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
        if (StringUtil.isNotBlank(url)) {
            payload.put("url", url);
        }
        final String created = DocumentUtil.getValue(doc, fessConfig.getIndexFieldCreated(), String.class);
        if (StringUtil.isNotBlank(created)) {
            payload.put("created", created);
        }
        payload.put("charset", parseCharset(DocumentUtil.getValue(doc, fessConfig.getIndexFieldMimetype(), String.class)));
        return payload;
    }

    private static String parseCharset(final String mimetype) {
        if (StringUtil.isNotBlank(mimetype)) {
            final int idx = mimetype.toLowerCase(java.util.Locale.ROOT).indexOf("charset=");
            if (idx >= 0) {
                final String cs = mimetype.substring(idx + "charset=".length()).trim();
                final int sc = cs.indexOf(';');
                final String result = (sc >= 0 ? cs.substring(0, sc) : cs).trim();
                if (StringUtil.isNotBlank(result)) {
                    return result;
                }
            }
        }
        return org.codelibs.fess.Constants.UTF_8;
    }
```
  Add imports `org.codelibs.fess.mylasta.direction.FessConfig`, `org.codelibs.fess.util.DocumentUtil`.
- [ ] **Run, confirm green:** `mvn test -Dtest=CacheHandlerTest` → `Failures: 0, Errors: 0`.
- [ ] **Commit:** `git commit -am "feat(api/v2): expose url/created/charset in /api/v2/cache payload (CACHE-2)"`

### Task A2 — Geo contract: document + prove (GEO-1 backend)

**Param contract (consumed by B10/C2):** geo fields = `query.geo.fields` (default `location`). Client sends per field: `geo.{field}.point=<lat>,<lon>` (decimal degrees, multi-valued OR'd) and `geo.{field}.distance=<value+unit>` (e.g. `10km`). A point without distance is ignored; malformed point → `InvalidQueryException` → `400 invalid_request`. Multiple fields AND; multiple points on one field OR. Default field `location`.

- [ ] **Add geo contract tests** to `SearchHandlerTest.java`:
```java
    @Test
    public void test_geoInfo_validPoint_buildsGeoQuery() {
        final java.util.Map<String, String[]> params = new java.util.LinkedHashMap<>();
        params.put("geo.location.point", new String[] { "35.681,139.767" });
        params.put("geo.location.distance", new String[] { "10km" });
        final V2JsonRequestParams p = new V2JsonRequestParams(new GeoStubRequest("GET", params), ComponentUtil.getFessConfig());
        final org.codelibs.fess.entity.GeoInfo geo = p.getGeoInfo();
        assertNotNull(geo);
        assertNotNull(geo.toQueryBuilder());
    }

    @Test
    public void test_geoInfo_malformedPoint_throwsInvalidQuery() {
        final java.util.Map<String, String[]> params = new java.util.LinkedHashMap<>();
        params.put("geo.location.point", new String[] { "not-a-point" });
        params.put("geo.location.distance", new String[] { "10km" });
        final V2JsonRequestParams p = new V2JsonRequestParams(new GeoStubRequest("GET", params), ComponentUtil.getFessConfig());
        try {
            p.getGeoInfo();
            fail("malformed geo point must raise InvalidQueryException");
        } catch (final org.codelibs.fess.exception.InvalidQueryException expected) {
        }
    }
```
  Add a `GeoStubRequest` (copy the minimal `StubRequest` pattern from `CacheHandlerTest`, overriding `getParameterMap()` to return the map and `getParameter(name)` to return `map.get(name)[0]`).
- [ ] **Run, confirm green:** `mvn test -Dtest=SearchHandlerTest#test_geoInfo_validPoint_buildsGeoQuery+test_geoInfo_malformedPoint_throwsInvalidQuery`. (If "geo fields not configured", assert `getQueryGeoFieldsAsArray()` contains `location`.)
- [ ] **Commit:** `git commit -am "test(api/v2): lock /api/v2/search geo param contract (GEO-1 backend)"`

### Task A3 — Lock facet shapes; document vhost (Phase B dependency)

Shapes Phase B consumes: `facet_field` = `[{name, result:[{value, count}]}]`; `facet_query` = `[{value, count}]`; `facet_views` (from `/ui/config`) = `[{group_name, queries:[{label_key, value}]}]`. Join: `facet_views.queries[].value === facet_query[].value`; label facet joins `facet_field[name==label]` against `/ui/config label_options`.

- [ ] **Demote `buildFacetField`/`buildFacetQuery` to package-private** (drop `private`) in `SearchHandler.java` (same pattern as `UiConfigHandler.buildSortOptions`) and add to `SearchHandlerTest.java`:
```java
    @Test
    public void test_buildFacetField_shape() {
        final org.codelibs.fess.util.FacetResponse fr = mock(org.codelibs.fess.util.FacetResponse.class);
        final org.codelibs.fess.util.FacetResponse.Field field = mock(org.codelibs.fess.util.FacetResponse.Field.class);
        final java.util.Map<String, Long> vc = new java.util.LinkedHashMap<>();
        vc.put("label1", 42L);
        when(field.getName()).thenReturn("label");
        when(field.getValueCountMap()).thenReturn(vc);
        when(fr.getFieldList()).thenReturn(java.util.List.of(field));
        final java.util.List<java.util.Map<String, Object>> out = new SearchHandler().buildFacetField(fr);
        assertEquals(1, out.size());
        assertEquals("label", out.get(0).get("name"));
        final java.util.List<?> result = (java.util.List<?>) out.get(0).get("result");
        final java.util.Map<?, ?> first = (java.util.Map<?, ?>) result.get(0);
        assertEquals("label1", first.get("value"));
        assertEquals(42L, first.get("count"));
    }

    @Test
    public void test_buildFacetQuery_shape() {
        final org.codelibs.fess.util.FacetResponse fr = mock(org.codelibs.fess.util.FacetResponse.class);
        final java.util.Map<String, Long> qc = new java.util.LinkedHashMap<>();
        qc.put("filetype:pdf", 17L);
        when(fr.getQueryCountMap()).thenReturn(qc);
        final java.util.List<java.util.Map<String, Object>> out = new SearchHandler().buildFacetQuery(fr);
        assertEquals(1, out.size());
        assertEquals("filetype:pdf", out.get(0).get("value"));
        assertEquals(17L, out.get(0).get("count"));
    }
```
  (Verify the repo's mocking facility — UTFlute ships Mockito; if mocking `FacetResponse` is awkward, construct real instances if public setters exist.)
- [ ] **Run, confirm green:** `mvn test -Dtest=SearchHandlerTest#test_buildFacetField_shape+test_buildFacetQuery_shape`.
- [ ] **Commit:** `git commit -am "test(api/v2): lock facet_field/facet_query shapes; document vhost + facet_views (Phase B dep)"`

### Task A4 — Expose canonical `filetype_options` from `/ui/config` (ADV-1 source)

Canonical values must match `index.filetype`/`labels.facet_filetype_*`: `html, word, excel, powerpoint, odt, ods, odp, pdf, txt, others`.

- [ ] **Write failing test** in `UiConfigHandlerTest.java`:
```java
    @Test
    public void test_filetypeOptions_canonicalValuesMatchIndexMapping() {
        final java.util.List<Map<String, Object>> opts = new UiConfigHandler().buildFiletypeOptions();
        final java.util.List<String> values = new java.util.ArrayList<>();
        for (final Map<String, Object> o : opts) {
            values.add((String) o.get("value"));
            assertNotNull(o.get("label_key"));
        }
        assertEquals(java.util.List.of("html", "word", "excel", "powerpoint", "odt", "ods", "odp", "pdf", "txt", "others"), values);
    }

    @Test
    public void test_filetypeOptions_presentInSuccessPayload() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new UiConfigHandler().handle(new StubRequest("GET", "/api/v2/ui/config"), res);
        assertEquals(200, res.status);
        assertTrue(res.body().contains("\"filetype_options\""));
    }
```
- [ ] **Run, confirm red:** `mvn test -Dtest=UiConfigHandlerTest#test_filetypeOptions_canonicalValuesMatchIndexMapping` → compile failure.
- [ ] **Implement** in `UiConfigHandler.java` (next to `buildSortOptions`):
```java
    List<Map<String, Object>> buildFiletypeOptions() {
        final List<Map<String, Object>> list = new ArrayList<>();
        for (final String type : new String[] { "html", "word", "excel", "powerpoint", "odt", "ods", "odp", "pdf", "txt", "others" }) {
            final Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("value", type);
            entry.put("label_key", "labels.facet_filetype_" + type);
            list.add(entry);
        }
        return list;
    }
```
  Add to payload after the `facet_views` line: `payload.put("filetype_options", buildFiletypeOptions());`
- [ ] **Run, confirm green:** `mvn test -Dtest=UiConfigHandlerTest`.
- [ ] **Commit:** `git commit -am "feat(api/v2): expose canonical filetype_options from /ui/config (ADV-1 source)"`

### Phase A finalization
- [ ] `mvn test -Dtest=CacheHandlerTest+SearchHandlerTest+UiConfigHandlerTest+V2JsonRequestParamsTest` → BUILD SUCCESS.
- [ ] `mvn formatter:format && mvn license:format`; commit any formatting diffs.

**Phase A exit criteria:** `/api/v2/cache` returns `url`/`created`/`charset`; geo contract documented + tested; facet shapes locked; `/ui/config` exposes `filetype_options`.
---
## Phase B — Core search SPA

Implements SRCH-1..9 + GEO-1 client against `src/main/webapp/themes/bootstrap/`. XSS rule preserved: no `innerHTML` with dynamic data. Two safe-HTML exceptions: (a) server-highlighted fields via `renderHighlightedSnippet` (B2), (b) our own trusted i18n templates rendered as DOM nodes (B9).

Dependency: A2 (geo) and A3 (facet shapes) already satisfied server-side; B consumes the documented shapes.

**Files:** `assets/search.js` (B1–B10), `assets/format.js` (reused), `index.html` (B5 alert, B8 OSDD, B10 geo), `assets/styles.css` (B5/B10), `i18n/messages.{en,ja}.json` (new keys), `BundledBootstrapThemeTest.java` (assertions).

Note: every new string is added to en+ja here; the existing en≡ja parity test enforces both land together. Phase F back-fills the other 14 locales.

### Task B1 — Thumbnail URL (SRCH-1)
`thumbnail` is an index keyword, not a URL. JSP builds `/thumbnail/?docId=&queryId=`.

Current (`search.js` ~146-155): `img.setAttribute("src", safeHref(d.thumbnail));`
Replacement:
```js
  if (d.thumbnail && features.thumbnail_enabled) {
    const img = document.createElement("img");
    img.className = "result-thumbnail";
    img.setAttribute("loading", "lazy");
    img.setAttribute("alt", "");
    img.setAttribute("src",
      "/thumbnail/?docId=" + encodeURIComponent(d.doc_id || "") +
      "&queryId=" + encodeURIComponent(queryId || ""));
    img.addEventListener("error", () => { img.classList.add("d-none"); });
    li.appendChild(img);
  }
```
- [ ] Replace the thumbnail block.
- [ ] Add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_searchJs_thumbnailUsesThumbnailEndpoint() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("/thumbnail/?docId="));
        assertTrue(js.contains("&queryId="));
        assertFalse(js.contains("safeHref(d.thumbnail)"));
    }
```
- [ ] Manual: `?q=test` with thumbnails enabled → `<img.result-thumbnail src="/thumbnail/?docId=...&queryId=...">` renders (HTTP 200).
- [ ] Commit: `fix(theme): build /thumbnail/ URL from docId+queryId (SRCH-1)`

### Task B2 — Highlighted `content_title` (SRCH-2)
Server emits `content_title` already highlighted (escaped first, `<strong>`/`<em>` after; `response.field.content_title=content_title`). Route through the existing `renderHighlightedSnippet` whitelist (same as the snippet path).

Current (`search.js` ~166-177): `const a = el("a", { text: d.title || d.url || "", ... });`
Replacement:
```js
  const h2 = el("h2");
  const a = el("a", {
    attrs: { href: goHref, title: safeHref(originalUrl) !== "#" ? originalUrl : "" },
    dataset: { resultLink: "1" }
  });
  if (d.content_title) {
    a.innerHTML = renderHighlightedSnippet(d.content_title); // server-highlighted, whitelist-sanitized
  } else {
    a.textContent = d.title || d.url || "";
  }
  h2.appendChild(a);
  body.appendChild(h2);
```
Add helper:
```js
function plainTitle(d) {
  const raw = d.content_title || d.title || d.url || "";
  return String(raw).replace(/<\/?(?:strong|em)>/g, "");
}
```
and set the More-link aria-label to `t("labels.search_result_more") + " - " + plainTitle(d)`.
- [ ] Add `plainTitle()`, replace heading-anchor block, update More-link aria-label.
- [ ] Add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_searchJs_usesHighlightedContentTitle() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("renderHighlightedSnippet(d.content_title)"));
        assertTrue(js.contains("a.textContent = d.title || d.url"));
    }
```
- [ ] Manual: `?q=fess` → matched term bold in heading; title with literal `<` shows as text.
- [ ] Commit: `fix(theme): render highlighted content_title in result heading (SRCH-2)`

### Task B3 — Label facet from facet response (SRCH-3)
Build from response `facet_field[name==label]` with counts + zero-suppression, de-dup against `/labels`; skip `label` in the generic loop.

Replace `renderFacets` steps 1–2 (`search.js` ~1064-1078):
```js
  const facetField = env.facet_field || [];
  const labelValueSet = new Set((labels || []).map(l => l.value));
  const labelTitleByValue = new Map((labels || []).map(l => [l.value, l.label]));
  const labelField = facetField.find(f => f.name === "label");
  if (labelField) {
    const entries = (labelField.result || [])
      .filter(r => Number(r.count) > 0 && labelValueSet.has(r.value))
      .map(r => ({ labelText: labelTitleByValue.get(r.value) || r.value, value: r.value, count: r.count }));
    if (entries.length > 0) {
      body.appendChild(buildFacetGroup(t("labels.facet_label_title"), entries, "label"));
    }
  }
  for (const field of facetField) {
    if (field.name === "filetype" || field.name === "label") continue;
    const entries = (field.result || []).map(r => ({ labelText: r.value, value: r.value, count: r.count }));
    if (entries.length > 0) {
      body.appendChild(buildFacetGroup(field.name, entries, field.name));
    }
  }
```
Keep `loadLabels()` call (now the de-dup/title source).
- [ ] Replace steps 1–2; confirm `labels` still threaded into `renderFacets`.
- [ ] Add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_searchJs_labelFacetFromResponseWithZeroSuppress() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("facetField.find(f => f.name === \"label\")"));
        assertTrue(js.contains("Number(r.count) > 0 && labelValueSet.has(r.value)"));
        assertFalse(js.contains("if (labels.length > 0) {"));
    }
```
- [ ] Manual: only non-zero labels appear with count badges, no duplicates; zero-match query → no label group.
- [ ] Commit: `fix(theme): build label facet from response counts with zero-suppress + de-dup (SRCH-3)`

### Task B4 — Facet query views from server config + counts (SRCH-4)
Replace hardcoded ranges with `cfg.facet_views` + `env.facet_query` counts.

Keep `TIMESTAMP_RANGES`/`SIZE_RANGES`/`FILETYPE_VALUES` ONLY as the `ex_q` serialization map (mark with a comment); stop rendering groups from them. Replace `renderFacets` steps 3–5:
```js
  renderFacetQueryViews(body, env);
```
Add:
```js
function renderFacetQueryViews(body, env) {
  const cfg = api.getConfig() || {};
  const views = cfg.facet_views || [];
  const countByValue = {};
  (env.facet_query || []).forEach(fq => { countByValue[fq.value] = fq.count; });
  views.forEach(view => {
    const groupTitleKey = view.group_name || "";
    const title = groupTitleKey.startsWith("labels.") ? t(groupTitleKey) : groupTitleKey;
    const queries = (view.queries || []).filter(qy => Number(countByValue[qy.value]) > 0);
    if (queries.length === 0) return;
    const group = el("div", { className: "facet-group" });
    group.appendChild(el("h3", { text: title }));
    queries.forEach(qy => {
      const active = (state.facetQueries || []).includes(qy.value);
      const item = el("button", {
        className: "facet-item btn btn-link p-0 text-start w-100" + (active ? " active" : ""),
        attrs: { type: "button", "aria-pressed": active ? "true" : "false" }
      });
      const label = qy.label_key && qy.label_key.startsWith("labels.") ? t(qy.label_key) : (qy.label_key || qy.value);
      item.appendChild(el("span", { text: label }));
      item.appendChild(el("span", { className: "badge bg-secondary", text: String(countByValue[qy.value]) }));
      item.addEventListener("click", () => {
        const arr = state.facetQueries ? [...state.facetQueries] : [];
        const i = arr.indexOf(qy.value);
        if (i >= 0) arr.splice(i, 1); else arr.push(qy.value);
        state.facetQueries = arr;
        state.start = 0;
        runSearch();
      });
      group.appendChild(item);
    });
    body.appendChild(group);
  });
}
```
Add `facetQueries: []` to `state`; in `runSearch` replace the bespoke `timestampRange`/`sizeRange` ex_q blocks with:
```js
    if (Array.isArray(state.facetQueries) && state.facetQueries.length > 0) {
      params["ex_q"] = params["ex_q"] || [];
      if (!Array.isArray(params["ex_q"])) params["ex_q"] = [params["ex_q"]];
      state.facetQueries.forEach(v => params["ex_q"].push(v));
    }
```
Update `renderActiveChips`, `facet-clear` handler, and the `anyActive` check to use `state.facetQueries`; delete dead `TIMESTAMP_RANGES`/`SIZE_RANGES`/`buildRangeFacetGroup`/`state.timestampRange`/`state.sizeRange`. (Filetype facet deletion deferred to C1.)
- [ ] Apply all of the above.
- [ ] Add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_searchJs_facetViewsFromServerConfig() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("cfg.facet_views"));
        assertTrue(js.contains("env.facet_query"));
        assertFalse(js.contains("const TIMESTAMP_RANGES"));
        assertFalse(js.contains("const SIZE_RANGES"));
    }
```
- [ ] Manual: facet groups match JSP titles with count badges; zero groups hidden; click filters + chip; clear restores.
- [ ] Commit: `fix(theme): render facet query views from server config + counts (SRCH-4)`

### Task B5 — Inline validation feedback (SRCH-5)
Surface `ApiError.message` on `INVALID_REQUEST`.

`index.html` — add after `#results-notification` (and `#home-notification`):
```html
      <div id="search-error" class="alert alert-danger d-none mb-2" role="alert" aria-live="assertive"></div>
```
Replace the `runSearch` catch (`search.js` ~517-525):
```js
  } catch (e) {
    if (e && e.name === "AbortError") return;
    const meta = document.getElementById("results-meta");
    const errBox = document.getElementById("search-error");
    if (e && e.code === "INVALID_REQUEST") {
      if (errBox) {
        errBox.textContent = e.message || t("error.invalid_request");
        errBox.classList.remove("d-none");
      } else {
        meta.textContent = e.message || t("error.invalid_request");
      }
      return;
    }
    if (errBox) errBox.classList.add("d-none");
    if (e && e.name === "NetworkError") {
      meta.textContent = t("error.network");
    } else {
      meta.textContent = e.code === "AUTH_REQUIRED" ? t("error.auth_required") : t("error.server");
    }
  }
```
After `renderResults(env);` (success path) add: `const errBox = document.getElementById("search-error"); if (errBox) errBox.classList.add("d-none");` (`error.invalid_request` already exists; no new key).
- [ ] Add `#search-error` alert(s); replace catch; clear on success.
- [ ] Add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_indexHtml_hasInlineSearchErrorRegion() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        assertTrue(html.contains("id=\"search-error\""));
    }
    @Test
    public void test_searchJs_surfacesInvalidRequestMessage() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("e.code === \"INVALID_REQUEST\""));
        assertTrue(js.contains("errBox.textContent = e.message"));
    }
```
- [ ] Manual: `?q=test&start=-1` → red alert with server message; valid search hides it.
- [ ] Commit: `fix(theme): surface INVALID_REQUEST message inline (SRCH-5)`

### Task B6 — Suggest forwards lang/label (SRCH-6)
Replace the `/suggest-words` call (`search.js` ~558):
```js
    const suggestParams = { q, num: 10, fn: ["_default", "content", "title"] };
    if (Array.isArray(state.lang) && state.lang.length > 0) suggestParams.lang = state.lang;
    const labelFilters = state.fields.label || [];
    if (labelFilters.length > 0) suggestParams.label = labelFilters;
    const env = await api.get("/suggest-words", suggestParams);
```
- [ ] Replace; add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_searchJs_suggestForwardsLangAndLabel() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("suggestParams.lang = state.lang"));
        assertTrue(js.contains("suggestParams.label = labelFilters"));
    }
```
- [ ] Manual: pick lang+label, type → `/suggest-words` request carries `lang=`/`label=`.
- [ ] Commit: `fix(theme): forward lang/label to /suggest-words (SRCH-6)`

### Task B7 — Per-query `document.title` (SRCH-8)
In `runSearch`, after `state.requestedTime = ...`:
```js
    document.title = state.q ? t("page.search_title").replace("{0}", state.q) : "Fess";
```
Add `"page.search_title": "{0} - Fess"` to en + ja.
- [ ] Add key (en+ja); set `document.title`; add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_searchJs_setsPerQueryDocumentTitle() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("document.title = state.q ? t(\"page.search_title\")"));
    }
```
- [ ] Manual: `?q=fess` → tab title `fess - Fess`; home → `Fess`.
- [ ] Commit: `feat(theme): set per-query document.title (SRCH-8)`

### Task B8 — OSDD `<link>` config-gated (SRCH-7)
Remove the static `<link rel="search" ...>` from `index.html` (lines 13-14). Inject after config loads (in `attach()` after the config check):
```js
  ensureOsddLink();
```
```js
function ensureOsddLink() {
  const cfg = api.getConfig();
  if (!cfg) return;
  if (document.querySelector('link[rel="search"]')) return;
  const link = document.createElement("link");
  link.setAttribute("rel", "search");
  link.setAttribute("type", "application/opensearchdescription+xml");
  link.setAttribute("title", cfg.site_name || "Fess");
  link.setAttribute("href", "/osdd");
  document.head.appendChild(link);
}
```
(If Phase A later adds `features.osdd_enabled`, gate on it — one line.)
- [ ] Remove static link; add `ensureOsddLink()`; add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_indexHtml_noStaticOsddLink() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        assertFalse(html.contains("rel=\"search\""));
    }
    @Test
    public void test_searchJs_injectsOsddLinkGatedOnConfig() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("function ensureOsddLink()"));
        assertTrue(js.contains("application/opensearchdescription+xml"));
    }
```
- [ ] Manual: head contains the OSDD link after config load; absent if `/ui/config` fails.
- [ ] Commit: `fix(theme): gate OSDD search link on config availability (SRCH-7)`

### Task B9 — Results status-line emphasis (SRCH-9)
Re-tokenize i18n to `{b0}`=count `{b1}`=start `{b2}`=end `{bq}`=query and render `<b>` via DOM nodes (no innerHTML).

en: `"labels.search_result_status": "Results {b1} - {b2} of {b0} for {bq}"`, `"labels.search_result_status_over": "Results {b1} - {b2} of more than {b0} for {bq}"` (ja mirrors existing ordering).
Replace `renderResultsStatus` (`search.js` ~293-317):
```js
function renderResultsStatus(env) {
  const statusEl = document.getElementById("results-status");
  if (!statusEl) return;
  while (statusEl.firstChild) statusEl.removeChild(statusEl.firstChild);
  const count = env.record_count || 0;
  const start = env.start_record_number || 1;
  const end   = env.end_record_number   || 0;
  const q     = state.q || "";
  const isOver = env.record_count_relation && env.record_count_relation !== "EQUAL_TO";
  const statusKey = isOver ? "labels.search_result_status_over" : "labels.search_result_status";
  const values = { b0: String(count), b1: String(start), b2: String(end), bq: q };
  t(statusKey).split(/(\{b[012q]\})/).forEach(part => {
    const m = part.match(/^\{(b[012q])\}$/);
    if (m) {
      const b = document.createElement("b");
      b.textContent = values[m[1]] != null ? values[m[1]] : "";
      statusEl.appendChild(b);
    } else if (part) {
      statusEl.appendChild(document.createTextNode(part));
    }
  });
  if (env.exec_time != null) {
    const execSec = typeof env.exec_time === "number" ? env.exec_time.toFixed(2)
      : (typeof env.query_time === "number" ? (env.query_time / 1000).toFixed(2) : null);
    if (execSec !== null) {
      statusEl.appendChild(document.createTextNode(" " + t("labels.search_result_time").replace("{0}", execSec)));
    }
  }
}
```
- [ ] Re-tokenize en+ja; replace `renderResultsStatus`; add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_searchJs_statusLineUsesBoldNodes() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("document.createElement(\"b\")"));
        assertFalse(js.contains("statusEl.textContent = text"));
    }
```
- [ ] Manual: status reads "Results **1** - **10** of **42** for **fess**"; `?q=a<b` renders bold literal text.
- [ ] Commit: `fix(theme): bold emphasis in results status via DOM nodes (SRCH-9)`

### Task B10 — Geo search client (GEO-1)
Reproduce the single-field (`location`) geo controls; state serializes on every `/search` request (propagates across pagination/facets); `runFromUrl` hydrates from URL.

Add to `state`: `geo: { lat: "", lon: "", distance: "" },`
`index.html` — add inside `#search-options` (geo dropdown with `#geo-lat`, `#geo-lon`, `#geo-distance`, `#geo-apply`, `#geo-clear`, `data-i18n="search.geo*"`).
`runSearch` — after ex_q/facetQueries serialization:
```js
    if (state.geo && state.geo.lat !== "" && state.geo.lon !== "" && state.geo.distance !== "") {
      params["geo.location.point"] = state.geo.lat + "," + state.geo.lon;
      params["geo.location.distance"] = state.geo.distance;
    }
```
`attach()` listeners:
```js
  const geoApply = document.getElementById("geo-apply");
  if (geoApply) geoApply.addEventListener("click", () => {
    state.geo = {
      lat: (document.getElementById("geo-lat").value || "").trim(),
      lon: (document.getElementById("geo-lon").value || "").trim(),
      distance: (document.getElementById("geo-distance").value || "").trim()
    };
    state.start = 0;
    runSearch();
  });
  const geoClear = document.getElementById("geo-clear");
  if (geoClear) geoClear.addEventListener("click", () => {
    state.geo = { lat: "", lon: "", distance: "" };
    ["geo-lat", "geo-lon", "geo-distance"].forEach(id => { const el = document.getElementById(id); if (el) el.value = ""; });
    state.start = 0;
    runSearch();
  });
```
Also clear `state.geo` in `facet-clear`. Extend `runFromUrl` (coordinate with C2 — same function) to hydrate geo from `geo.location.point`/`geo.location.distance`. Add en+ja keys `search.geo`, `search.geo_lat`, `search.geo_lon`, `search.geo_distance`, `search.geo_apply`, `search.geo_clear`. Invalid input → server `INVALID_REQUEST` (surfaced by B5).
- [ ] Apply state/index.html/runSearch/listeners/runFromUrl/i18n.
- [ ] Add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_indexHtml_hasGeoControls() throws Exception {
        final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
        assertTrue(html.contains("id=\"geo-lat\"") && html.contains("id=\"geo-lon\"") && html.contains("id=\"geo-distance\""));
        assertTrue(html.contains("id=\"geo-apply\""));
    }
    @Test
    public void test_searchJs_emitsAndHydratesGeoParams() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("\"geo.location.point\""));
        assertTrue(js.contains("\"geo.location.distance\""));
        assertTrue(js.contains("params.get(\"geo.location.point\")"));
    }
```
- [ ] Manual: apply lat/lon/10km → results filter; params persist on pagination/facet; deep-link pre-fills; invalid point → B5 alert.
- [ ] Commit: `feat(theme): geo search controls + param propagation (GEO-1 client)`

### Phase B wrap-up
- [ ] `mvn test -Dtest=BundledBootstrapThemeTest` green; en≡ja parity intact; no backslash-escaped paths.
- [ ] Phase F note: back-fill `page.search_title`, retokenized `labels.search_result_status`/`_over`, and `search.geo*` to the other 14 locales.
---
## Phase C — Advanced search SPA

Closes ADV-1..6. P0: C1 (filetype matches zero docs), C2 (advanced filters dropped). C1 depends on A4 (filetype_options) but ships a correct fallback so it is independently right.

**Files:** `assets/advance.js`, `assets/search.js` (C2 runFromUrl + C4 export `attachSuggest`), `i18n/messages.{en,ja}.json`, `BundledBootstrapThemeTest.java`. Read-only refs: `UiConfigHandler.java`, `fess_config.properties` (canonical filetype `index.filetype`/`labels.facet_filetype_*`), `util/QueryStringBuilder.java` (server none-of → `NOT word`).

### Task C1 — Filetype values from config (ADV-1, P0)
Replace hardcoded `msword/msexcel/mspowerpoint` (`advance.js` ~215-226) with canonical values, sourced from `/ui/config filetype_options` with a correct static fallback:
```js
const FILETYPE_OPTIONS_FALLBACK = [
  { value: "",           labelKey: "advance.any_filetype" },
  { value: "html",       labelKey: "labels.facet_filetype_html" },
  { value: "word",       labelKey: "labels.facet_filetype_word" },
  { value: "excel",      labelKey: "labels.facet_filetype_excel" },
  { value: "powerpoint", labelKey: "labels.facet_filetype_powerpoint" },
  { value: "odt",        labelKey: "labels.facet_filetype_odt" },
  { value: "ods",        labelKey: "labels.facet_filetype_ods" },
  { value: "odp",        labelKey: "labels.facet_filetype_odp" },
  { value: "pdf",        labelKey: "labels.facet_filetype_pdf" },
  { value: "txt",        labelKey: "labels.facet_filetype_txt" },
  { value: "others",     labelKey: "labels.facet_filetype_others" },
];
function buildFiletypeOptions(serverConfig) {
  const fromServer = serverConfig && Array.isArray(serverConfig.filetype_options) ? serverConfig.filetype_options : null;
  if (fromServer && fromServer.length > 0) {
    return [
      { value: "", labelKey: "advance.any_filetype" },
      ...fromServer.map(o => ({
        value: o.value != null ? o.value : "",
        labelKey: o.label_key || undefined,
        label: o.label_key ? undefined : (o.label || o.value || ""),
      })),
    ];
  }
  return FILETYPE_OPTIONS_FALLBACK.slice();
}
```
Rewire the filetype select (`advance.js` ~269-273) to `buildFiletypeOptions(serverConfig)`; move `const serverConfig = getConfig() || {};` above the filetype block. Verify `advance.any_filetype` exists in en/ja (add if missing: en "Any file type" / ja "すべてのファイル形式"); `labels.facet_filetype_*` already exist.
- [ ] Apply; add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_advanceJs_filetypeCanonicalValues() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/advance.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("word") && js.contains("excel") && js.contains("powerpoint"));
        assertFalse(js.contains("msword"));
        assertFalse(js.contains("msexcel"));
        assertFalse(js.contains("mspowerpoint"));
        assertTrue(js.contains("filetype_options"));
    }
```
- [ ] Manual: `/advance` filetype=Word + q=test → URL `q=test+filetype%3Aword`, non-empty results for a Word doc.
- [ ] Commit: `fix(theme): advance filetype uses canonical word/excel/powerpoint from config (ADV-1)`

### Task C2 — `runFromUrl()` honors lang/fields.label/ex_q/geo (ADV-2, P0)
`advance.js` already appends these; `search.js runFromUrl()` (~789-801) ignores them. Replace:
```js
export function runFromUrl() {
  const params = new URLSearchParams(location.search);
  const q = params.get("q");
  if (!q) return;
  state.q = q;
  state.start = Number(params.get("start")) || 0;
  const numVal = Number(params.get("num"));
  if (numVal > 0) state.num = numVal;
  state.sort = params.get("sort") || "";
  state.lang = params.getAll("lang").filter(v => v !== "");
  state.fields = {};
  for (const [key, value] of params.entries()) {
    if (key.startsWith("fields.") && value !== "") {
      const field = key.slice("fields.".length);
      (state.fields[field] = state.fields[field] || []).push(value);
    }
  }
  state.exQ = params.getAll("ex_q").filter(v => v !== "");
  state.geo = {};
  for (const [key, value] of params.entries()) {
    if (key.startsWith("geo.") && value !== "") state.geo[key] = value;
  }
  syncSearchInputs(state.q);
  runSearch();
}
```
> Coordinate with B10: B10 used a `{lat,lon,distance}` geo object hydrated from `geo.location.point`/`.distance`. Reconcile to ONE representation — recommended: keep B10's `{lat,lon,distance}` object and in C2 hydrate it the same way (parse `geo.location.point`), NOT a raw `geo.*` map. Use the B10 `runFromUrl` body and ADD the `lang`/`fields.*`/`ex_q` reads to it. The `runSearch` geo serialization (B10) stays; for `ex_q` add the serialization block below.
Add `exQ: []` to `state` (B4 added `facetQueries`; B10 added `geo`). In `runSearch` before `api.get("/search", params)`:
```js
    if (Array.isArray(state.exQ) && state.exQ.length > 0) {
      params["ex_q"] = params["ex_q"] || [];
      if (!Array.isArray(params["ex_q"])) params["ex_q"] = [params["ex_q"]];
      params["ex_q"].push(...state.exQ);
    }
```
`state.fields` is already serialized by the existing loop, so `fields.label` is honored once populated.
- [ ] Apply (reconciled with B10); add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_searchJs_runFromUrlHonorsAdvancedParams() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("getAll(\"lang\")"));
        assertTrue(js.contains("fields."));
        assertTrue(js.contains("getAll(\"ex_q\")"));
    }
```
- [ ] Manual: `/advance` set lang=ja + label + Time=past week → request carries `lang=ja`, `fields.label=`, `ex_q=last_modified:[...]`.
- [ ] Commit: `fix(theme): runFromUrl forwards lang/fields.label/ex_q to search request (ADV-2)`

### Task C3 — None-of uses `NOT word` (ADV-3)
Replace (`advance.js` ~163-167):
```js
  if (parts.none) {
    const tokens = tokenize(parts.none);
    out.push(...tokens.filter(Boolean).map(w => "NOT " + w));
  }
```
- [ ] Replace; add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_advanceJs_noneOfUsesNot() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/advance.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("\"NOT \" + w"));
        assertFalse(js.contains("\"-\" + w"));
    }
```
- [ ] Manual: none-of `draft confidential` → `q=report NOT draft NOT confidential`.
- [ ] Commit: `fix(theme): advance none-of emits NOT word matching QueryStringBuilder (ADV-3)`

### Task C4 — Suggestor on advanced all-words field (ADV-4)
Export a reusable `attachSuggest(input, dropdown, opts)` from `search.js` (after `hideSuggest`), reusing the `/suggest-words` shape and createElement/textContent rendering:
```js
export function attachSuggest(input, dropdown, opts = {}) {
  if (!input || !dropdown) return;
  let timer = null;
  const clear = () => {
    while (dropdown.firstChild) dropdown.removeChild(dropdown.firstChild);
    dropdown.classList.add("d-none");
    input.setAttribute("aria-expanded", "false");
  };
  const choose = (text) => { input.value = text; clear(); input.focus(); };
  const render = async (q) => {
    if (!q || q.length < 1) { clear(); return; }
    try {
      const params = { q, num: 10, fn: ["_default", "content", "title"] };
      if (Array.isArray(opts.lang) && opts.lang.length > 0) params.lang = opts.lang;
      const env = await api.get("/suggest-words", params);
      const items = env.suggest_words || [];
      while (dropdown.firstChild) dropdown.removeChild(dropdown.firstChild);
      if (items.length === 0) { clear(); return; }
      items.forEach((it, i) => {
        const li = document.createElement("li");
        li.className = "list-group-item";
        li.setAttribute("role", "option");
        li.id = input.id + "-suggest-" + i;
        li.textContent = it.text || "";
        li.addEventListener("mousedown", (e) => { e.preventDefault(); choose(it.text || ""); });
        dropdown.appendChild(li);
      });
      dropdown.classList.remove("d-none");
      input.setAttribute("aria-expanded", "true");
    } catch { /* best-effort */ }
  };
  input.addEventListener("input", () => {
    if (timer) clearTimeout(timer);
    const v = input.value.trim();
    timer = setTimeout(() => render(v), 150);
  });
  input.addEventListener("blur", () => setTimeout(clear, 120));
}
```
In `advance.js` add `import { attachSuggest } from "./search.js";` and after the all-words field + lang select exist (before submit listener):
```js
  const allSuggest = document.createElement("ul");
  allSuggest.className = "list-group suggest-dropdown d-none";
  allSuggest.id = "adv-all-suggest";
  allSuggest.setAttribute("role", "listbox");
  fAll.wrap.appendChild(allSuggest);
  attachSuggest(fAll.input, allSuggest, {
    get lang() { return Array.from(fLang.input.selectedOptions).map(o => o.value).filter(v => v !== ""); },
  });
```
- [ ] Apply; ensure `.suggest-dropdown` styling exists (reuse header suggest style). Add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_advanceSuggestWired() throws Exception {
        final String s = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
        final String a = Files.readString(THEME_DIR.resolve("assets/advance.js"), StandardCharsets.UTF_8);
        assertTrue(s.contains("export function attachSuggest"));
        assertTrue(a.contains("attachSuggest(") && a.contains("from \"./search.js\""));
    }
```
- [ ] Manual: `/advance` type 2 chars in all-words → suggest dropdown (`/suggest-words` request); lang selection adds `lang=`; click fills input; text-only render.
- [ ] Commit: `feat(theme): wire suggestor to advance all-words field (ADV-4)`

### Task C5 — Preserve existing query params (ADV-5)
Replace `advance.js` ~402-403:
```js
    const params = new URLSearchParams(location.search);
    for (const key of ["q", "start", "num", "sort", "lang", "ex_q"]) params.delete(key);
    for (const key of Array.from(params.keys())) { if (key.startsWith("fields.")) params.delete(key); }
    params.delete("start");
    if (q) params.set("q", q);
```
- [ ] Replace; add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_advanceJs_preservesParams() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/advance.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("new URLSearchParams(location.search)"));
        assertTrue(js.contains("startsWith(\"fields.\")"));
    }
```
- [ ] Manual: `/advance?keep=1&debug=on` submit → URL keeps `keep=1&debug=on`; re-submit doesn't accumulate `lang`/`fields.label`/`ex_q`.
- [ ] Commit: `fix(theme): advance preserves unrelated query params on submit (ADV-5)`

### Task C6 — Sort options gated by config (ADV-6)
Server `buildSortOptions` already gates click/favorite. Make the SPA fallback (used when `sort_options` empty) gated by feature flags. Replace `advance.js` ~348-359 with the gated-fallback block:
```js
  const features = serverConfig.features || {};
  const searchLogEnabled = !!features.search_log_enabled;
  const favoriteEnabled = !!features.user_favorite;
  const sortFallback = [
    { value: "score.desc",          label_key: "labels.search_result_sort_score_desc" },
    { value: "filename.asc",        label_key: "labels.search_result_sort_filename_asc" },
    { value: "filename.desc",       label_key: "labels.search_result_sort_filename_desc" },
    { value: "created.asc",         label_key: "labels.search_result_sort_created_asc" },
    { value: "created.desc",        label_key: "labels.search_result_sort_created_desc" },
    { value: "content_length.asc",  label_key: "labels.search_result_sort_content_length_asc" },
    { value: "content_length.desc", label_key: "labels.search_result_sort_content_length_desc" },
    { value: "last_modified.asc",   label_key: "labels.search_result_sort_last_modified_asc" },
    { value: "last_modified.desc",  label_key: "labels.search_result_sort_last_modified_desc" },
  ];
  if (searchLogEnabled) sortFallback.push(
    { value: "click_count.asc",  label_key: "labels.search_result_sort_click_count_asc" },
    { value: "click_count.desc", label_key: "labels.search_result_sort_click_count_desc" });
  if (favoriteEnabled) sortFallback.push(
    { value: "favorite_count.asc",  label_key: "labels.search_result_sort_favorite_count_asc" },
    { value: "favorite_count.desc", label_key: "labels.search_result_sort_favorite_count_desc" });
  const sortOptsRaw = serverConfig.sort_options && serverConfig.sort_options.length > 0 ? serverConfig.sort_options : sortFallback;
  const sortOpts = [
    { value: "", label: t("labels.advance_search_sort_default") },
    ...sortOptsRaw.map(o => ({ value: o.value != null ? o.value : "", label: t(o.label_key || o.value || "") })),
  ];
  const fSort = makeSelect("labels.advance_search_sort", "adv-sort", sortOpts);
```
Confirm `labels.search_result_sort_*`, `labels.advance_search_sort`, `labels.advance_search_sort_default` exist in en/ja (add the two `advance_search_sort*` if missing).
- [ ] Apply; add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_advanceJs_sortGatedByConfig() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/advance.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("search_log_enabled") && js.contains("user_favorite"));
        assertTrue(js.contains("click_count.desc") && js.contains("favorite_count.desc"));
    }
```
- [ ] Manual: `/advance` sort dropdown lists score/filename/created/size/last_modified + click/favorite; disable favorites → those entries disappear.
- [ ] Commit: `fix(theme): advance sort options gated by search-log/favorite config (ADV-6)`

**Phase C exit criteria:** advance emits canonical filetype, NOT none-of, all-words suggestor, preserved params, gated sort; `runFromUrl` round-trips lang/fields.label/ex_q/geo. `BundledBootstrapThemeTest` green. Run format/license before the phase's final commit.
---
## Phase D — Chat SPA

Closes CHAT-1..6. Two UIs share `submitQuestion()` (chat.js:722) — D1/D4 land there and apply to both. Legacy `webapp/js/chat.js` is the gold reference; backend contract is `api/v2/handlers/ChatRequestBody.java` + `ChatStreamHandler.java` (do NOT loosen). **All content assertions go into `BundledBootstrapThemeTest.java`** (the spec's "StaticThemeParityTest" does not exist).

**Files:** `assets/chat.js` (primary), `assets/api.js` (D5d eof), `assets/markdown.js` (D6), `assets/format.js` (read-only allow-list), `assets/styles.css` (D3/D5b), `i18n/messages.{en,ja}.json`, `BundledBootstrapThemeTest.java`.

### Task D1 (P0) — Fix chat request body (CHAT-1)
Backend reads `message`, `session_id`, nested `fields.label`, `extra_queries`; blank `message` → "message is required". Replace `submitQuestion` body (chat.js:751-754):
```js
  const filters = getFilters ? getFilters() : { fields: [], extraQ: [] };
  const body = { message: question };
  if (sessionId) body.session_id = sessionId;
  if (filters.fields.length > 0) body.fields = { label: filters.fields.slice() };
  if (filters.extraQ.length > 0) body.extra_queries = filters.extraQ.slice();
```
`sseStream` call (854) unchanged. `getFilters()` returning `{fields, extraQ}` is correct input; verify `data-filter-type` label→fields, ex_q→extraQ.
- [ ] Apply; verify against `ChatRequestBody.from`. Add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_chatRequestBodyContract() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("message: question"));
        assertTrue(js.contains("fields = { label:"));
        assertTrue(js.contains("extra_queries"));
        assertFalse(js.contains("body = { question"));
        assertFalse(js.contains("body.extra_q "));
    }
```
- [ ] Manual: ask a question → answer streams (not INVALID_REQUEST); payload `{"message":"…"}`; label filter → `"fields":{"label":[…]}`.
- [ ] Commit: `fix(theme): chat sends message/fields.label/extra_queries per v2 contract (CHAT-1)`

### Task D2 — IME composition guard (CHAT-2)
Both keydown handlers submit on plain Enter without `isComposing`. For BOTH the inline handler (chat.js ~958-968) and standalone (chat.js ~1190-1201), add a composing flag + guard:
```js
  let <scope>Composing = false;
  <el>.addEventListener("compositionstart", () => { <scope>Composing = true; });
  <el>.addEventListener("compositionend", () => { <scope>Composing = false; });
  <el>.addEventListener("keydown", ev => {
    if (ev.key === "Enter" && !ev.shiftKey && !ev.isComposing && !<scope>Composing) {
      ev.preventDefault();
      ... existing submit body ...
    }
  });
```
(inline: `<el>`=`input`, `<scope>`=`inline`; standalone: `<el>`=`textarea`, `<scope>`=`standalone`.)
- [ ] Apply both; add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_chatImeGuard() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("ev.isComposing"));
        assertTrue(js.contains("compositionstart") && js.contains("compositionend"));
        assertTrue(js.contains("!ev.shiftKey && !ev.isComposing"));
    }
```
- [ ] Manual (IME): Enter confirming a candidate does NOT send; second Enter sends; Latin Enter sends immediately.
- [ ] Commit: `fix(theme): guard chat Enter during IME composition (CHAT-2)`

### Task D3 — Sources: icon + type + index, correct precedence (CHAT-3)
Precedence `go_url || url_link || url`. Replace `appendSources` (chat.js:412-433) to render numbered cards with `sourceIcon`/`sourceTypeLabel` (port from legacy chat.js:827-876), createElement/textContent only, href via `safeHref`. Add the two helpers after `safeHref`. Add new keys `labels.chat_source_fallback` ("(source)"/"(出典)"), `labels.chat_source_type_document` ("Document"/"ドキュメント"). Add `.source-card/.source-index/.source-info/.source-title/.source-meta/.source-type` CSS.
(See spec/legacy for exact `sourceIcon`/`sourceTypeLabel` extension→class/label maps: pdf→fa-file-pdf-o/PDF, doc(x)→Word, xls(x)→Excel, ppt(x)→PowerPoint, image exts→fa-file-image-o, html→fa-globe/Web, default→fa-file-o/Document.)
- [ ] Apply helpers + appendSources + i18n + CSS. Add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_chatSourcesPrecedence() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("src.go_url || src.url_link || src.url"));
        assertTrue(js.contains("source-index") && js.contains("sourceIcon(") && js.contains("sourceTypeLabel("));
        assertFalse(js.contains("src.url || src.go_url"));
    }
```
- [ ] Manual: question with sources → numbered cards with icon/type/title; link target uses `go_url` when present.
- [ ] Commit: `feat(theme): chat sources show icon/type/index with go_url||url_link||url precedence (CHAT-3)`

### Task D4 — Phase model: status start|complete + hit_count (CHAT-4)
Backend emits `status:"start"` then `status:"complete"` (with `hit_count` on search). Add `complete(phase, hitCount)` to `buildPhaseStrip` and return it; drive from `data.status`.
Add inside `buildPhaseStrip`:
```js
  function complete(phase, hitCount) {
    const idx = PHASE_ORDER.indexOf(phase);
    if (idx === -1) return;
    const b = badges[phase];
    b.classList.remove("bg-primary", "bg-secondary");
    b.classList.add("bg-success");
    if (phase === "search" && hitCount != null) {
      const old = b.querySelector(".chat-hit-count");
      if (old) old.remove();
      const countText = t("labels.chat_hit_count", { count: hitCount });
      b.appendChild(el("span", { className: "chat-hit-count ms-1 small", text: " (" + countText + ")" }));
    }
  }
```
return `{ strip, advanceTo, complete, reset }`. Replace `submitQuestion` phase branch (chat.js:760-766):
```js
  function onEvent({ type, data }) {
    if (type === "phase") {
      const phase = data && data.phase;
      const status = data && data.status;
      const hitCount = data && data.hit_count;
      if (phase) {
        if (status === "complete") {
          phaseStrip.complete(phase, hitCount);
        } else {
          phaseStrip.advanceTo(phase);
          statusLozenge.setStatus("thinking");
        }
      }
      return;
    }
```
Thread `complete` through both `refs.phaseStrip` (chat.js:902/948 and 1098/1167).
- [ ] Apply; add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_chatPhaseStatusModel() throws Exception {
        final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
        assertTrue(js.contains("data.status"));
        assertTrue(js.contains("status === \"complete\""));
        assertTrue(js.contains("phaseStrip.complete("));
        assertTrue(js.contains("data.hit_count"));
    }
```
- [ ] Manual: phase steps go blue (start) → green (complete); search step shows `(N documents found)` from `hit_count`.
- [ ] Commit: `feat(theme): chat phase strip honors status:start|complete + hit_count timing (CHAT-4)`

### Task D5 — Remaining CHAT-5 items (one commit at end)
- **D5a retry interpolation:** replace retry branch (chat.js:820-823) to compute `seconds = Math.max(1, Math.round((data.sleep_ms||0)/1000))` and call `t("labels.chat_retrying", {attempt, max, seconds})`; change `labels.chat_retrying` value to interpolated form (en `"Retrying… ({attempt}/{max}, next attempt in {seconds}s)"`, ja `"再試行中… ({attempt}/{max}、{seconds} 秒後に再試行)"`).
- **D5b char-counter warning/danger:** in the standalone input listener (chat.js:1185-1188) set `CHAT_MAX_LEN=4000`; add/remove `.warning`(≥80%)/`.danger`(≥95%) on `charCountSpan`; clear at reset points (1197/1207/~1237). Add CSS `.char-counter.warning{color:var(--bs-warning)} .char-counter.danger{color:var(--bs-danger)}`.
- **D5c auto-resize:** in the input listener add `textarea.style.height="auto"; textarea.style.height=Math.min(textarea.scrollHeight,150)+"px";`; reset `height="auto"` at clear points; add a minimal inline `input` listener for the inline UI (~968).
- **D5d premature-EOF recovery:** in `api.sseStream` after the read loop + final-frame flush (api.js ~195-197) emit `if (onEvent) onEvent({ type: "eof", data: null });`. In `submitQuestion.onEvent` add an `eof` branch that only acts when `currentStream` is still set (re-enable input/submit; lozenge "ready" if buffer had content else "error" + `errorBanner.show(t("error.server"))`).
- **D5e per-dropdown filter search (≤10 auto-hide):** in `buildFilterPanel` (chat.js:228-256) per group add a `chat-filter-search` input above rows; live-filter rows by case-insensitive substring of label text; `search.hidden = options.length <= 10`. Add key `labels.chat_filter_search_placeholder` (en "Search filters…"/ja "フィルターを検索…").
- [ ] Apply D5a–D5e; add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_chat5Features() throws Exception {
        final String chat = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
        final String api = Files.readString(THEME_DIR.resolve("assets/api.js"), StandardCharsets.UTF_8);
        final String en = Files.readString(THEME_DIR.resolve("i18n/messages.en.json"), StandardCharsets.UTF_8);
        assertTrue(chat.contains("data.sleep_ms"));
        assertTrue(en.contains("{attempt}") && en.contains("{max}") && en.contains("{seconds}"));
        assertTrue(chat.contains("* 0.95") && chat.contains("* 0.8"));
        assertTrue(chat.contains("scrollHeight, 150"));
        assertTrue(api.contains("type: \"eof\"") && chat.contains("type === \"eof\""));
        assertTrue(chat.contains("chat-filter-search") && chat.contains("<= 10"));
    }
```
- [ ] Manual: retry shows `(2/3, next attempt in 5s)`; counter ambers/reds near limit; textarea grows to 150px then scrolls; killing stream re-enables input; >10-item dropdown shows a working filter box, ≤10 hides it.
- [ ] Commit: `feat(theme): chat retry interpolation, char warnings, auto-resize, EOF recovery, per-group filter search (CHAT-5)`

### Task D6 — Markdown tables + blockquotes (CHAT-6)
`format.js sanitizeHtml` already allow-lists TABLE/THEAD/TBODY/TR/TH/TD + BLOCKQUOTE, so generation adds no XSS surface. In `markdown.js processBlock` (before paragraph fallback) add: (1) blockquote (every non-empty line starts `>` → strip `>` + space, `inlineMarkdown` per line, join `<br>`, wrap `<blockquote>`), (2) GFM table (line0 `|…|`, line1 delimiter `---`/`:--`/`:-:`/`--:`, body rows; split on `|` tolerating leading/trailing pipe; each cell via `inlineMarkdown`; emit `<table class="table">…`). Add `BLOCKQUOTE_LINE_RE=/^>\s?(.*)$/` and `TABLE_DELIM_RE=/^\s*\|?\s*:?-{1,}:?\s*(\|\s*:?-{1,}:?\s*)*\|?\s*$/`. **Accepted limitation (decision, not placeholder):** column alignment and nested-block-in-cell are NOT rendered (would require widening td/th attribute allow-list).
- [ ] Apply; add to `BundledBootstrapThemeTest`:
```java
    @Test
    public void test_markdownTablesBlockquote() throws Exception {
        final String md = Files.readString(THEME_DIR.resolve("assets/markdown.js"), StandardCharsets.UTF_8);
        assertTrue(md.contains("<table") && md.contains("<thead>") && md.contains("<tbody>"));
        assertTrue(md.contains("<blockquote>") && md.contains("TABLE_DELIM_RE") && md.contains("inlineMarkdown"));
    }
```
- [ ] Manual: answer with a GFM table + `>` blockquote renders correctly; cell `<`/`&` escaped; cell links get `target=_blank rel`.
- [ ] Commit: `feat(theme): markdown supports GFM tables + blockquotes (CHAT-6); alignment recorded as accepted limitation`

### Phase D wrap-up
- [ ] `mvn test -Dtest=BundledBootstrapThemeTest` green (en≡ja for the new keys; other-locale parity completes in Phase F).
- [ ] `mvn formatter:format && mvn license:format`.
- [ ] New keys for Phase F back-fill: `labels.chat_source_fallback`, `labels.chat_source_type_document`, `labels.chat_filter_search_placeholder`, and the changed `labels.chat_retrying` value.
---
## Phase E — Cache + Error + CSP + security

Closes CACHE-1/2/3, ERR-1/2, HELP-1, HDR-1. Consumes Phase A's cache `url`/`created`/`charset` (verify A1 landed before E2). Some `429` mapping pieces already exist from prior rounds; tasks scope to remaining work + verification. **JS/JSON content assertions → `BundledBootstrapThemeTest`; server-side → `ThemeViewActionTest`.**

**Files:** `index.html` (CSP), `ThemeViewAction.java` (INDEX_CSP), `assets/cache.js`, `assets/error.js`, `assets/app.js` (footer), `i18n/messages.{en,ja}.json`, `help/{en,ja}.json`, `CacheAction.java`, `ErrorAction.java`, `ThemeViewActionTest.java`, `BundledBootstrapThemeTest.java`.

Notes: `t(key, params)` supports `{name}` interpolation and falls back to the raw key on miss. The cache iframe is `blob:`-sourced + sandboxed without `allow-same-origin`/`allow-scripts` — do NOT weaken.

### Task E1 (P0) — CSP allow `blob:` frames (CACHE-1)
- [ ] **E1.1** `index.html` line 5 — add `frame-src blob:; child-src blob:;` before `frame-ancestors`:
  ```html
  <meta http-equiv="Content-Security-Policy" content="default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; connect-src 'self'; frame-src blob:; child-src blob:; frame-ancestors 'none'; base-uri 'self'">
  ```
- [ ] **E1.2** `ThemeViewAction.INDEX_CSP` (~65-66) — same addition:
  ```java
  static final String INDEX_CSP = "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline';"
          + " img-src 'self' data:; connect-src 'self'; frame-src blob:; child-src blob:;"
          + " frame-ancestors 'none'; base-uri 'self'";
  ```
  Update its Javadoc to note blob: framed cache content while keeping `frame-ancestors 'none'`.
- [ ] **E1.3** Extend `ThemeViewActionTest#test_serveIndex_setsCsp_andNoStore_cacheControl`:
  ```java
  final String csp = headers.get("Content-Security-Policy")[0];
  assertTrue(csp.contains("frame-src blob:"), csp);
  assertTrue(csp.contains("child-src blob:"), csp);
  assertTrue(csp.contains("frame-ancestors 'none'"), csp);
  ```
- [ ] **E1.4** Add to `BundledBootstrapThemeTest`:
  ```java
  @Test
  public void test_indexHtml_cspAllowsBlobFrameForCacheViewer() throws Exception {
      final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
      assertTrue(html.contains("frame-src blob:"));
      assertTrue(html.contains("child-src blob:"));
      assertTrue(html.contains("frame-ancestors 'none'"));
  }
  ```
- [ ] **E1.5** Manual: `/cache/?docId=…` → iframe renders, no CSP console violation; SPA still refuses external framing.
- [ ] **E1.6** Commit: `feat(theme): allow blob: cache iframe in CSP (index.html + ThemeViewAction) [CACHE-1]`

### Task E2 — Cache banner + `<base href>` + charset (CACHE-2)
Prerequisite: confirm A1 landed (`grep -n 'payload.put' CacheHandler.java` shows url/created/charset).
- [ ] **E2.1** Banner uses `labels.search_cache_msg` (`{0}`=url, `{1}`=created) — added in E3.
- [ ] **E2.2** Replace `cache.js` ~149-160 to consume `env.url`/`env.created` (fallback `env.last_modified`), render an out-of-iframe banner `alert alert-secondary cache-banner` via `t("labels.search_cache_msg", [url||unknown, created||unknown])` (textContent), and keep the meta rows.
- [ ] **E2.3** Replace `cache.js` ~126 mimetype default with charset-aware derivation:
  ```js
      const charset = env.charset || "utf-8";
      const baseMime = env.mimetype || "text/html";
      const mimetype = /charset=/i.test(baseMime) ? baseMime : baseMime + ";charset=" + charset;
  ```
  And before building the blob (~170-171) inject a `<base href>` into the cached doc only when absent and a url exists (HTML-escape the url for attribute context):
  ```js
      let docHtml = content;
      if (cacheUrl && !/<base\b/i.test(docHtml)) {
        const safeBase = cacheUrl.replace(/&/g,"&amp;").replace(/"/g,"&quot;").replace(/</g,"&lt;").replace(/>/g,"&gt;");
        const baseTag = '<base href="' + safeBase + '">';
        docHtml = /<head\b[^>]*>/i.test(docHtml) ? docHtml.replace(/<head\b[^>]*>/i, m => m + baseTag) : baseTag + docHtml;
      }
      const blob = new Blob([docHtml], { type: mimetype });
      const blobUrl = URL.createObjectURL(blob);
  ```
- [ ] **E2.4** Keep iframe sandbox unchanged; add a comment that `<base>` is safe (no scripts, no parent-origin access).
- [ ] **E2.5** Add to `BundledBootstrapThemeTest`:
  ```java
  @Test
  public void test_cacheJs_rendersBannerAndBaseAndCharset() throws Exception {
      final String js = Files.readString(THEME_DIR.resolve("assets/cache.js"), StandardCharsets.UTF_8);
      assertTrue(js.contains("labels.search_cache_msg"));
      assertTrue(js.contains("env.charset"));
      assertTrue(js.contains("<base href=\""));
  }
  ```
- [ ] **E2.6** Manual: cache of a page with relative assets → banner shows url+date; relative links resolve; Shift_JIS page renders without mojibake.
- [ ] **E2.7** Commit: `feat(theme): cache banner + base href + charset from v2 cache fields [CACHE-2]`

### Task E3 — Missing `labels.search_result_cache` + banner keys (CACHE-3)
- [ ] **E3.1/E3.2** Add to en + ja (after `labels.cache_loading`): `labels.search_result_cache` (en "Cache"/ja "キャッシュ"), `labels.search_cache_msg` (en "This is a cache of {0}. It is a snapshot of the page as it appeared on {1}."/ja equivalent), `labels.search_unknown` (en "Unknown"/ja "不明"). Maintain key order with en.
- [ ] **E3.3** `mvn test -Dtest=BundledBootstrapThemeTest#test_i18nBundles_haveMatchingTopLevelKeysAcrossLocales` (en≡ja).
- [ ] **E3.4** Commit: `feat(theme): add labels.search_result_cache + cache banner i18n (en/ja) [CACHE-3]`

### Task E4 — Busy unified on HTTP 429 (ERR-1)
- [ ] **E4.1** Verify (no change): `web.xml` 429→`redirect.jsp?type=busy`→`/error/busy/`; `computeErrorStatus` busy→429; `error.js PATH_TO_CODE` busy→"429".
- [ ] **E4.2** Confirm `error.js` renders `error.title_429`/`error.body_429` for code 429.
- [ ] **E4.3** Reword `error.body_503` so it is NOT a busy duplicate (en "The service is temporarily unavailable. Please try again later."; ja equivalent); keep `error.title_503`. (503 is reachable only via explicit app 503, not via busy.)
- [ ] **E4.4** Document in commit that `_503` keys are retained for genuine 503.
- [ ] **E4.5** Add `ThemeViewActionTest#test_serveIndex_busy_emitsErrorCodeHeader429` asserting `/error/busy` yields `X-Fess-Error-Code: 429` and httpStatus 429 (mirror the existing temp-theme test scaffolding `newActionWithRequestUri`).
- [ ] **E4.6** Manual: `/error/busy` → "Service Busy" copy, HTTP 429.
- [ ] **E4.7** Commit: `fix(theme): unify busy on HTTP 429, repurpose _503 wording for true service-unavailable [ERR-1]`

### Task E5 — Forward saved `message_key` from cache/error redirects (ERR-2)
- [ ] **E5.1** In `CacheAction.java`, replace both `return redirect(ErrorAction.class);` (docid-not-found sites) with `return redirect2ErrorWithMessageKey("errors.docid_not_found");` and add the helper:
  ```java
  private HtmlResponse redirect2ErrorWithMessageKey(final String messageKey) {
      return newHtmlResponseAsRediret("/error/notfound/?message_key=" + messageKey);
  }
  ```
  (Confirm the exact redirect-builder available on the action superclass by grepping sibling actions; do not guess — use `HtmlResponse.fromRedirectPath(...)` if `newHtmlResponseAsRediret` is unavailable. The key is a fixed code constant, never user input.)
- [ ] **E5.2** Verify `error.detail_docid_not_found` exists in en/ja (it does); `resolveMessageKey` allowlist accepts `errors.docid_not_found`.
- [ ] **E5.3** Confirm no other caller relies on `redirect(ErrorAction.class)` carrying saved messages for the static path.
- [ ] **E5.4** Add `ThemeViewActionTest#test_errorPath_docidNotFoundMessageKey_injectsDetailMeta` asserting the served `/error/notfound?message_key=errors.docid_not_found` body contains `content="errors.docid_not_found"` and status 404 (reuse the existing message-key test scaffolding).
- [ ] **E5.5** Manual: `/cache/?docId=nonexistent` → redirect to `/error/notfound/?message_key=errors.docid_not_found`, HTTP 404, specific detail line shown.
- [ ] **E5.6** Commit: `feat(theme): forward saved message_key from cache redirects to SPA error view [ERR-2]`

### Task E6 — Help ranges `{}` exclusive note (HELP-1)
- [ ] **E6.1/E6.2** In `help/en.json` + `help/ja.json` `ranges` section, append the `{}` exclusive-bound note to the `html` (en: "Square brackets `[ ]` include the bounds; curly braces `{ }` exclude them, e.g. `content_length:{1024 TO 10240}` …"; ja equivalent).
- [ ] **E6.3** Add to `BundledBootstrapThemeTest`:
  ```java
  @Test
  public void test_helpEn_rangesDocumentsExclusiveBounds() throws Exception {
      final String json = Files.readString(THEME_DIR.resolve("help/en.json"), StandardCharsets.UTF_8);
      assertTrue(json.contains("{ }") || json.contains("{1024 TO 10240}"));
  }
  ```
- [ ] **E6.4** Commit: `docs(theme): document {} exclusive range bounds in help (en/ja) [HELP-1]`

### Task E7 — Footer copyright from i18n release year (HDR-1)
Release year is baked into properties at build time (no runtime constant); mirror via i18n, not `Date.getFullYear()`.
- [ ] **E7.1** Re-point `footer.copyright` to `"© {0} {1}."` and add `footer.copyright_org` ("CodeLibs Project") + `footer.copyright_year` ("2026") in en + ja (year identical across locales; bump per release alongside `fess_label_*.properties`).
- [ ] **E7.2** Replace `renderFooterCopyright` (`app.js` ~205-217) to build from `t("footer.copyright_year")` + `t("footer.copyright_org")`, splicing the org as a link via a ` ` sentinel for `{1}` (text nodes around it; no innerHTML). Drop `new Date().getFullYear()`.
- [ ] **E7.3** Add to `BundledBootstrapThemeTest`:
  ```java
  @Test
  public void test_appJs_footerUsesI18nYearNotClientClock() throws Exception {
      final String js = Files.readString(THEME_DIR.resolve("assets/app.js"), StandardCharsets.UTF_8);
      assertTrue(js.contains("footer.copyright_year"));
      assertFalse(js.contains("new Date().getFullYear()"));
  }
  ```
- [ ] **E7.4** Manual: footer reads `© 2026 CodeLibs Project.`, org links to github.com/codelibs, year independent of browser clock.
- [ ] **E7.5** Commit: `feat(theme): render footer copyright from i18n release year, drop client-clock year [HDR-1]`

### Task E8 — Phase E close-out
- [ ] `mvn formatter:format && mvn license:format`
- [ ] `mvn test -Dtest=ThemeViewActionTest,BundledBootstrapThemeTest`
- [ ] Validate touched JSON parses (messages.en/ja, help/en/ja).
- [ ] Hand Phase F the keys added/changed here: `labels.search_result_cache`, `labels.search_cache_msg`, `labels.search_unknown`, `footer.copyright` (re-pointed), `footer.copyright_org`, `footer.copyright_year`, reworded `error.body_503`, help `ranges` change.
- [ ] Commit any formatting-only diffs: `chore(theme): formatter/license after Phase E`
---
## Phase F — i18n locale expansion (8 → 16 locales)

Add the missing 8 locales (`hi, id, it, nl, pl, ru, tr, zh-TW`) and back-fill all net-new B–E keys into all 16 message bundles. Help bundles fall back to English at runtime for new locales (no new help files). BCP-47 file naming: properties `zh_TW`→`messages.zh-TW.json`; `pt_BR`→`pt-BR` (present).

**Net-new keys (8) absent from en today** (must reach all 16): `footer.copyright_org`, `footer.copyright_year`, `labels.chat_source_fallback`, `labels.chat_source_type_document`, `labels.chat_filter_search_placeholder`, `labels.search_result_cache`, `labels.search_cache_msg`, `labels.search_unknown`. The latter three have authoritative `fess_label_*.properties` translations for all 16 locales — source verbatim. (Phase-specific keys like `page.search_title`, `search.geo*`, retokenized `labels.search_result_status*` are added to en+ja in their phases; F3 back-fills them too.)

en today = 224 keys → 232 after F3; every bundle must match exactly. **Ordering rule:** preserve `messages.en.json` key order; append new keys in the listed order. Valid UTF-8 JSON, no trailing commas.

**Files:** new `i18n/messages.{hi,id,it,nl,pl,ru,tr,zh-TW}.json`; edit existing 8 `messages.*.json`; `assets/i18n.js` (SUPPORTED); verify `assets/help.js`; extend `LabelMessageThemeParityTest.java`.

### Task F1 — Create 8 new full message bundles
Procedure per locale L: (1) copy `messages.en.json` as key skeleton (post-F3 keys + order); (2) replace each value from `fess_label_<props>.properties`/`fess_message_<props>.properties` (`props` = `zh_TW` for `zh-TW`, else same token) using the SPA-key→property-key mapping (search labels, pagination, auth, advance fields, sort/lang/facet families, error titles/bodies, `error.detail_*`); (3) SPA-only keys use the F3 translation table; (4) keep brand/format tokens identical to en (`HTML/Word/Excel/PowerPoint/ODT/ODS/ODP/PDF`, `Fess`, `Powered by Fess`); (5) preserve all `{...}` placeholders verbatim; (6) if a property key is missing, fall back to the en value (F5 checks key presence only).

- [ ] Create each of `messages.{hi,id,it,nl,pl,ru,tr,zh-TW}.json` per procedure (sample authoritative values per locale are in `fess_label_<props>.properties`; e.g. hi `labels.search=खोज`, id `Cari`, it `Cerca`, nl `Zoeken`, pl `Szukaj`, ru `Поиск`, tr `Ara`, zh-TW `搜尋`).
- [ ] Validate JSON for all 8: `python3 -c "import json;json.load(open(P))"`.
- [ ] Verify key set + order equals en (run after F3): `python3 -c "import json;a=list(json.load(open(EN)));b=list(json.load(open(L)));print('OK' if a==b else [k for k in a if k not in b])"` → `OK` for all 8.

### Task F2 — Help: English runtime fallback (NO new files)
Decision (b): do NOT author help for the 8 new locales — no authoritative source, highest risk, least-visited page. `help.js` already falls back to `help/en.json` locale-agnostically.
- [ ] Confirm no `help/<L>.json` created for the 8 new locales.
- [ ] Verify `help.js` fallback covers them (re-read; the `catch` fetches `help/en.json`). No edit. Record this.
- [ ] (Guard) F5 must NOT assert per-locale help existence for new locales.

### Task F3 — Back-fill 8 net-new keys into all 16 bundles
Append (after `labels.cache_loading`) in this order: `footer.copyright_org`, `footer.copyright_year`, `labels.search_result_cache`, `labels.search_cache_msg`, `labels.search_unknown`, `labels.chat_source_fallback`, `labels.chat_source_type_document`, `labels.chat_filter_search_placeholder`. `search_result_cache`/`search_cache_msg`/`search_unknown` sourced from properties; the rest from this table. `footer.copyright_year` value `"© {year}"` (keep placeholder).

Per-locale snippet (append to each bundle; the 8 new F1 files include these by construction):

**en:** `"footer.copyright_org":"CodeLibs Project","footer.copyright_year":"© {year}","labels.search_result_cache":"Cache","labels.search_cache_msg":"This is a cache of {0}. It is a snapshot of the page as it appeared on {1}.","labels.search_unknown":"Unknown","labels.chat_source_fallback":"Source","labels.chat_source_type_document":"Document","labels.chat_filter_search_placeholder":"Filter…"`
**ja:** `…"Cache"→"キャッシュ","…cache_msg":"このページは {0} のキャッシュです。{1} に存在していたページのスナップショットです。","…search_unknown":"不明","…chat_source_fallback":"出典","…chat_source_type_document":"ドキュメント","…chat_filter_search_placeholder":"絞り込み…"`
**de:** cache "Cache", msg "Dies ist ein Cache von {0}. Es ist ein Schnappschuss der Seite, wie sie am {1} erschien.", unknown "Unbekannt", source "Quelle", doc "Dokument", filter "Filtern…"
**es:** "Caché", "Esta es una caché de {0}. Es una instantánea de la página tal como apareció en {1}.", "Desconocido", "Fuente", "Documento", "Filtrar…"
**fr:** "Cache", "Ceci est un cache de {0}. C'est un instantané de la page telle qu'elle est apparue le {1}.", "Inconnu", "Source", "Document", "Filtrer…"
**ko:** "캐시", "이 페이지는 {0}의 캐시입니다. {1}에 존재했던 페이지의 스냅샷입니다.", "알 수 없음", "출처", "문서", "필터…"
**pt-BR:** "Cache", "Este é um cache de {0}. É um instantâneo da página como ela apareceu em {1}.", "Desconhecido", "Fonte", "Documento", "Filtrar…"
**zh-CN:** "缓存", "此页面是 {0} 的缓存。它是 {1} 时页面的快照。", "未知", "来源", "文档", "筛选…"
**hi:** "कैश", "यह {0} का कैश है। यह पृष्ठ का एक स्नैपशॉट है जैसा कि यह {1} पर दिखाई दिया था।", "अज्ञात", "स्रोत", "दस्तावेज़", "फ़िल्टर…"
**id:** "Tembolok", "Ini adalah cache dari {0}. Ini adalah snapshot halaman seperti yang muncul pada {1}.", "Tidak Diketahui", "Sumber", "Dokumen", "Saring…"
**it:** "Cache", "Questa è una cache di {0}. È un'istantanea della pagina come appariva il {1}.", "Sconosciuto", "Fonte", "Documento", "Filtra…"
**nl:** "Cache", "Dit is een cache van {0}. Het is een momentopname van de pagina zoals deze verscheen op {1}.", "Onbekend", "Bron", "Document", "Filteren…"
**pl:** "Pamięć podręczna", "To jest pamięć podręczna {0}. Jest to migawka strony, która pojawiła się {1}.", "Nieznany", "Źródło", "Dokument", "Filtruj…"
**ru:** "Кэш", "Это кэш {0}. Это снимок страницы, как она выглядела {1}.", "Неизвестно", "Источник", "Документ", "Фильтр…"
**tr:** "Önbellek", "Bu, {0} adresinin önbelleğidir. {1} tarihinde göründüğü şekliyle sayfanın bir anlık görüntüsüdür.", "Bilinmeyen", "Kaynak", "Belge", "Süz…"
**zh-TW:** "快取", "此頁面是 {0} 的快取。它是 {1} 時頁面的快照。", "未知", "來源", "文件", "篩選…"
(`footer.copyright_org`="CodeLibs Project" and `footer.copyright_year`="© {year}" for ALL locales.)

Also handle value-change-only keys across all 16 (en/ja owned by their phase; new bundles get the final value): `labels.chat_retrying` interpolated form (en/ja real, others = en value, keep `{attempt}/{max}/{seconds}`); `footer.copyright`="© {0} {1}." (HDR-1); reworded `error.body_503` (ERR-1); plus the phase-introduced keys (`page.search_title`, `search.geo*`, retokenized `labels.search_result_status*`) — translate or fall back to en, key must exist in all 16.
- [ ] Edit the 8 existing bundles: append the 8-key snippet (+ phase keys); set value-change keys.
- [ ] Confirm the 8 F1 files contain all keys; re-sync if F1 ran first.
- [ ] JSON-validate all 16; re-run the 232-key order-equality check.

### Task F4 — Register 16 locales
- [ ] `i18n.js` SUPPORTED → `["de","en","es","fr","hi","id","it","ja","ko","nl","pl","pt-BR","ru","tr","zh-CN","zh-TW"]`. `pickLocale()` already does case-insensitive exact + primary-subtag fallback (zh-TW/zh-CN resolve correctly); no alias map needed.
- [ ] Verify `help.js` has no own SUPPORTED list and falls back to en for the 8 new locales. No edit; record.

### Task F5 — Extend parity test to 16 locales
Edit `LabelMessageThemeParityTest.java`:
- [ ] `ALL_LOCALES` = the 16; `OTHER_LOCALES` = all but en.
- [ ] Keep `test_enJaParity`; point `test_allOtherLocalesHaveAllEnKeys`/`test_chatKeysInAllBundles` at the wider list.
- [ ] Add exact-parity (both directions):
  ```java
  @Test
  public void test_allLocalesExactlyMatchEnKeys() throws Exception {
      final Set<String> enKeys = new TreeSet<>(loadBundle("en").keySet());
      for (final String locale : ALL_LOCALES) {
          final Set<String> localeKeys = new TreeSet<>(loadBundle(locale).keySet());
          final Set<String> missing = new TreeSet<>(enKeys); missing.removeAll(localeKeys);
          final Set<String> extra = new TreeSet<>(localeKeys); extra.removeAll(enKeys);
          assertTrue(missing.isEmpty() && extra.isEmpty(),
                  "messages." + locale + ".json differs — missing=" + missing + ", extra=" + extra);
      }
  }
  @Test
  public void test_allSixteenMessageBundlesExist() throws Exception {
      for (final String locale : ALL_LOCALES) {
          assertTrue(Files.isRegularFile(I18N_DIR.resolve("messages." + locale + ".json")));
      }
  }
  ```
- [ ] Add a help section-id parity test scoped to existing help files only (so new English-fallback locales aren't required): assert every `help/*.json` present has the same ordered `sections[].id` list as `help/en.json`.
- [ ] Run: `mvn test -Dtest=LabelMessageThemeParityTest` green; then `mvn test -Dtest=BundledBootstrapThemeTest`.

**Phase F exit criteria:** 16 valid `messages.*.json`, identical 232-key sets in en order; 8 net-new + value-change + phase keys consistent across all 16; `i18n.js` lists 16; help English-fallback verified; parity test extended + green.
---

## Phase G — Verification (the user's re-check requirement)

**Files:**
- Aggregate test run across all extended/new test classes.
- `docs/superpowers/reports/2026-05-29-static-theme-parity-closure.md` (new — closing report).

### Task G1 — Full theme + handler test suite green
- [ ] **Format + license** (covers all phases' Java/JSON): `mvn formatter:format && mvn license:format`
- [ ] **Run the full impacted test set:**
  `mvn test -Dtest=BundledBootstrapThemeTest,LabelMessageThemeParityTest,ThemeViewActionTest,CacheHandlerTest,SearchHandlerTest,UiConfigHandlerTest,StaticThemeFilterTest`
  Expected: `BUILD SUCCESS`, `Failures: 0, Errors: 0`.
- [ ] **Validate every touched JSON bundle parses** (16 messages + 8 help):
  `for f in src/main/webapp/themes/bootstrap/i18n/messages.*.json src/main/webapp/themes/bootstrap/help/*.json; do python3 -m json.tool "$f" >/dev/null && echo "OK $f" || echo "FAIL $f"; done`
  Expected: every line `OK`.
- [ ] Commit any formatting-only diffs: `git commit -am "chore(theme): formatter/license after parity remediation"`

### Task G2 — Re-run the four-quadrant gap audit (closure proof)
Re-run the same audit that produced the spec §3 backlog, now asserting closure. Dispatch four read-only audit sub-agents (or re-read manually) over: (1) core search, (2) header/footer/help, (3) chat/advance, (4) error/cache. For each spec item ID (CHAT-1..6, SRCH-1..9, ADV-1..6, CACHE-1..3, ERR-1..2, HELP-1, HDR-1, GEO-1), record DONE (with the implementing commit) or N/A (with justification).
- [ ] **Confirm each P0 closed at the code level:**
  - CHAT-1: `grep -n "message: question" src/main/webapp/themes/bootstrap/assets/chat.js` returns the new body; `grep -c "body = { question" chat.js` returns 0.
  - CACHE-1: `grep -n "frame-src blob:" src/main/webapp/themes/bootstrap/index.html src/main/java/org/codelibs/fess/app/web/theme/ThemeViewAction.java` returns both.
  - ADV-1: `grep -c "msword" src/main/webapp/themes/bootstrap/assets/advance.js` returns 0.
  - ADV-2: `grep -n "getAll(\"ex_q\")" src/main/webapp/themes/bootstrap/assets/search.js` present.
- [ ] **Confirm i18n completeness:** `LabelMessageThemeParityTest#test_allLocalesExactlyMatchEnKeys` green (16 bundles, identical key sets).
- [ ] Write `docs/superpowers/reports/2026-05-29-static-theme-parity-closure.md`: a table of every spec item → status → commit hash → evidence. Flag any item that could not be closed with a reason.
- [ ] Commit: `docs(report): static-theme parity closure report`

### Task G3 — Manual smoke (running Fess) of the P0 paths
With a running Fess (`./bin/fess`, an index with at least one Word/PDF doc, a labeled corpus, RAG chat enabled):
- [ ] **Chat (CHAT-1):** ask a question → streamed answer renders (not "message is required"); a label filter resends with `fields.label`.
- [ ] **Cache (CACHE-1/2):** open a result's cache link → iframe renders (no CSP violation in console), banner shows URL + date.
- [ ] **Advanced (ADV-1/2):** `/advance` with filetype=Word + a Language + a Label + Time=past week → results non-empty, request carries `filetype:word`, `lang`, `fields.label`, `ex_q`.
- [ ] **Geo (GEO-1):** location dropdown apply → results filter, params persist across pagination.
- [ ] **Errors (ERR-1):** `/error/busy` returns HTTP 429 with "Service Busy" copy.
- [ ] Record results in the closure report.

### Phase G exit criteria
All P0–P2 + GEO-1 items are DONE or justified-N/A in the closure report; all extended tests green; 16 locales key-complete; P0 paths verified in a running instance. This report is the artifact proving "実装漏れ・考慮漏れなし".
