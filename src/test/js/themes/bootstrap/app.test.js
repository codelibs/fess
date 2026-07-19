// SPDX-License-Identifier: Apache-2.0
// Tests for app.js — the bootstrap theme SPA boot module.
//
// app.js runs main() at import time when document.readyState === "complete"
// (the jsdom default). To import the module WITHOUT booting the whole SPA, the
// readyState getter is forced to "loading" BEFORE the (dynamic) import so app.js
// takes the DOMContentLoaded-listener branch instead of calling main(). main()
// is then called explicitly in the tests that exercise the boot flow.
//
// Every sibling module app.js imports is mocked to an inert stub so main() and
// the route handlers are fully controllable and side-effect-free. format.js is
// left REAL because renderHomeFlash / renderNotifications rely on its DOM-safe
// text/sanitize construction. The real i18n.t() is replaced by an identity
// stub, so text assertions target the caller-supplied string / i18n key.

import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { resetDom } from "../../helpers/dom.js";

vi.mock("../../../../main/webapp/themes/bootstrap/assets/api.js", () => ({
  init: vi.fn(async () => {}),
  getConfig: vi.fn(() => ({ features: {} })),
  get: vi.fn(async () => ({ popular_words: [] })),
  post: vi.fn(async () => ({})),
  isAuthenticated: vi.fn(() => false),
  setAuthenticated: vi.fn(),
  getCsrfToken: vi.fn(() => ""),
  setCsrfToken: vi.fn(),
  ApiError: class extends Error {},
  NetworkError: class extends Error {},
}));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/i18n.js", () => ({
  init: vi.fn(async () => {}),
  t: (k) => k,
  applyDom: () => {},
  languageLabel: (v) => v,
  getLocale: () => "en",
  pickLocale: () => "en",
}));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/auth.js", () => ({
  attach: vi.fn(async () => null),
  isLoginLinkEnabled: () => false,
  probeMe: vi.fn(async () => null),
  buildUserDropdown: () => document.createElement("div"),
  buildLoginLink: () => document.createElement("a"),
  rotateCsrf: vi.fn(async () => {}),
}));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/search.js", () => ({
  attach: vi.fn(),
  runFromUrl: vi.fn(),
  refresh: vi.fn(),
  clearSearchState: vi.fn(),
  attachSuggest: vi.fn(),
  disableSubmitBriefly: vi.fn(),
  renderPopularWords: vi.fn(),
}));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/chat.js", () => ({
  attach: vi.fn(),
  attachInline: vi.fn(),
  attachStandalone: vi.fn(),
}));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/router.js", () => ({
  register: vi.fn(),
  navigate: vi.fn(),
  attach: vi.fn(),
  dispatch: vi.fn(),
}));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/error.js", () => ({ attach: vi.fn() }));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/profile.js", () => ({ attach: vi.fn() }));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/help.js", () => ({ attach: vi.fn(async () => {}) }));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/advance.js", () => ({ attach: vi.fn() }));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/cache.js", () => ({ attach: vi.fn() }));

import * as api from "../../../../main/webapp/themes/bootstrap/assets/api.js";
import * as i18n from "../../../../main/webapp/themes/bootstrap/assets/i18n.js";
import * as auth from "../../../../main/webapp/themes/bootstrap/assets/auth.js";
import * as search from "../../../../main/webapp/themes/bootstrap/assets/search.js";
import * as chat from "../../../../main/webapp/themes/bootstrap/assets/chat.js";
import * as router from "../../../../main/webapp/themes/bootstrap/assets/router.js";
import * as errorView from "../../../../main/webapp/themes/bootstrap/assets/error.js";
import * as profile from "../../../../main/webapp/themes/bootstrap/assets/profile.js";
import * as help from "../../../../main/webapp/themes/bootstrap/assets/help.js";
import * as advance from "../../../../main/webapp/themes/bootstrap/assets/advance.js";
import * as cache from "../../../../main/webapp/themes/bootstrap/assets/cache.js";

// CRITICAL: neutralize main() by making readyState report "loading" BEFORE the
// dynamic import, so app.js registers a DOMContentLoaded listener instead of
// auto-running main() at import time.
Object.defineProperty(document, "readyState", { configurable: true, get: () => "loading" });
const { renderHomeFlash, hasSearchQuery, updateAdvanceLinks, registerRoutes, main } = await import(
  "../../../../main/webapp/themes/bootstrap/assets/app.js"
);

/** Set the current URL (search string) without a reload, so location.search reads it. */
function setSearch(url) {
  window.history.replaceState({}, "", url);
}

/** Let queued microtasks (deferred focus, async awaits) settle. */
const flush = () => new Promise((r) => setTimeout(r));

/** A full index-like fixture covering every id app.js and its route handlers touch. */
function mountFullDom() {
  document.body.innerHTML = `
    <nav class="navbar fixed-top">
      <ul id="header-nav" class="navbar-nav"></ul>
      <a id="brand-link" class="d-inline-flex"></a>
      <div id="search-form-wrap"></div>
      <li id="chat-nav-item" class="d-none"></li>
      <a id="chat-nav-link" href="/chat"><span data-i18n="nav.chat_ai_mode">Chat</span></a>
    </nav>
    <div id="home-notification"></div>
    <div id="results-notification"></div>
    <div id="advance-notification"></div>
    <div id="home-view">
      <h1>Home</h1>
      <form id="home-search-form">
        <input id="contentQuery">
        <button type="submit" data-i18n="search.button"></button>
      </form>
      <select id="sortSearchOption"></select>
      <select id="numSearchOption"></select>
      <select id="langSearchOption" multiple></select>
      <div id="home-popular-words"></div>
      <div id="home-flash"></div>
    </div>
    <div id="results-view" hidden></div>
    <div id="advance-view" hidden></div>
    <div id="error-view" hidden></div>
    <div id="profile-view" hidden></div>
    <div id="help-view" hidden></div>
    <div id="chat-view" hidden></div>
    <div id="cache-view" hidden></div>
    <input id="query">
    <a class="adv" href="/search/advance">Advanced</a>
    <div id="footer-copyright"></div>
    <div id="back-to-top"></div>
    <div id="searchOptions"></div>`;
}

beforeEach(() => {
  resetDom();
  vi.clearAllMocks();
  // clearAllMocks wipes implementations set inside the mock factory, so restore
  // the ones whose return values main()/handlers depend on.
  api.getConfig.mockReturnValue({ features: {} });
  api.get.mockResolvedValue({ popular_words: [] });
  api.init.mockResolvedValue(undefined);
  i18n.init.mockResolvedValue(undefined);
  auth.attach.mockResolvedValue(null);
  help.attach.mockResolvedValue(undefined);
  setSearch("/");
});
afterEach(resetDom);

// ---------------------------------------------------------------------------
// renderHomeFlash (existing coverage — unchanged)
// ---------------------------------------------------------------------------

describe("renderHomeFlash", () => {
  it("shows an info alert with the message text and no d-none", () => {
    document.body.innerHTML = '<div id="home-flash"></div>';
    renderHomeFlash("Hello");
    const el = document.getElementById("home-flash");
    expect(el.textContent).toBe("Hello");
    expect(el.className).toContain("alert-info");
    expect(el.classList.contains("d-none")).toBe(false);
  });

  it("uses the given level to pick the alert variant", () => {
    document.body.innerHTML = '<div id="home-flash"></div>';
    renderHomeFlash("bad", "danger");
    const el = document.getElementById("home-flash");
    expect(el.className).toContain("alert-danger");
    expect(el.textContent).toBe("bad");
  });

  it("hides the region (d-none, empty) for a null message", () => {
    document.body.innerHTML = '<div id="home-flash">stale</div>';
    renderHomeFlash(null);
    const el = document.getElementById("home-flash");
    expect(el.classList.contains("d-none")).toBe(true);
    expect(el.textContent).toBe("");
  });

  it("hides the region for a blank/whitespace message", () => {
    document.body.innerHTML = '<div id="home-flash"></div>';
    renderHomeFlash("   ");
    const el = document.getElementById("home-flash");
    expect(el.classList.contains("d-none")).toBe(true);
    expect(el.textContent).toBe("");
  });

  it("inserts HTML-looking input as a literal text node (XSS-safe)", () => {
    document.body.innerHTML = '<div id="home-flash"></div>';
    renderHomeFlash("<b>x</b>");
    const el = document.getElementById("home-flash");
    expect(el.textContent).toBe("<b>x</b>");
    expect(el.querySelector("b")).toBeNull();
    expect(el.children.length).toBe(0);
  });

  it("does not throw when #home-flash is absent", () => {
    expect(() => renderHomeFlash("hi")).not.toThrow();
  });
});

// ---------------------------------------------------------------------------
// hasSearchQuery — reads location.search for a non-empty q=
// ---------------------------------------------------------------------------

describe("hasSearchQuery", () => {
  it("returns true for a non-empty q= parameter", () => {
    setSearch("/?q=hello");
    expect(hasSearchQuery()).toBe(true);
  });

  it("returns false when q= is absent", () => {
    setSearch("/");
    expect(hasSearchQuery()).toBe(false);
  });

  it("returns false when q= is present but whitespace-only", () => {
    setSearch("/?q=%20%20");
    expect(hasSearchQuery()).toBe(false);
  });

  it("returns false when q= is empty", () => {
    setSearch("/?q=");
    expect(hasSearchQuery()).toBe(false);
  });

  it("returns true even when other params precede q=", () => {
    setSearch("/?num=20&q=abc");
    expect(hasSearchQuery()).toBe(true);
  });
});

// ---------------------------------------------------------------------------
// updateAdvanceLinks — forwards q/num/sort/lang/fields.label to /search/advance
// ---------------------------------------------------------------------------

describe("updateAdvanceLinks", () => {
  it("forwards the header query and URL paging state onto the advance link", () => {
    document.body.innerHTML =
      '<input id="query" value="hello"><a class="adv" href="/search/advance">A</a>';
    setSearch("/?num=20&sort=score&lang=en&fields.label=foo");
    updateAdvanceLinks();
    const href = document.querySelector("a.adv").getAttribute("href");
    expect(href.startsWith("/search/advance?")).toBe(true);
    const qs = new URLSearchParams(href.split("?")[1]);
    expect(qs.get("q")).toBe("hello");
    expect(qs.get("num")).toBe("20");
    expect(qs.get("sort")).toBe("score");
    expect(qs.getAll("lang")).toEqual(["en"]);
    expect(qs.getAll("fields.label")).toEqual(["foo"]);
  });

  it("falls back to the URL q= when no input is populated", () => {
    document.body.innerHTML = '<a class="adv" href="/search/advance">A</a>';
    setSearch("/?q=urlq");
    updateAdvanceLinks();
    expect(document.querySelector("a.adv").getAttribute("href")).toBe("/search/advance?q=urlq");
  });

  it("prefers the header #query value over the home #contentQuery value", () => {
    document.body.innerHTML =
      '<input id="query" value="fromHeader"><input id="contentQuery" value="fromHome">' +
      '<a class="adv" href="/search/advance">A</a>';
    setSearch("/");
    updateAdvanceLinks();
    const qs = new URLSearchParams(
      document.querySelector("a.adv").getAttribute("href").split("?")[1]
    );
    expect(qs.get("q")).toBe("fromHeader");
  });

  it("emits a bare /search/advance href when there is no query at all", () => {
    document.body.innerHTML = '<a class="adv" href="/search/advance">A</a>';
    setSearch("/");
    updateAdvanceLinks();
    expect(document.querySelector("a.adv").getAttribute("href")).toBe("/search/advance");
  });

  it("forwards multiple lang and fields.label values", () => {
    document.body.innerHTML = '<a class="adv" href="/search/advance">A</a>';
    setSearch("/?q=x&lang=en&lang=ja&fields.label=a&fields.label=b");
    updateAdvanceLinks();
    const qs = new URLSearchParams(
      document.querySelector("a.adv").getAttribute("href").split("?")[1]
    );
    expect(qs.getAll("lang")).toEqual(["en", "ja"]);
    expect(qs.getAll("fields.label")).toEqual(["a", "b"]);
  });
});

// ---------------------------------------------------------------------------
// registerRoutes — the route table wired into router.register
// ---------------------------------------------------------------------------

describe("registerRoutes", () => {
  /** @returns {[Function, Function][]} the (predicate, handler) pairs registered */
  function registered() {
    registerRoutes();
    return router.register.mock.calls;
  }

  it("registers nine routes as (predicate, handler) pairs", () => {
    const calls = registered();
    expect(calls.length).toBe(9);
    for (const [predicate, handler] of calls) {
      expect(typeof predicate).toBe("function");
      expect(typeof handler).toBe("function");
    }
  });

  it("registers a home route matching '/' only when there is no q=", () => {
    const calls = registered();
    setSearch("/");
    const homeRoute = calls.find(([p]) => p("/") === true);
    expect(homeRoute).toBeTruthy();
    // The home predicate must reject '/' once a q= is present (results take over).
    const homePredicate = calls[0][0];
    setSearch("/");
    expect(homePredicate("/")).toBe(true);
    setSearch("/?q=x");
    expect(homePredicate("/")).toBe(false);
  });

  it("registers a search/results route matching /search and /index paths", () => {
    const calls = registered();
    const searchPredicate = calls[1][0];
    expect(searchPredicate("/search")).toBe(true);
    expect(searchPredicate("/")).toBe(true);
    expect(searchPredicate("/index")).toBe(true);
    expect(searchPredicate("/index.html")).toBe(true);
    expect(searchPredicate("/profile")).toBe(false);
  });

  it("registers predicates covering profile, advance, help, chat, cache and error", () => {
    const calls = registered();
    const matches = (path) => calls.some(([p]) => p(path));
    expect(matches("/profile")).toBe(true);
    expect(matches("/advance")).toBe(true);
    expect(matches("/search/advance")).toBe(true);
    expect(matches("/help")).toBe(true);
    expect(matches("/chat")).toBe(true);
    expect(matches("/cache")).toBe(true);
    expect(matches("/cache/?docId=1")).toBe(true);
    expect(matches("/error")).toBe(true);
    expect(matches("/error/404")).toBe(true);
  });

  it("registers a catch-all fallback predicate returning true for unknown paths", () => {
    const calls = registered();
    const fallback = calls[calls.length - 1][0];
    expect(fallback("/totally-unknown")).toBe(true);
    expect(fallback("")).toBe(true);
  });

  it("route handlers reveal the matching view and hide the rest (no throw)", async () => {
    mountFullDom();
    const calls = registered();
    const hiddenState = () =>
      Object.fromEntries(
        ["home-view", "results-view", "advance-view", "profile-view", "help-view", "chat-view", "cache-view", "error-view"].map(
          (id) => [id, document.getElementById(id).hasAttribute("hidden")]
        )
      );

    // Home handler (index 0) reveals home-view and clears search state.
    setSearch("/");
    expect(() => calls[0][1]()).not.toThrow();
    await flush();
    expect(hiddenState()["home-view"]).toBe(false);
    expect(hiddenState()["results-view"]).toBe(true);
    expect(search.clearSearchState).toHaveBeenCalled();

    // Results handler (index 1) reveals results-view and runs the URL search.
    calls[1][1]();
    expect(hiddenState()["results-view"]).toBe(false);
    expect(hiddenState()["home-view"]).toBe(true);
    expect(search.attach).toHaveBeenCalled();
    expect(search.runFromUrl).toHaveBeenCalled();

    // Profile handler.
    calls[2][1]();
    expect(hiddenState()["profile-view"]).toBe(false);
    expect(profile.attach).toHaveBeenCalled();

    // Advance handler.
    calls[3][1]();
    expect(hiddenState()["advance-view"]).toBe(false);
    expect(advance.attach).toHaveBeenCalled();

    // Help handler.
    calls[4][1]();
    expect(hiddenState()["help-view"]).toBe(false);
    expect(help.attach).toHaveBeenCalled();

    // Chat handler puts the nav link into "Search" mode and mounts standalone chat.
    calls[5][1]();
    expect(hiddenState()["chat-view"]).toBe(false);
    expect(chat.attachStandalone).toHaveBeenCalled();
    expect(document.getElementById("chat-nav-link").getAttribute("href")).toBe("/");

    // Cache handler.
    calls[6][1]();
    expect(hiddenState()["cache-view"]).toBe(false);
    expect(cache.attach).toHaveBeenCalled();

    // Error handler.
    calls[7][1]();
    expect(hiddenState()["error-view"]).toBe(false);
    expect(errorView.attach).toHaveBeenCalled();

    // Fallback handler (index 8) also lands on the error view.
    calls[8][1]();
    expect(hiddenState()["error-view"]).toBe(false);
  });
});

// ---------------------------------------------------------------------------
// main — the SPA boot sequence
// ---------------------------------------------------------------------------

describe("main", () => {
  it("boots: awaits api/i18n init, attaches auth+search, wires the router (no throw)", async () => {
    mountFullDom();
    await expect(main()).resolves.toBeUndefined();

    expect(api.init).toHaveBeenCalledTimes(1);
    expect(i18n.init).toHaveBeenCalledTimes(1);
    expect(auth.attach).toHaveBeenCalledTimes(1);
    expect(search.attach).toHaveBeenCalledTimes(1);
    // registerRoutes -> nine router.register calls, then attach + dispatch.
    expect(router.register).toHaveBeenCalledTimes(9);
    expect(router.attach).toHaveBeenCalledTimes(1);
    expect(router.dispatch).toHaveBeenCalledTimes(1);
  });

  it("renders EOL + dev-mode warnings and reveals the chat nav item from config", async () => {
    mountFullDom();
    api.getConfig.mockReturnValue({
      features: {
        eoled: true,
        eol_link: "https://example.test/eol",
        development_mode: true,
        installation_link: "https://example.test/install",
        rag_chat_enabled: true,
      },
    });
    await main();
    // Two warning indicators inserted into #header-nav.
    const warnings = document.querySelectorAll("#header-nav li.warning-indicator");
    expect(warnings.length).toBe(2);
    // Chat nav item revealed (d-none removed).
    expect(document.getElementById("chat-nav-item").classList.contains("d-none")).toBe(false);
  });

  it("renders sanitized notification banners from config into the slots", async () => {
    mountFullDom();
    api.getConfig.mockReturnValue({
      features: {},
      notifications: { search_top: "<b>Top</b>", advance_search: "<i>Adv</i>" },
    });
    await main();
    const home = document.getElementById("home-notification");
    expect(home.classList.contains("d-none")).toBe(false);
    expect(home.textContent).toContain("Top");
    const adv = document.getElementById("advance-notification");
    expect(adv.textContent).toContain("Adv");
  });

  it("renders the footer copyright with a CodeLibs link", async () => {
    mountFullDom();
    await main();
    const footer = document.getElementById("footer-copyright");
    const link = footer.querySelector("a");
    expect(link).toBeTruthy();
    expect(link.getAttribute("href")).toBe("https://github.com/codelibs");
  });

  it("still completes when api.init() rejects (error is swallowed and logged)", async () => {
    mountFullDom();
    const spy = vi.spyOn(console, "error").mockImplementation(() => {});
    api.init.mockRejectedValueOnce(new Error("config down"));
    await expect(main()).resolves.toBeUndefined();
    // Boot continued past the failed api.init.
    expect(i18n.init).toHaveBeenCalledTimes(1);
    expect(router.dispatch).toHaveBeenCalledTimes(1);
    expect(spy).toHaveBeenCalled();
    spy.mockRestore();
  });

  it("does not throw when booting against a minimal (mostly empty) DOM", async () => {
    document.body.innerHTML = "";
    await expect(main()).resolves.toBeUndefined();
    expect(router.dispatch).toHaveBeenCalledTimes(1);
  });
});
