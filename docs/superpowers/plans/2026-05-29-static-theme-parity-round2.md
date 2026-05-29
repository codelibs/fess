# Static Theme JSP Parity Remediation (Round 2) — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Close the 9 verified JSP-vs-static-theme parity gaps in the bootstrap theme (no-result screen, home-view options + logo, input attributes, advanced-link query forwarding, facet not-found message, 3s submit-disable, chat i18n key alignment).

**Architecture:** The bootstrap theme is a build-step-free vanilla-JS SPA in `src/main/webapp/themes/bootstrap/`. There is **no JS test runner**; the theme is verified by Java tests that assert the source files *contain* specific markers (`BundledBootstrapThemeTest`) and that all 16 i18n bundles share an identical key set (`theme/LabelMessageThemeParityTest#test_allLocalesExactlyMatchEnKeys`). Our TDD loop is therefore: add a failing marker-assertion to `BundledBootstrapThemeTest` (or rely on the parity test for i18n), run it, implement the marker in `index.html`/`*.js`/JSON, re-run, commit.

**Tech Stack:** Vanilla ES modules, Bootstrap 5, Font Awesome, JSON i18n bundles; JUnit 5 + Maven for verification; `node --check` for JS syntax.

**Conventions (verified):**
- i18n interpolation: `t(key, [positional])` → `{0}` placeholders (`i18n.js:68-72`).
- 16 locale bundles: `de en es fr hi id it ja ko nl pl pt-BR ru tr zh-CN zh-TW`.
- `theme/LabelMessageThemeParityTest` requires **every** bundle to have the **exact** same key set as `messages.en.json`.
- Reusable option renderers in `search.js`: `renderSortOptions()` (679), `renderNumOptions()` (699), `renderLangOptions()` (722), `renderLabelOptions()` (771), invoked from `renderSearchOptions()` (~829).
- Run a single Java test: `mvn -o test -Dtest=BundledBootstrapThemeTest` (drop `-o` if offline cache lacks deps).
- Run JS syntax check: `node --check <file>`.

**Locale translation policy:** New keys must be added to **all 16** bundles (parity test). Provide proper translations for the 8 core locales (`ja de es fr ko pt-BR zh-CN`); use the English value for the 8 partial locales (`hi id it nl pl ru tr zh-TW`), matching the existing native-but-partial approach. English + Japanese values are given verbatim below; the executing agent translates the remaining 6 core locales.

---

## File Structure

| File | Responsibility | Tasks |
|------|----------------|-------|
| `src/main/webapp/themes/bootstrap/i18n/messages.*.json` (×16) | UI strings; add 4 keys, rename 5 chat keys | 1 |
| `src/main/webapp/themes/bootstrap/assets/chat.js` | Update chat phase-label key reference | 1 |
| `src/main/webapp/themes/bootstrap/index.html` | SPA shell markup (no-result, home, inputs, advance links) | 2,3,4,5 |
| `src/main/webapp/themes/bootstrap/assets/search.js` | No-result render, home options wiring, facet not-found, 3s disable | 2,3,5,6,7 |
| `src/main/webapp/themes/bootstrap/assets/app.js` | Home view activation (autofocus, advance-link href) | 3,5 |
| `src/main/webapp/themes/bootstrap/assets/advance.js` | Prefill from `?q=`, 3s disable | 5,7 |
| `src/test/java/org/codelibs/fess/theme/BundledBootstrapThemeTest.java` | Marker assertions (the test contract) | 2,3,4,5,6,7 |

---

## Task 1: i18n bundles — add 4 keys, rename 5 chat keys

**Files:**
- Modify: all 16 `src/main/webapp/themes/bootstrap/i18n/messages.<loc>.json`
- Modify: `src/main/webapp/themes/bootstrap/assets/chat.js:118`
- Test: `src/test/java/org/codelibs/fess/theme/LabelMessageThemeParityTest.java` (existing, no edit)

- [ ] **Step 1: Run the parity test to confirm a green baseline**

Run: `mvn -o test -Dtest=org.codelibs.fess.theme.LabelMessageThemeParityTest`
Expected: PASS (all 16 bundles currently match).

- [ ] **Step 2: Add the 4 new keys to `messages.en.json`**

Insert near related keys (e.g. after `"search.popular_searches"` for the search.* keys, after `"facet.empty"` for facet.not_found):

```json
"search.did_not_match": "Your search — {0} — did not match any documents.",
"search.did_not_match_suggestion": "Try different keywords or fewer keywords.",
"search.options": "Options",
"facet.not_found": "No matching filters."
```

- [ ] **Step 3: Rename the 5 chat keys in `messages.en.json`**

Rename `labels.chat_step_*` → `labels.chat_phase_*` (values unchanged):

```json
"labels.chat_phase_intent": "Intent",
"labels.chat_phase_search": "Search",
"labels.chat_phase_evaluate": "Evaluate",
"labels.chat_phase_fetch": "Fetch",
"labels.chat_phase_answer": "Answer"
```

- [ ] **Step 4: Run the parity test to verify it now FAILS**

Run: `mvn -o test -Dtest=org.codelibs.fess.theme.LabelMessageThemeParityTest`
Expected: FAIL — `test_allLocalesExactlyMatchEnKeys` reports the 4 new + 5 renamed keys missing from the other 15 bundles.

- [ ] **Step 5: Apply the same key additions + chat rename to the other 15 bundles**

For each of `ja de es fr hi id it ko nl pl pt-BR ru tr zh-CN zh-TW`:
- rename `labels.chat_step_*` → `labels.chat_phase_*` (keep existing translated values);
- add the 4 new keys.

Japanese (`messages.ja.json`) values:

```json
"search.did_not_match": "検索キーワード「{0}」に一致する文書が見つかりませんでした。",
"search.did_not_match_suggestion": "別のキーワードを試すか、キーワードを減らしてください。",
"search.options": "オプション",
"facet.not_found": "該当する絞り込みがありません。"
```

Core locales `de es fr ko pt-BR zh-CN`: translate the 4 values appropriately.
Partial locales `hi id it nl pl ru tr zh-TW`: use the English values verbatim.

- [ ] **Step 6: Update the chat.js reference to the renamed key**

In `src/main/webapp/themes/bootstrap/assets/chat.js:118`, change:

```js
badge.appendChild(document.createTextNode(" " + t("labels.chat_step_" + phase)));
```
to:
```js
badge.appendChild(document.createTextNode(" " + t("labels.chat_phase_" + phase)));
```

- [ ] **Step 7: Verify JSON validity, JS syntax, and parity**

Run: `node --check src/main/webapp/themes/bootstrap/assets/chat.js`
Run: `for f in src/main/webapp/themes/bootstrap/i18n/messages.*.json; do node -e "JSON.parse(require('fs').readFileSync('$f'))" || echo "BAD: $f"; done`
Run: `mvn -o test -Dtest=org.codelibs.fess.theme.LabelMessageThemeParityTest`
Expected: all PASS; no `chat_step` remaining (`grep -rn chat_step src/main/webapp/themes/bootstrap` returns nothing).

- [ ] **Step 8: Commit**

```bash
git add src/main/webapp/themes/bootstrap/i18n/messages.*.json src/main/webapp/themes/bootstrap/assets/chat.js
git commit -m "feat(theme): add no-result/options/facet i18n keys; align chat phase keys to JSP [parity-r2 #1/#7/#9]"
```

---

## Task 2: No-result screen + header input maxlength

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/index.html:24` (header input), `:143-146` (empty-state)
- Modify: `src/main/webapp/themes/bootstrap/assets/search.js` (empty-state render path, ~line 413)
- Test: `src/test/java/org/codelibs/fess/theme/BundledBootstrapThemeTest.java`

- [ ] **Step 1: Add failing marker assertions to `BundledBootstrapThemeTest`**

Append these test methods (place near the other `index.html`/`search.js` content tests):

```java
@Test
public void test_indexHtml_noResultScreenHasIconAndMessages() throws Exception {
    final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
    assertTrue(html.contains("id=\"empty-did-not-match\""), "no-result screen must have a did-not-match element");
    assertTrue(html.contains("fa fa-search fa-3x"), "no-result screen must show a search icon");
    assertTrue(html.contains("data-i18n=\"search.did_not_match_suggestion\""), "no-result screen must show the suggestion line");
}

@Test
public void test_searchJs_noResultRendersInterpolatedQuery() throws Exception {
    final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
    assertTrue(js.contains("search.did_not_match"), "search.js must render the did-not-match message with the query");
}

@Test
public void test_indexHtml_searchInputHasMaxLength() throws Exception {
    final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
    assertTrue(html.contains("maxlength=\"1000\""), "search inputs must cap length at 1000 (JSP parity)");
}
```

- [ ] **Step 2: Run the new tests to verify they FAIL**

Run: `mvn -o test -Dtest='BundledBootstrapThemeTest#test_indexHtml_noResultScreenHasIconAndMessages+test_searchJs_noResultRendersInterpolatedQuery+test_indexHtml_searchInputHasMaxLength'`
Expected: FAIL (markers absent).

- [ ] **Step 3: Add `maxlength="1000"` to the header search input**

In `index.html` the `#search-input` element (lines 24-36), add `maxlength="1000"` (e.g. right after `name="q"`):

```html
            name="q"
            maxlength="1000"
```

- [ ] **Step 4: Replace the empty-state markup**

Replace `index.html:143-146`:

```html
          <div class="text-center text-muted my-5 d-none" id="empty-state">
            <p data-i18n="search.no_results">No results.</p>
            <div id="popular-words"></div>
          </div>
```
with:
```html
          <div class="text-center text-muted my-5 d-none" id="empty-state">
            <p class="mb-3"><i class="fa fa-search fa-3x" aria-hidden="true"></i></p>
            <p class="mb-2" id="empty-did-not-match"></p>
            <p class="mb-3" data-i18n="search.did_not_match_suggestion">Try different keywords or fewer keywords.</p>
            <div id="popular-words"></div>
          </div>
```

- [ ] **Step 5: Render the interpolated did-not-match message in `search.js`**

In the empty-state branch (around `search.js:413`, where `const empty = document.getElementById("empty-state");` and the state is shown), set the message element using the current query via `textContent` (XSS-safe), e.g.:

```js
const empty = document.getElementById("empty-state");
const dnm = document.getElementById("empty-did-not-match");
if (dnm) {
  dnm.textContent = t("search.did_not_match", [state.q || ""]);
}
empty.classList.remove("d-none");
```
Follow the file's existing show/hide idiom; the marker the test checks is the literal `t("search.did_not_match"` call. Keep the existing popular-words population.

- [ ] **Step 6: Run the tests + node check**

Run: `node --check src/main/webapp/themes/bootstrap/assets/search.js`
Run: `mvn -o test -Dtest='BundledBootstrapThemeTest#test_indexHtml_noResultScreenHasIconAndMessages+test_searchJs_noResultRendersInterpolatedQuery+test_indexHtml_searchInputHasMaxLength'`
Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add src/main/webapp/themes/bootstrap/index.html src/main/webapp/themes/bootstrap/assets/search.js src/test/java/org/codelibs/fess/theme/BundledBootstrapThemeTest.java
git commit -m "feat(theme): no-result screen icon + query-interpolated did_not_match; header input maxlength [parity-r2 #1/#4]"
```

---

## Task 3: Home view — logo image + up-front options panel + input attrs

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/index.html:58-73` (home-view)
- Modify: `src/main/webapp/themes/bootstrap/assets/search.js` (populate home option controls; apply on home submit)
- Modify: `src/main/webapp/themes/bootstrap/assets/app.js` (autofocus home input on home-view activation)
- Test: `BundledBootstrapThemeTest`

- [ ] **Step 1: Add failing marker assertions**

```java
@Test
public void test_indexHtml_homeViewHasLogoImage() throws Exception {
    final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
    assertTrue(html.contains("home-logo"), "home view must render the product logo image, not a text heading");
    assertFalse(html.contains("<h2 class=\"display-4 mb-4\">Fess</h2>"), "home view text heading must be replaced by the logo");
}

@Test
public void test_indexHtml_homeViewHasOptionsPanel() throws Exception {
    final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
    assertTrue(html.contains("id=\"home-options\""), "home view must have a collapsible options panel");
    assertTrue(html.contains("id=\"home-options-toggle\""), "home view must have an Options toggle button");
    assertTrue(html.contains("id=\"home-sort-select\""), "home options must include a sort select");
    assertTrue(html.contains("id=\"home-num-select\""), "home options must include a num select");
    assertTrue(html.contains("id=\"home-lang-select\""), "home options must include a lang select");
}

@Test
public void test_searchJs_populatesHomeOptions() throws Exception {
    final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
    assertTrue(js.contains("home-sort-select"), "search.js must populate the home option controls");
}
```

- [ ] **Step 2: Run to verify FAIL**

Run: `mvn -o test -Dtest='BundledBootstrapThemeTest#test_indexHtml_homeViewHasLogoImage+test_indexHtml_homeViewHasOptionsPanel+test_searchJs_populatesHomeOptions'`
Expected: FAIL.

- [ ] **Step 3: Replace the home-view inner markup**

Replace `index.html:62-72` (the `<div class="text-center">…</div>` block contents) with:

```html
      <div class="text-center">
        <h2 class="mb-4"><img src="/images/logo.png" alt="" data-i18n-alt="labels.header_brand_name" class="home-logo"></h2>
        <form id="home-search-form" class="d-flex justify-content-center mb-3" role="search" aria-label="Home search">
          <input id="home-search-input" type="search" class="form-control form-control-lg home-search-input"
                 name="q" maxlength="1000" autocomplete="off"
                 data-i18n-placeholder="search.placeholder"
                 aria-label="Search query">
          <button type="submit" class="btn btn-primary btn-lg ms-2"
                  data-i18n="search.button">Search</button>
        </form>
        <div class="mb-3">
          <button class="btn btn-link btn-sm" type="button" id="home-options-toggle"
                  data-bs-toggle="collapse" data-bs-target="#home-options"
                  aria-expanded="false" aria-controls="home-options" data-i18n="search.options">Options</button>
          <a class="btn btn-link btn-sm" href="/advance" data-spa id="home-advance-link" data-i18n="advance.link">Advanced</a>
        </div>
        <div class="collapse mb-4" id="home-options">
          <div class="d-flex gap-2 flex-wrap justify-content-center align-items-center">
            <label class="form-label me-1 mb-0 small" for="home-sort-select" data-i18n="search.sort">Sort</label>
            <select id="home-sort-select" class="form-select form-select-sm options-select" aria-label="Sort order"></select>
            <label class="form-label me-1 mb-0 small" for="home-num-select" data-i18n="search.num">Per page</label>
            <select id="home-num-select" class="form-select form-select-sm options-select" aria-label="Results per page"></select>
            <label class="form-label me-1 mb-0 small" for="home-lang-select" data-i18n="search.lang">Language</label>
            <select id="home-lang-select" class="form-select form-select-sm options-select" aria-label="Language filter"></select>
          </div>
        </div>
        <div id="home-popular-words" class="text-muted"></div>
      </div>
```

- [ ] **Step 4: Populate the home option controls and apply them on submit (`search.js`)**

The results-view renderers (`renderSortOptions/NumOptions/LangOptions` at 679/699/722) target fixed IDs (`#sort-select`, etc.). Generalise them to accept an optional element (defaulting to the existing ID) **or** add a small helper that clones the populated `<option>`s from the results selects into the home selects after config load. Whichever fits the file's style, the requirement is:
- after `/api/v2/ui/config` loads (where `renderSearchOptions()` runs, ~829), also populate `#home-sort-select`, `#home-num-select`, `#home-lang-select` with the same options;
- on `#home-search-form` submit, read those three controls into the search params (`state.sort`, `state.num`, `state.lang`) before navigating to the search — mirroring how the header/home query is currently submitted (`attach()` ~908, `syncSearchInputs` exported at 1573).

The test marker is the literal string `home-sort-select` appearing in `search.js`.

- [ ] **Step 5: Autofocus the home input on home-view activation (`app.js`)**

In the route handler that reveals `#home-view`, focus the input:

```js
const homeInput = document.getElementById("home-search-input");
if (homeInput) { homeInput.focus(); }
```
Place it where the home view is un-hidden (search `app.js` for `home-view`).

- [ ] **Step 6: Verify**

Run: `node --check src/main/webapp/themes/bootstrap/assets/search.js`
Run: `node --check src/main/webapp/themes/bootstrap/assets/app.js`
Run: `mvn -o test -Dtest='BundledBootstrapThemeTest#test_indexHtml_homeViewHasLogoImage+test_indexHtml_homeViewHasOptionsPanel+test_searchJs_populatesHomeOptions'`
Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add src/main/webapp/themes/bootstrap/index.html src/main/webapp/themes/bootstrap/assets/search.js src/main/webapp/themes/bootstrap/assets/app.js src/test/java/org/codelibs/fess/theme/BundledBootstrapThemeTest.java
git commit -m "feat(theme): home view logo image + up-front options panel + autofocus [parity-r2 #2/#3/#5]"
```

---

## Task 4: Advanced link forwards current query + advance form prefill

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/assets/app.js` (set advance link hrefs with current `q`)
- Modify: `src/main/webapp/themes/bootstrap/assets/advance.js` (prefill "must contain words" from `?q=`)
- Test: `BundledBootstrapThemeTest`

- [ ] **Step 1: Add failing marker assertion**

```java
@Test
public void test_advanceJs_prefillsFromQueryParam() throws Exception {
    final String js = Files.readString(THEME_DIR.resolve("assets/advance.js"), StandardCharsets.UTF_8);
    assertTrue(js.contains("params.get(\"q\")") || js.contains("get(\"q\")"),
            "advance.js must prefill the must-contain-words field from the incoming q param");
}
```

- [ ] **Step 2: Run to verify FAIL**

Run: `mvn -o test -Dtest='BundledBootstrapThemeTest#test_advanceJs_prefillsFromQueryParam'`
Expected: FAIL.

- [ ] **Step 3: Prefill the advanced form from `?q=` in `advance.js`**

In the advanced view's render/attach (where the form fields are built, around the form-build section ~line 276+), read the URL query and set the "must contain words" (`fAll`) input's value:

```js
const incomingQ = new URLSearchParams(location.search).get("q");
if (incomingQ && fAll && fAll.input) { fAll.input.value = incomingQ; }
```
Use the actual field handle name used in the file (`fAll` per audit); set its `.value` before suggest attaches.

- [ ] **Step 4: Make advance links carry the current query (`app.js`)**

Where the nav/home "Advanced" links are present (`#home-advance-link` and the navbar `a[href="/advance"]`), update their `href` to include the current query when navigating, e.g. on config/route update:

```js
const q = (document.getElementById("search-input") || {}).value
       || (document.getElementById("home-search-input") || {}).value || "";
document.querySelectorAll('a[href^="/advance"]').forEach(a => {
  a.setAttribute("href", q ? "/advance?q=" + encodeURIComponent(q) : "/advance");
});
```
Integrate following the SPA's existing link/route handling (`data-spa`). The router must preserve the `?q=` when activating the advance view.

- [ ] **Step 5: Verify**

Run: `node --check src/main/webapp/themes/bootstrap/assets/advance.js`
Run: `node --check src/main/webapp/themes/bootstrap/assets/app.js`
Run: `mvn -o test -Dtest='BundledBootstrapThemeTest#test_advanceJs_prefillsFromQueryParam'`
Expected: PASS.

- [ ] **Step 6: Commit**

```bash
git add src/main/webapp/themes/bootstrap/assets/advance.js src/main/webapp/themes/bootstrap/assets/app.js src/test/java/org/codelibs/fess/theme/BundledBootstrapThemeTest.java
git commit -m "feat(theme): forward current query to advanced search + prefill [parity-r2 #6]"
```

---

## Task 5: Facet "not found" message for all-zero groups

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/assets/search.js` (`renderFacetQueryViews`, ~1117-1150)
- Test: `BundledBootstrapThemeTest`

- [ ] **Step 1: Add failing marker assertion**

```java
@Test
public void test_searchJs_facetQueryViewShowsNotFound() throws Exception {
    final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
    assertTrue(js.contains("facet.not_found"),
            "search.js must render a 'not found' message for a facet-query group with all-zero counts");
}
```

- [ ] **Step 2: Run to verify FAIL**

Run: `mvn -o test -Dtest='BundledBootstrapThemeTest#test_searchJs_facetQueryViewShowsNotFound'`
Expected: FAIL.

- [ ] **Step 3: Render the not-found message in `renderFacetQueryViews`**

Currently (~`search.js:1126`) groups whose queries all have count 0 are skipped. Change the logic so a configured facet-query group with no positive-count entries still renders its title and a muted `t("facet.not_found")` line instead of being omitted. Build the message node with `textContent` (no innerHTML). Match `searchResults.jsp`'s `facet_is_not_found` semantics (only for configured groups, not dynamic facet fields).

- [ ] **Step 4: Verify**

Run: `node --check src/main/webapp/themes/bootstrap/assets/search.js`
Run: `mvn -o test -Dtest='BundledBootstrapThemeTest#test_searchJs_facetQueryViewShowsNotFound'`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/webapp/themes/bootstrap/assets/search.js src/test/java/org/codelibs/fess/theme/BundledBootstrapThemeTest.java
git commit -m "feat(theme): show facet 'not found' message for all-zero facet groups [parity-r2 #7]"
```

---

## Task 6: Submit button disable for 3 seconds

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/assets/search.js` (header + home form submit)
- Modify: `src/main/webapp/themes/bootstrap/assets/advance.js` (advanced form submit)
- Test: `BundledBootstrapThemeTest`

- [ ] **Step 1: Add failing marker assertions**

```java
@Test
public void test_searchJs_disablesSubmitTemporarily() throws Exception {
    final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
    assertTrue(js.contains("3000"), "search.js must re-enable the submit button after a 3s disable (JSP parity)");
}

@Test
public void test_advanceJs_disablesSubmitTemporarily() throws Exception {
    final String js = Files.readString(THEME_DIR.resolve("assets/advance.js"), StandardCharsets.UTF_8);
    assertTrue(js.contains("3000"), "advance.js must re-enable the submit button after a 3s disable (JSP parity)");
}
```

- [ ] **Step 2: Run to verify FAIL**

Run: `mvn -o test -Dtest='BundledBootstrapThemeTest#test_searchJs_disablesSubmitTemporarily+test_advanceJs_disablesSubmitTemporarily'`
Expected: FAIL.

- [ ] **Step 3: Add a 3s submit-disable helper and wire it**

Add a small helper (in `search.js`, exported or duplicated minimally in `advance.js`) and call it in each form's submit handler, after the navigation/submit is triggered:

```js
function disableSubmitBriefly(btn) {
  if (!btn) return;
  btn.disabled = true;
  setTimeout(() => { btn.disabled = false; }, 3000);
}
```
Wire it for: the header form submit (`#search-submit`), the home form submit (`#home-search-form` button), and the advanced form submit button. Do not block the actual search/navigation — disable only the button. (Matches JSP `BUTTON_DISABLE_DURATION=3000`.)

- [ ] **Step 4: Verify**

Run: `node --check src/main/webapp/themes/bootstrap/assets/search.js`
Run: `node --check src/main/webapp/themes/bootstrap/assets/advance.js`
Run: `mvn -o test -Dtest='BundledBootstrapThemeTest#test_searchJs_disablesSubmitTemporarily+test_advanceJs_disablesSubmitTemporarily'`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/webapp/themes/bootstrap/assets/search.js src/main/webapp/themes/bootstrap/assets/advance.js src/test/java/org/codelibs/fess/theme/BundledBootstrapThemeTest.java
git commit -m "feat(theme): disable submit button for 3s after search (JSP parity) [parity-r2 #8]"
```

---

## Task 7: Full verification + independent re-audit

**Files:** none (verification only) — except fixes surfaced by the audit.

- [ ] **Step 1: Run the full theme + i18n test suite**

Run: `mvn -o test -Dtest='BundledBootstrapThemeTest,org.codelibs.fess.theme.LabelMessageThemeParityTest,ThemeViewActionTest,StaticThemeFilterTest'`
Expected: all PASS.

- [ ] **Step 2: JS syntax check across all changed assets**

Run: `for f in src/main/webapp/themes/bootstrap/assets/*.js; do node --check "$f" || echo "FAIL: $f"; done`
Expected: no FAIL lines.

- [ ] **Step 3: Confirm no stale references**

Run: `grep -rn "chat_step" src/main/webapp/themes/bootstrap` → expect no output.
Run: `grep -rn "display-4 mb-4\">Fess" src/main/webapp/themes/bootstrap/index.html` → expect no output.

- [ ] **Step 4: Independent re-audit (fresh sub-agent)**

Dispatch a fresh Explore sub-agent to re-verify each of the 9 items against its JSP source and the now-modified static code, confirming the acceptance criteria in the spec (`docs/superpowers/specs/2026-05-29-static-theme-parity-round2-design.md` §4). The audit must NOT trust this plan or any report — read the actual files. Report any item still PARTIAL/MISSING with file:line.

- [ ] **Step 5: Address any audit findings**

If the re-audit flags a gap, fix it (new task or inline), re-run the relevant tests, and re-audit that item. Repeat until all 9 items verify.

- [ ] **Step 6: Update the spec status and write a short closure note**

Set the spec status to "Implemented & re-verified" and add a one-paragraph closure note listing the commits. Commit:

```bash
git add docs/superpowers/specs/2026-05-29-static-theme-parity-round2-design.md
git commit -m "docs(theme): mark round-2 parity remediation implemented & re-verified [parity-r2]"
```

---

## Self-Review

**Spec coverage:** Items #1 (Task 1 i18n + Task 2 render), #2/#3 (Task 3), #4 (Task 2), #5 home-input (Task 3) + advance-q (Task 4), #6 (Task 4), #7 (Task 1 key + Task 5 render), #8 (Task 6), #9 (Task 1) — all covered. Verification + re-audit (spec §6) → Task 7.

**Placeholder scan:** i18n translation values for 6 core locales are delegated to the executing agent under an explicit policy + parity-test gate (a translation step, not a vague placeholder); en/ja given verbatim. JS edits in large files (`search.js`) give exact code to add + the exact test-marker contract + the anchor function/line, rather than full-file diffs of a 1573-line file — the executing subagent reads the surrounding code. No "TODO/handle edge cases" placeholders.

**Type/name consistency:** Element IDs (`empty-did-not-match`, `home-options`, `home-options-toggle`, `home-sort-select`, `home-num-select`, `home-lang-select`, `home-advance-link`), i18n keys (`search.did_not_match`, `search.did_not_match_suggestion`, `search.options`, `facet.not_found`, `labels.chat_phase_*`), and the `disableSubmitBriefly` helper name are used consistently across tasks and test assertions.
