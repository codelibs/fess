# Static Theme JSP Parity Remediation (Round 4) — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Close the round-4 JSP-vs-static-theme parity gaps found by a fresh independent audit of all 17 legacy JSP screens (rounds 1–3 closed the rest). Scope = the confirmed functional gaps the requester approved; intentional static-theme improvements are preserved.

**Architecture:** The bootstrap theme is a build-step-free vanilla-JS SPA in `src/main/webapp/themes/bootstrap/`. There is **no JS test runner**; verification is by Java tests that assert source files *contain* markers (`BundledBootstrapThemeTest`) and that all 16 i18n bundles share an identical key set (`theme.LabelMessageThemeParityTest#test_allLocalesExactlyMatchEnKeys` and the duplicate `i18n.LabelMessageThemeParityTest`). TDD loop: add a failing marker assertion to `BundledBootstrapThemeTest`, run it (fail), implement the marker in `index.html`/`*.js`/JSON, re-run (pass), commit.

**Tech Stack:** Vanilla ES modules, Bootstrap 5, Font Awesome, JSON i18n bundles; JUnit 5 + Maven for verification; `node --check` for JS syntax, `node -e "JSON.parse(...)"` for JSON validity.

---

## Scope (confirmed with requester)

**Philosophy:** FUNCTIONAL PARITY ONLY. Deliberate improvements are preserved (cache blob-sandbox/CSP, a11y/skip-nav, responsive facet reflow, markdown `<img>` stripping, link `rel` hardening, in-place AJAX re-search, active-filter chips, broader help localization, dismiss buttons). The round-3 uncommitted changes stay as the baseline (audited as-is).

**Verified NON-issues — explicitly OUT of scope (do not "fix"):**
- **occt (`allintitle:`/`allinurl:`) spacing.** `QueryContext` (`src/main/java/org/codelibs/fess/entity/QueryContext.java:83-88`) strips the prefix via `startsWith(prefix)` + `substring(prefix.length())`; the leading space the server leaves is ignored by the parser. The SPA's `"allintitle:" + q` satisfies `startsWith("allintitle:")` → functionally identical. NO change.
- **Results-page popular-words gate.** The `/popular-words` endpoint itself returns INVALID_REQUEST when the feature is disabled and the SPA fetch fails silently → equivalent to JSP not showing them. NO change.
- **Cache & all six error views** — confirmed at parity (banner-once, `hq` passthrough, `<base>` injection, charset/mimetype, status codes, localized title/body wording, `redirect.jsp` branches incl. badAuth→`message_key` bridge). NO change except #7/#8 polish below.
- **Tier-3 B** (remove the no-op "More.." link) and **Tier-3 G** (advanced `(`/`)` / inner-quote escaping edge cases): kept as-is (NOT in scope).

**IN scope (this plan) — 13 items:**

| # | Severity | Area | Gap |
|---|----------|------|-----|
| 1 | MAJOR | Home | Home search box lacks suggest/autocomplete (header box has it; home box doesn't) |
| 2 | MAJOR | Chat | Inline (results-sidebar) chat shows no per-phase narration text (standalone is fine) |
| 3 | MINOR | Results | Favorite POST omits `query_id` (JSP sends it → click/query correlation lost) |
| 4 | MINOR | Header | Chat nav label is "Chat"; JSP shows "AI Search" (`nav.chat_ai_mode` key already exists, unused) |
| 5 | MINOR | Home | Home options panel has no "Clear" button (JSP `#searchOptionsClearButton`) |
| 6 | MINOR | Chat | Phase narration echoes the whole question on every phase; legacy only substitutes a server-supplied keyword on the search phase |
| 7 | TRIVIAL | Error | `error.js` `codeFromPath` JSDoc says default 404 but returns 500 (stale comment) |
| 8 | TRIVIAL | Error | `error.js` `PATH_TO_CODE` has 503 entries `computeErrorStatus` never emits (mark reserved) |
| D | MINOR | Chat | Error shown in both bubble + banner; legacy shows banner only (remove the bubble) |
| E | MINOR | Chat | Markdown renders only h2–h4 (`#{2,4}`); legacy `marked` renders `#`→h1 |
| C | MINOR | Chat | Progress strip stays visible after completion; legacy hides it on done/error |
| F | MINOR | Header | No "back to Search" link on the chat page (JSP swaps the nav link; brand link still works) |
| A | MINOR | Home | `#home-flash` region + `renderHomeFlash()` is dead code; wire it to a query-param-driven message |

**Locale policy (for new i18n keys #F `nav.search`, #A `flash.*`):** add to **all 16** bundles atomically (`LabelMessageThemeParityTest` enforces identical key sets). Provide real translations for the 8 core locales (`en ja de es fr ko pt-BR zh-CN`); use the English value for the 8 partial locales (`hi id it nl pl ru tr zh-TW`).

**Execution model:** Shared files (`index.html`, `app.js`, `chat.js`, `BundledBootstrapThemeTest.java`, the 16 i18n bundles) are touched by multiple tasks → implement tasks **SEQUENTIALLY** in the order below, one focused subagent per task. After each task: `node --check` changed JS + JSON validity for changed bundles, then the targeted Java test. After all tasks: full theme suite + `mvn formatter:format && mvn license:format`.

**All proposed marker strings were grepped and confirmed ABSENT in the current tree.** `BundledBootstrapThemeTest` reads files via `Files.readString(THEME_DIR.resolve(<rel>), StandardCharsets.UTF_8)` where `THEME_DIR = Paths.get("src/main/webapp/themes/bootstrap")` (line 48). It is JUnit 5 (`org.junit.jupiter.api.Test`, static `Assertions.*`). The i18n loop idiom uses `Files.list(THEME_DIR.resolve("i18n"))` filtering `startsWith("messages.")` + `endsWith(".json")`.

---

## File Structure

| File | Responsibility | Tasks |
|------|----------------|-------|
| `index.html` | SPA shell markup (home suggest slot, home clear button, chat nav label) | 1, 4, 5 |
| `assets/app.js` | home view wiring (suggest, clear, flash), chat nav label + chat-route Search-link swap | 1, 4, 5, 12, 13 |
| `assets/chat.js` | inline progress element, phase-keyword scope, error→banner-only, hide strip on done | 6, 7, 8, 9 |
| `assets/markdown.js` | heading regex h1–h6 | 10 |
| `assets/search.js` | favorite POST `query_id` | 11 |
| `assets/error.js` | JSDoc default 500, 503-reserved comment | 2 |
| `i18n/messages.*.json` (×16) | new keys `nav.search` (#F), `flash.login_required` + `flash.session_expired` (#A) | 13, 3 |
| `test/.../theme/BundledBootstrapThemeTest.java` | marker assertions (test contract) | all |

---

## Standard per-task verification commands

- JS syntax (changed files): `node --check src/main/webapp/themes/bootstrap/assets/<file>.js`
- JSON validity (each changed bundle): `node -e "JSON.parse(require('fs').readFileSync('src/main/webapp/themes/bootstrap/i18n/messages.<loc>.json','utf8'))"`
- Targeted Java test (single method): `mvn -o test -Dtest='BundledBootstrapThemeTest#<methodName>'` (drop `-o` if the offline cache lacks deps).
- i18n parity (tasks that add keys): `mvn -o test -Dtest='org.codelibs.fess.theme.LabelMessageThemeParityTest,org.codelibs.fess.i18n.LabelMessageThemeParityTest'`

---

## Task 1 — #2 error.js polish: JSDoc default + 503 reserved comment

> Ordered first because `error.js` is touched by no other task — a clean warm-up that locks the test idiom.

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/assets/error.js`
- Test: `src/test/java/org/codelibs/fess/theme/BundledBootstrapThemeTest.java`

- [ ] **Step 1: Write the failing tests** — append to `BundledBootstrapThemeTest.java` (inside the class, before its closing brace):

```java
@Test
public void test_errorJs_codeFromPathDocSays500() throws Exception {
    final String js = Files.readString(THEME_DIR.resolve("assets/error.js"), StandardCharsets.UTF_8);
    assertTrue(js.contains("Returns \"500\" as the default when no segment matches"),
            "error.js codeFromPath JSDoc must state 500 default (#7)");
    assertFalse(js.contains("Returns \"404\" as the default"),
            "error.js codeFromPath JSDoc must not state 404 default (#7)");
}

@Test
public void test_errorJs_503ReservedComment() throws Exception {
    final String js = Files.readString(THEME_DIR.resolve("assets/error.js"), StandardCharsets.UTF_8);
    assertTrue(js.contains("503 path segments are reserved"),
            "error.js must mark the 503 path mappings as reserved (#8)");
}
```

- [ ] **Step 2: Run to verify they fail**

Run: `mvn -o test -Dtest='BundledBootstrapThemeTest#test_errorJs_codeFromPathDocSays500+test_errorJs_503ReservedComment'`
Expected: FAIL (assertions not yet satisfied).

- [ ] **Step 3: Implement** — in `assets/error.js`, fix the `codeFromPath` JSDoc (currently around lines 34-37, reading `Returns "404" as the default when no segment matches.`):

```js
 * Returns "500" as the default when no segment matches (mirrors
 * ThemeViewAction.computeErrorStatus, whose else branch is 500).
 *
 * @param {string} pathname - e.g. "/error/404", "/error/not_found", "/error/notFound"
 * @returns {string} - one of "400", "404", "429", "500", "503"
```

Then, in the `PATH_TO_CODE` object (lines ~11-27), add a reserved-comment above the `"503"` entry (keep all entries; do NOT remove i18n keys):

```js
  "system": "500",
  "error": "500",
  // #8: 503 path segments are reserved — computeErrorStatus never emits 503
  // (busy maps to 429), but a direct /error/503 URL still resolves to the
  // localized 503 page (error.title_503 / error.body_503 are kept in all 16 bundles).
  "503": "503",
  "service_unavailable": "503",
  "serviceunavailable": "503",
  "busy": "429"
```

- [ ] **Step 4: Verify** — `node --check src/main/webapp/themes/bootstrap/assets/error.js` (Expected: no output). Then re-run Step 2's command. Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/webapp/themes/bootstrap/assets/error.js src/test/java/org/codelibs/fess/theme/BundledBootstrapThemeTest.java
git commit -m "docs(theme): fix error.js codeFromPath default doc + mark 503 reserved [parity-r4 #7 #8]"
```

---

## Task 2 — #4 chat nav label "AI Search"

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/index.html`, `src/main/webapp/themes/bootstrap/assets/app.js`
- Test: `BundledBootstrapThemeTest.java`

- [ ] **Step 1: Failing test**

```java
@Test
public void test_chatNavUsesAiMode() throws Exception {
    final String js = Files.readString(THEME_DIR.resolve("assets/app.js"), StandardCharsets.UTF_8);
    final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
    assertTrue(js.contains("t(\"nav.chat_ai_mode\")"),
            "renderChatNavLink must use nav.chat_ai_mode (parity #4)");
    assertTrue(html.contains("id=\"chat-nav-link\" data-spa data-i18n=\"nav.chat_ai_mode\""),
            "chat nav markup must use nav.chat_ai_mode (parity #4)");
}
```

- [ ] **Step 2: Run, expect FAIL** — `mvn -o test -Dtest='BundledBootstrapThemeTest#test_chatNavUsesAiMode'`

- [ ] **Step 3: Implement**

In `app.js` `renderChatNavLink` (lines ~269-276) change the two `nav.chat` references:

```js
  chatLink.setAttribute("data-i18n", "nav.chat_ai_mode");
  chatLink.textContent = t("nav.chat_ai_mode");
```

In `index.html` (line ~44) change the static nav link:

```html
          <a class="nav-link px-2 d-none" href="/chat" id="chat-nav-link" data-spa data-i18n="nav.chat_ai_mode">AI Search</a>
```

(`nav.chat_ai_mode` = "AI Search" already exists in all 16 bundles — no i18n change.)

- [ ] **Step 4: Verify** — `node --check src/main/webapp/themes/bootstrap/assets/app.js`; re-run Step 2. Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/webapp/themes/bootstrap/index.html src/main/webapp/themes/bootstrap/assets/app.js src/test/java/org/codelibs/fess/theme/BundledBootstrapThemeTest.java
git commit -m "feat(theme): chat nav label uses AI Search (JSP parity) [parity-r4 #4]"
```

---

## Task 3 — #F chat-page header "Search" link (+ new i18n key `nav.search`)

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/assets/app.js`
- Modify (all 16): `src/main/webapp/themes/bootstrap/i18n/messages.*.json`
- Test: `BundledBootstrapThemeTest.java`

- [ ] **Step 1: Failing tests**

```java
@Test
public void test_appJs_chatHeaderSearchLink() throws Exception {
    final String js = Files.readString(THEME_DIR.resolve("assets/app.js"), StandardCharsets.UTF_8);
    assertTrue(js.contains("header-search-link"),
            "app.js must swap the chat nav into a Search link on the chat route (parity #F)");
    assertTrue(js.contains("setChatNavSearchMode(true)"),
            "app.js chat route must enable Search-link mode (parity #F)");
    assertTrue(js.contains("setChatNavSearchMode(false)"),
            "app.js non-chat routes must restore the chat label (parity #F)");
}

@Test
public void test_i18n_hasNavSearchKey() throws Exception {
    try (java.util.stream.Stream<java.nio.file.Path> files = Files.list(THEME_DIR.resolve("i18n"))) {
        files.filter(p -> p.getFileName().toString().startsWith("messages.")
                       && p.getFileName().toString().endsWith(".json"))
            .forEach(p -> {
                try {
                    assertTrue(Files.readString(p, StandardCharsets.UTF_8).contains("\"nav.search\""),
                            "bundle " + p.getFileName() + " must contain nav.search (parity #F)");
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            });
    }
}
```

- [ ] **Step 2: Run, expect FAIL** — `mvn -o test -Dtest='BundledBootstrapThemeTest#test_appJs_chatHeaderSearchLink+test_i18n_hasNavSearchKey'`

- [ ] **Step 3a: Add `nav.search` to all 16 bundles** — insert the key immediately after the existing `"nav.chat_ai_mode": ...,` line in each `messages.<loc>.json`. Values:

| locale | line to insert |
|--------|----------------|
| en | `  "nav.search": "Search",` |
| ja | `  "nav.search": "検索",` |
| de | `  "nav.search": "Suche",` |
| es | `  "nav.search": "Buscar",` |
| fr | `  "nav.search": "Rechercher",` |
| ko | `  "nav.search": "검색",` |
| pt-BR | `  "nav.search": "Pesquisar",` |
| zh-CN | `  "nav.search": "搜索",` |
| hi, id, it, nl, pl, ru, tr, zh-TW | `  "nav.search": "Search",` |

(Match each file's existing indentation/quoting exactly; the value is followed by a comma since `nav.*` keys are not the last entry.)

- [ ] **Step 3b: Add the swap helper + route wiring in `app.js`** — add this function next to `renderChatNavLink`:

```js
/**
 * #F (parity header.jsp:83-89): on the chat route, turn the chat nav link into a
 * "Search" link (href "/", fa-search). On any other route restore the chat label.
 * header-search-link
 */
function setChatNavSearchMode(onChat) {
  const link = document.getElementById("chat-nav-link");
  if (!link) return;
  while (link.firstChild) link.removeChild(link.firstChild);
  if (onChat) {
    link.href = "/";
    link.setAttribute("data-i18n", "nav.search");
    const icon = document.createElement("i");
    icon.className = "fa fa-search me-1";
    icon.setAttribute("aria-hidden", "true");
    link.appendChild(icon);
    link.appendChild(document.createTextNode(t("nav.search")));
  } else {
    link.href = "/chat";
    link.setAttribute("data-i18n", "nav.chat_ai_mode");
    link.textContent = t("nav.chat_ai_mode");
  }
}
```

In the chat route handler (lines ~405-413) add the enable call:

```js
    () => {
      setSearchFormVisible(false);
      showView("chat-view");
      setChatNavSearchMode(true);   // #F
      chat.attachStandalone();
    }
```

**Restore on EVERY non-chat route (required — `setChatNavSearchMode(true)` mutates the persistent `#chat-nav-link`; missing a route leaves a stale "Search" label after leaving chat).** Add `setChatNavSearchMode(false);` to the first line of each of these `router.register(...)` callbacks in `app.js` (verified matchers):

1. Home — `(path === "/" || path === "/index" || path === "/index.html") && !hasSearchQuery()` (~353-360)
2. Search/results — `path === "/" || path === "/search" || path === "/index" || path === "/index.html"` (~363-373)
3. Profile — `path === "/profile"` (~376-383)
4. Advanced — `path === "/advance"` (~386-393)
5. Help — `path === "/help"` (~396-403)
6. Cache — `path === "/cache" || path.startsWith("/cache/")` (~418-425)
7. Error — `path === "/error" || path.startsWith("/error/")` (~428-435)
8. Fallback — `() => true` (~438-445)

(Equivalent robust alternative: if `router` exposes a pre-dispatch/before hook in `assets/router.js`, register a single `setChatNavSearchMode(false)` there and only let the chat route flip it to `true` — verify the hook exists before choosing this. Either way the marker test asserts a `setChatNavSearchMode(false)` call is present.)

- [ ] **Step 4: Verify**

```bash
node --check src/main/webapp/themes/bootstrap/assets/app.js
for loc in de en es fr hi id it ja ko nl pl pt-BR ru tr zh-CN zh-TW; do node -e "JSON.parse(require('fs').readFileSync('src/main/webapp/themes/bootstrap/i18n/messages.$loc.json','utf8'))"; done
```

Then: `mvn -o test -Dtest='BundledBootstrapThemeTest#test_appJs_chatHeaderSearchLink+test_i18n_hasNavSearchKey,org.codelibs.fess.theme.LabelMessageThemeParityTest,org.codelibs.fess.i18n.LabelMessageThemeParityTest'`
Expected: PASS (parity tests confirm all 16 bundles still share the exact key set).

- [ ] **Step 5: Commit**

```bash
git add src/main/webapp/themes/bootstrap/assets/app.js src/main/webapp/themes/bootstrap/i18n/messages.*.json src/test/java/org/codelibs/fess/theme/BundledBootstrapThemeTest.java
git commit -m "feat(theme): show Search link in header on chat page (JSP parity) [parity-r4 #F]"
```

---

## Task 4 — #1 home search box suggest/autocomplete

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/index.html`, `src/main/webapp/themes/bootstrap/assets/app.js`
- Test: `BundledBootstrapThemeTest.java`

- [ ] **Step 1: Failing test**

```java
@Test
public void test_homeSuggestWired() throws Exception {
    final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
    final String js = Files.readString(THEME_DIR.resolve("assets/app.js"), StandardCharsets.UTF_8);
    assertTrue(html.contains("id=\"home-suggest-dropdown\""),
            "index.html must contain the home suggest dropdown (parity #1)");
    assertTrue(js.contains("search.attachSuggest(input, homeSuggest"),
            "app.js must attach suggest to the home input (parity #1)");
}
```

- [ ] **Step 2: Run, expect FAIL** — `mvn -o test -Dtest='BundledBootstrapThemeTest#test_homeSuggestWired'`

- [ ] **Step 3a: index.html** — replace the home search form block (lines ~68-75) with (adds `position-relative`, combobox ARIA, and the dropdown `<ul>`):

```html
        <form id="home-search-form" class="d-flex justify-content-center mb-3 position-relative" role="search" aria-label="Home search">
          <input id="home-search-input" type="search" class="form-control form-control-lg home-search-input"
                 name="q" maxlength="1000" autocomplete="off"
                 data-i18n-placeholder="search.placeholder"
                 role="combobox" aria-expanded="false" aria-controls="home-suggest-dropdown"
                 aria-autocomplete="list" aria-haspopup="listbox"
                 aria-label="Search query">
          <button type="submit" class="btn btn-primary btn-lg ms-2"
                  data-i18n="search.button">Search</button>
          <ul class="list-group suggest-dropdown position-absolute top-100 start-0 end-0 mt-1 d-none shadow-sm" id="home-suggest-dropdown" role="listbox" aria-label="Search suggestions"></ul>
        </form>
```

- [ ] **Step 3b: app.js** — in `attachHomeView()`, just before the `renderHomePopularWords();` call (line ~219), add:

```js
  // #1 (parity index.jsp:135-137): attach the shared suggest dropdown to the home
  // search box, forwarding the selected home languages as the suggest lang filter.
  const homeSuggest = document.getElementById("home-suggest-dropdown");
  if (input && homeSuggest && !input.dataset.suggestAttached) {
    input.dataset.suggestAttached = "1";
    search.attachSuggest(input, homeSuggest, {
      get lang() {
        const sel = document.getElementById("home-lang-select");
        return sel ? Array.from(sel.selectedOptions).map(o => o.value).filter(v => v !== "") : [];
      }
    });
  }
```

(`app.js` already `import * as search from "./search.js"`, and `search.attachSuggest` is exported at `search.js:638`. `input` is the home input resolved earlier in `attachHomeView`. The `{ get lang() }` getter mirrors `advance.js:561-566`.)

- [ ] **Step 4: Verify** — `node --check src/main/webapp/themes/bootstrap/assets/app.js`; re-run Step 2. Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/webapp/themes/bootstrap/index.html src/main/webapp/themes/bootstrap/assets/app.js src/test/java/org/codelibs/fess/theme/BundledBootstrapThemeTest.java
git commit -m "feat(theme): wire suggest/autocomplete on home search box (JSP parity) [parity-r4 #1]"
```

---

## Task 5 — #5 home options "Clear" button

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/index.html`, `src/main/webapp/themes/bootstrap/assets/app.js`
- Test: `BundledBootstrapThemeTest.java`

- [ ] **Step 1: Failing test**

```java
@Test
public void test_homeOptionsClearButton() throws Exception {
    final String html = Files.readString(THEME_DIR.resolve("index.html"), StandardCharsets.UTF_8);
    final String js = Files.readString(THEME_DIR.resolve("assets/app.js"), StandardCharsets.UTF_8);
    assertTrue(html.contains("id=\"home-options-clear-btn\""),
            "home options must have a clear button (parity #5)");
    assertTrue(js.contains("home-options-clear-btn"),
            "app.js must wire the home options clear button (parity #5)");
}
```

- [ ] **Step 2: Run, expect FAIL** — `mvn -o test -Dtest='BundledBootstrapThemeTest#test_homeOptionsClearButton'`

- [ ] **Step 3a: index.html** — inside the `#home-options` panel (lines ~82-96), after the label-select block and before the panel's closing `</div>`, add the clear button (reuses the existing `facet.clear` key = "Clear"):

```html
            <button type="button" id="home-options-clear-btn" class="btn btn-outline-secondary btn-sm ms-1" data-i18n="facet.clear">Clear</button>
```

- [ ] **Step 3b: app.js** — in `attachHomeView()`, near the suggest wiring added in Task 4, add the clear handler (resets sort/num to index 0; multi-select lang/label fully deselected — mirrors `js/index.js:62-68`):

```js
  // #5 (parity js/index.js:62-68, index.jsp:101-103): reset the home option selects.
  const homeClearBtn = document.getElementById("home-options-clear-btn");
  if (homeClearBtn && !homeClearBtn.dataset.attached) {
    homeClearBtn.dataset.attached = "1";
    homeClearBtn.addEventListener("click", ev => {
      ev.preventDefault();
      const sort = document.getElementById("home-sort-select");
      const num = document.getElementById("home-num-select");
      const lang = document.getElementById("home-lang-select");
      const label = document.getElementById("home-label-select");
      if (sort) sort.selectedIndex = 0;
      if (num) num.selectedIndex = 0;
      if (lang) Array.from(lang.options).forEach(o => { o.selected = false; });
      if (label) Array.from(label.options).forEach(o => { o.selected = false; });
    });
  }
```

- [ ] **Step 4: Verify** — `node --check src/main/webapp/themes/bootstrap/assets/app.js`; re-run Step 2. Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/webapp/themes/bootstrap/index.html src/main/webapp/themes/bootstrap/assets/app.js src/test/java/org/codelibs/fess/theme/BundledBootstrapThemeTest.java
git commit -m "feat(theme): add Clear button to home options panel (JSP parity) [parity-r4 #5]"
```

---

## Task 6 — #A wire home-flash (+ new i18n keys `flash.*`)

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/assets/app.js`
- Modify (all 16): `src/main/webapp/themes/bootstrap/i18n/messages.*.json`
- Test: `BundledBootstrapThemeTest.java`

- [ ] **Step 1: Failing tests**

```java
@Test
public void test_appJs_wiresHomeFlash() throws Exception {
    final String js = Files.readString(THEME_DIR.resolve("assets/app.js"), StandardCharsets.UTF_8);
    assertTrue(js.contains("home-flash-query-message"),
            "app.js attachHomeView must surface a query-param flash message (parity #A)");
    assertTrue(js.contains("renderHomeFlash(t(\"flash."),
            "app.js must call renderHomeFlash with a flash.* i18n key (parity #A)");
}

@Test
public void test_i18n_hasFlashKeys() throws Exception {
    try (java.util.stream.Stream<java.nio.file.Path> files = Files.list(THEME_DIR.resolve("i18n"))) {
        files.filter(p -> p.getFileName().toString().startsWith("messages.")
                       && p.getFileName().toString().endsWith(".json"))
            .forEach(p -> {
                try {
                    final String s = Files.readString(p, StandardCharsets.UTF_8);
                    assertTrue(s.contains("\"flash.login_required\""),
                            "bundle " + p.getFileName() + " must contain flash.login_required (parity #A)");
                    assertTrue(s.contains("\"flash.session_expired\""),
                            "bundle " + p.getFileName() + " must contain flash.session_expired (parity #A)");
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            });
    }
}
```

- [ ] **Step 2: Run, expect FAIL** — `mvn -o test -Dtest='BundledBootstrapThemeTest#test_appJs_wiresHomeFlash+test_i18n_hasFlashKeys'`

- [ ] **Step 3a: Add `flash.login_required` and `flash.session_expired` to all 16 bundles.** Pick a stable, alphabetically/logically sensible spot (e.g. just before the existing `"footer.*"` keys, or wherever the file groups top-level prefixes — keep each file's ordering convention). Both keys must appear in every bundle. Values:

| locale | flash.login_required | flash.session_expired |
|--------|----------------------|------------------------|
| en | `Please sign in to continue.` | `Your session has expired. Please search again.` |
| ja | `続行するにはサインインしてください。` | `セッションの有効期限が切れました。もう一度検索してください。` |
| de | `Bitte melden Sie sich an, um fortzufahren.` | `Ihre Sitzung ist abgelaufen. Bitte suchen Sie erneut.` |
| es | `Inicie sesión para continuar.` | `Su sesión ha expirado. Vuelva a buscar.` |
| fr | `Veuillez vous connecter pour continuer.` | `Votre session a expiré. Veuillez relancer la recherche.` |
| ko | `계속하려면 로그인하세요.` | `세션이 만료되었습니다. 다시 검색하세요.` |
| pt-BR | `Faça login para continuar.` | `Sua sessão expirou. Pesquise novamente.` |
| zh-CN | `请登录以继续。` | `您的会话已过期。请重新搜索。` |
| hi, id, it, nl, pl, ru, tr, zh-TW | (English value) | (English value) |

Each entry like `  "flash.login_required": "<value>",` — preserve the file's indentation and trailing-comma convention.

- [ ] **Step 3b: app.js** — in `attachHomeView()`, before `renderHomePopularWords();`, add the wiring (uses the existing exported `renderHomeFlash`, app.js:155-166; `t()` returns the key itself if missing, so unknown params degrade gracefully):

```js
  // #A (parity index.jsp:123-130): surface a query-param-driven flash message on the
  // home view (e.g. an auth redirect that appends ?error=login_required). The message
  // text is looked up via an allowlisted flash.* i18n key (never raw query text).
  // home-flash-query-message
  const flashParams = new URLSearchParams(location.search);
  const errKey = flashParams.get("error");
  const msgKey = flashParams.get("msg");
  if (errKey) {
    renderHomeFlash(t("flash." + errKey), "danger");
  } else if (msgKey) {
    renderHomeFlash(t("flash." + msgKey), "info");
  } else {
    renderHomeFlash(null);
  }
```

- [ ] **Step 4: Verify**

```bash
node --check src/main/webapp/themes/bootstrap/assets/app.js
for loc in de en es fr hi id it ja ko nl pl pt-BR ru tr zh-CN zh-TW; do node -e "JSON.parse(require('fs').readFileSync('src/main/webapp/themes/bootstrap/i18n/messages.$loc.json','utf8'))"; done
```

Then: `mvn -o test -Dtest='BundledBootstrapThemeTest#test_appJs_wiresHomeFlash+test_i18n_hasFlashKeys,org.codelibs.fess.theme.LabelMessageThemeParityTest,org.codelibs.fess.i18n.LabelMessageThemeParityTest'`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/webapp/themes/bootstrap/assets/app.js src/main/webapp/themes/bootstrap/i18n/messages.*.json src/test/java/org/codelibs/fess/theme/BundledBootstrapThemeTest.java
git commit -m "feat(theme): surface query-param flash messages on home view (JSP parity) [parity-r4 #A]"
```

---

## Task 7 — #3 favorite POST includes query_id

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/assets/search.js`
- Test: `BundledBootstrapThemeTest.java`

- [ ] **Step 1: Failing test**

```java
@Test
public void test_searchJs_favoritePostsQueryId() throws Exception {
    final String js = Files.readString(THEME_DIR.resolve("assets/search.js"), StandardCharsets.UTF_8);
    assertTrue(js.contains("toggleFavorite(docId, btn, li.dataset.queryId"),
            "favorite click must forward the card queryId (parity #3)");
    assertTrue(js.contains("{ query_id: queryId || \"\" }"),
            "favorite POST body must include query_id (parity #3)");
}
```

- [ ] **Step 2: Run, expect FAIL** — `mvn -o test -Dtest='BundledBootstrapThemeTest#test_searchJs_favoritePostsQueryId'`

- [ ] **Step 3: Implement** — two edits in `search.js`.

(i) The favorite-button click wiring (line ~454) currently:
```js
    btn.addEventListener("click", () => toggleFavorite(docId, btn));
```
becomes:
```js
    btn.addEventListener("click", () => toggleFavorite(docId, btn, li.dataset.queryId || ""));
```
(The card `<li>` already carries `dataset.queryId` from `search.js:132`, set to `env.query_id`.)

(ii) The `toggleFavorite` handler (lines ~1746-1748) currently:
```js
async function toggleFavorite(docId, btn) {
  try {
    const env = await api.post("/documents/" + encodeURIComponent(docId) + "/favorite", {});
```
becomes:
```js
async function toggleFavorite(docId, btn, queryId) {
  try {
    // #3 (parity js/search.js:137): include query_id so the click is attributed to its query.
    const env = await api.post("/documents/" + encodeURIComponent(docId) + "/favorite", { query_id: queryId || "" });
```

- [ ] **Step 4: Verify** — `node --check src/main/webapp/themes/bootstrap/assets/search.js`; re-run Step 2. Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/webapp/themes/bootstrap/assets/search.js src/test/java/org/codelibs/fess/theme/BundledBootstrapThemeTest.java
git commit -m "fix(theme): include query_id in favorite POST (JSP parity) [parity-r4 #3]"
```

---

## Task 8 — #2 inline chat per-phase narration

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/assets/chat.js`
- Test: `BundledBootstrapThemeTest.java`

- [ ] **Step 1: Failing test**

```java
@Test
public void test_chatJs_inlineProgressMessage() throws Exception {
    final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
    assertTrue(js.contains("chat-inline-progress-message"),
            "chat.js inline panel must build a progress message element (parity #2)");
    assertTrue(js.contains("progressMessageEl: inlineProgressMessageEl"),
            "chat.js inline refs must pass progressMessageEl (parity #2)");
}
```

- [ ] **Step 2: Run, expect FAIL** — `mvn -o test -Dtest='BundledBootstrapThemeTest#test_chatJs_inlineProgressMessage'`

- [ ] **Step 3: Implement** — two edits in `chat.js` inline `attach()` builder.

(i) Before `card.appendChild(body);` (line ~1007), create the inline progress element:
```js
  // #2 (parity): inline progress message element so the sidebar narrates phases
  // like the standalone panel (mirror of standalone chat.js progressMessageEl).
  const inlineProgressMessageEl = el("div", {
    className: "chat-progress-message text-muted small mt-1 d-none",
    attrs: { id: "chat-inline-progress-message", "aria-live": "polite" }
  });
  body.appendChild(inlineProgressMessageEl);
```

(ii) In the inline `refs` object (lines ~1010-1019), add the `progressMessageEl` property so `submitQuestion` (which already reads `progressMessageEl` from the refs) narrates phases:
```js
  const refs = {
    log,
    phaseStrip: { advanceTo: phaseAdvanceTo, complete: phaseComplete, reset: phaseReset },
    statusLozenge: { setStatus },
    errorBanner: { show: showError, hide: hideError },
    inputEl: input,
    submitEl: submit,
    emptyState: null,
    getFilters: () => ({ fields: [], extraQ: [] }),
    progressMessageEl: inlineProgressMessageEl
  };
```

(iii) Reveal the element while narrating. The existing narration block in `submitQuestion` (chat.js:~786-791) is currently a **two-line** form using an intermediate `const keywords`:
```js
          // #7 phase narration: render per-phase status message into progress element.
          // The search phase uses {0} for keywords; substitute the question text.
          if (progressMessageEl) {
            const keywords = (data && data.keywords) ? data.keywords : question;
            progressMessageEl.textContent = t("labels.chat_phase_" + phase, [keywords]);
          }
```
Edit THIS existing block (do not search for a one-line ternary) to add the `classList.remove("d-none")` line so the inline element becomes visible (harmless for the standalone element, which is already shown):
```js
          if (progressMessageEl) {
            progressMessageEl.classList.remove("d-none");
            const keywords = (data && data.keywords) ? data.keywords : question;
            progressMessageEl.textContent = t("labels.chat_phase_" + phase, [keywords]);
          }
```
(Task 9 then rewrites the `const keywords` + `textContent` lines into the scoped form; keep the `classList.remove("d-none")` line.)

- [ ] **Step 4: Verify** — `node --check src/main/webapp/themes/bootstrap/assets/chat.js`; re-run Step 2. Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/webapp/themes/bootstrap/assets/chat.js src/test/java/org/codelibs/fess/theme/BundledBootstrapThemeTest.java
git commit -m "feat(theme): narrate phases in inline chat panel (JSP parity) [parity-r4 #2]"
```

---

## Task 9 — #6 chat phase keyword substitution scope

> Depends on Task 8 (same narration block). Run after Task 8.

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/assets/chat.js`
- Test: `BundledBootstrapThemeTest.java`

- [ ] **Step 1: Failing test**

```java
@Test
public void test_chatJs_phaseKeywordScope() throws Exception {
    final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
    assertTrue(js.contains("? t(\"labels.chat_phase_\" + phase, [data.keywords])"),
            "phase narration must only substitute keywords when present (parity #6)");
    assertFalse(js.contains("? data.keywords : question"),
            "phase narration must not fall back to the full question (parity #6)");
}
```

- [ ] **Step 2: Run, expect FAIL** — `mvn -o test -Dtest='BundledBootstrapThemeTest#test_chatJs_phaseKeywordScope'`

- [ ] **Step 3: Implement** — replace the narration `textContent` line (from Task 8) in `submitQuestion` (lines ~786-791) with the scoped version:

```js
          // #6 (parity js/chat.js:496-500): only substitute {0} when the server sent
          // data.keywords (search phase). Other phases render their label as-is.
          if (progressMessageEl) {
            progressMessageEl.classList.remove("d-none");
            progressMessageEl.textContent = (data && data.keywords)
              ? t("labels.chat_phase_" + phase, [data.keywords])
              : t("labels.chat_phase_" + phase);
          }
```

- [ ] **Step 4: Verify** — `node --check src/main/webapp/themes/bootstrap/assets/chat.js`; re-run Step 2. Expected: PASS. (Re-run Task 8's test too: `mvn -o test -Dtest='BundledBootstrapThemeTest#test_chatJs_inlineProgressMessage+test_chatJs_phaseKeywordScope'`.)

- [ ] **Step 5: Commit**

```bash
git add src/main/webapp/themes/bootstrap/assets/chat.js src/test/java/org/codelibs/fess/theme/BundledBootstrapThemeTest.java
git commit -m "fix(theme): scope chat phase keyword substitution to search phase (legacy parity) [parity-r4 #6]"
```

---

## Task 10 — #D chat error: banner only (remove bubble)

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/assets/chat.js`
- Test: `BundledBootstrapThemeTest.java`

- [ ] **Step 1: Failing test**

```java
@Test
public void test_chatJs_errorClearsBubble() throws Exception {
    final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
    assertTrue(js.contains("activeBubble.wrap.remove()"),
            "chat.js must remove the assistant bubble on error, banner only (parity #D)");
    assertFalse(js.contains("activeBubble.bubble.textContent = msg"),
            "chat.js must not leave the error text in the assistant bubble (parity #D)");
}
```

- [ ] **Step 2: Run, expect FAIL** — `mvn -o test -Dtest='BundledBootstrapThemeTest#test_chatJs_errorClearsBubble'`

- [ ] **Step 3: Implement** — `appendAssistantBubble` returns `{ bubble, wrap, … }` (chat.js:623); `activeBubble.wrap` is the whole bubble row. Replace the three `activeBubble.bubble.textContent = …;` lines with bubble removal, keeping the banner.

(i) Auth branch (the `code === "auth_error"` block, ~line 841) — replace `activeBubble.bubble.textContent = authMsg;` with:
```js
        errorBanner.show(authMsg);
        // #D (parity js/chat.js:646-651): remove the assistant bubble, show banner only.
        if (activeBubble && activeBubble.wrap) activeBubble.wrap.remove();
        return;
```

(ii) Generic error branch (~lines 846-847) — replace `activeBubble.bubble.textContent = msg;` with:
```js
      errorBanner.show(msg);
      // #D (parity js/chat.js:646-651): remove the assistant bubble, show banner only.
      if (activeBubble && activeBubble.wrap) activeBubble.wrap.remove();
      return;
```

(iii) `onError` (~lines 914-915) — replace `activeBubble.bubble.textContent = msg;` with:
```js
    errorBanner.show(msg);
    // #D (parity js/chat.js:646-651): remove the assistant bubble, show banner only.
    if (activeBubble && activeBubble.wrap) activeBubble.wrap.remove();
```

(The test asserts the generic `activeBubble.bubble.textContent = msg` string is gone; also remove the auth-branch `activeBubble.bubble.textContent = authMsg;` line as shown so no bubble text remains.)

- [ ] **Step 4: Verify** — `node --check src/main/webapp/themes/bootstrap/assets/chat.js`; re-run Step 2. Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/webapp/themes/bootstrap/assets/chat.js src/test/java/org/codelibs/fess/theme/BundledBootstrapThemeTest.java
git commit -m "fix(theme): show chat errors in banner only, remove assistant bubble (legacy parity) [parity-r4 #D]"
```

---

## Task 11 — #C hide chat progress strip on completion

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/assets/chat.js`
- Test: `BundledBootstrapThemeTest.java`

- [ ] **Step 1: Failing test**

```java
@Test
public void test_chatJs_hidesProgressOnDone() throws Exception {
    final String js = Files.readString(THEME_DIR.resolve("assets/chat.js"), StandardCharsets.UTF_8);
    assertTrue(js.contains("chat-progress-hide-done"),
            "chat.js must hide the progress strip on ready/error (parity #C)");
    assertFalse(js.contains("Keep strip visible for user inspection after completion."),
            "chat.js must no longer keep the strip visible after completion (parity #C)");
}
```

- [ ] **Step 2: Run, expect FAIL** — `mvn -o test -Dtest='BundledBootstrapThemeTest#test_chatJs_hidesProgressOnDone'`

- [ ] **Step 3: Implement** — replace the wrapped-setStatus block (standalone builder, lines ~1347-1359) with the hiding version:

```js
  // #C (parity js/chat.js:544,657): hide the progress strip once the stream
  // completes (ready) or errors, mirroring hideProgressIndicator().
  const origSetStatus = setStatus;
  refs.statusLozenge = {
    setStatus: (key) => {
      origSetStatus(key);
      if (key === "ready" || key === "error") {
        // chat-progress-hide-done
        progressWrap.classList.add("d-none");
        progressMessageEl.textContent = "";
      }
    }
  };
```

(`progressWrap` and `progressMessageEl` are the standalone closures defined earlier in the same builder; the new-chat reset at chat.js:1334 already hides `progressWrap`, so this is consistent.)

- [ ] **Step 4: Verify** — `node --check src/main/webapp/themes/bootstrap/assets/chat.js`; re-run Step 2. Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/webapp/themes/bootstrap/assets/chat.js src/test/java/org/codelibs/fess/theme/BundledBootstrapThemeTest.java
git commit -m "fix(theme): hide chat progress strip on completion (legacy parity) [parity-r4 #C]"
```

---

## Task 12 — #E markdown h1–h6 headings

**Files:**
- Modify: `src/main/webapp/themes/bootstrap/assets/markdown.js`
- Test: `BundledBootstrapThemeTest.java`

- [ ] **Step 1: Failing test**

```java
@Test
public void test_markdownJs_h1ThroughH6() throws Exception {
    final String md = Files.readString(THEME_DIR.resolve("assets/markdown.js"), StandardCharsets.UTF_8);
    assertTrue(md.contains("/^(#{1,6}) (.+)$/"),
            "markdown.js HEADING_RE must accept # (h1) through ###### (h6) (parity #E)");
    assertTrue(md.contains("Math.min(hm[1].length, 6)"),
            "markdown.js must clamp heading level to 6 (parity #E)");
    assertFalse(md.contains("/^(#{2,4}) (.+)$/"),
            "markdown.js must no longer restrict headings to h2-h4 (parity #E)");
}
```

- [ ] **Step 2: Run, expect FAIL** — `mvn -o test -Dtest='BundledBootstrapThemeTest#test_markdownJs_h1ThroughH6'`

- [ ] **Step 3: Implement** — three edits in `markdown.js`.

(i) `HEADING_RE` (line ~81):
```js
const HEADING_RE = /^(#{1,6}) (.+)$/;
```
(ii) Both heading-level clamps (lines ~252 and ~305) change `Math.min(hm[1].length, 4)` →
```js
const level = Math.min(hm[1].length, 6);
```
(iii) JSDoc (line ~15):
```js
 *   - ATX headings: # H1 / ## H2 / ### H3 / #### H4 / ##### H5 / ###### H6
```

- [ ] **Step 4: Verify** — `node --check src/main/webapp/themes/bootstrap/assets/markdown.js`; re-run Step 2. Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/webapp/themes/bootstrap/assets/markdown.js src/test/java/org/codelibs/fess/theme/BundledBootstrapThemeTest.java
git commit -m "feat(theme): render markdown h1-h6 headings in chat (legacy parity) [parity-r4 #E]"
```

---

## Final verification (after all tasks)

- [ ] **All JS syntax:** `for f in src/main/webapp/themes/bootstrap/assets/*.js; do node --check "$f" || echo "FAIL $f"; done`
- [ ] **All JSON valid:** `for f in src/main/webapp/themes/bootstrap/i18n/messages.*.json; do node -e "JSON.parse(require('fs').readFileSync('$f','utf8'))" || echo "FAIL $f"; done`
- [ ] **Full theme suite:** `mvn -o test -Dtest='BundledBootstrapThemeTest,org.codelibs.fess.theme.LabelMessageThemeParityTest,org.codelibs.fess.i18n.LabelMessageThemeParityTest,ThemeViewActionTest,StaticThemeFilterTest,UiConfigHandlerTest'`  → expect BUILD SUCCESS, all green.
- [ ] **Format:** `mvn formatter:format && mvn license:format`  → expect 0 reformatted (commit any whitespace if produced).
- [ ] **Behavioral review gate (marker tests are `contains()` only — cannot catch runtime control-flow bugs).** Dispatch a fresh subagent to read the full diff and verify, for each task: the new code paths are actually reachable, no `null`/`undefined` deref (e.g. `homeSuggest`/`homeClearBtn` present in the home view; `activeBubble` may be null when error fires before a bubble is created — confirm the `if (activeBubble && activeBubble.wrap)` guard covers it; `progressMessageEl`/`progressWrap` closures exist in both inline and standalone builders for #C/#2); the inline progress element is revealed when narrating; the chat `setChatNavSearchMode(false)` restore is wired on ALL non-chat routes (no stale "Search" label after leaving chat).
- [ ] **Update reports:** add `docs/superpowers/reports/2026-05-29-static-theme-parity-round4-closure.md` summarizing items 1–13, the verified non-issues (occt spacing, popular-words gate, cache/errors), new i18n keys (`nav.search`, `flash.login_required`, `flash.session_expired`), and the not-runtime-smoke-tested caveat.

## Out of scope (intentional / verified non-issues)
- occt prefix spacing (verified equivalent via `QueryContext`), results-page popular-words feature gate (server-gated), cache blob-sandbox/CSP, a11y/skip-nav, responsive facet reflow, markdown `<img>` stripping, link `rel` hardening, in-place AJAX re-search, active-filter chips, dismiss buttons, broader help localization.
- Tier-3 **B** (remove no-op "More.." link) and **G** (advanced `(`/`)` / inner-quote escaping edge cases) — explicitly excluded by the requester.
- Cosmetic-only diffs (title trailing periods, redundant cache "indexed at" row, `_blank` vs `_olh`, avatars/timestamps) — not result-affecting.
