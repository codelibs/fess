import * as api from "./api.js";
import * as i18n from "./i18n.js";
import { t } from "./i18n.js";
import { sanitizeHtml } from "./format.js";
import * as auth from "./auth.js";
import * as search from "./search.js";
import * as chat from "./chat.js";
import * as router from "./router.js";
import * as errorView from "./error.js";
import * as profile from "./profile.js";
import * as help from "./help.js";
import * as advance from "./advance.js";
import * as cache from "./cache.js";

/** Show one SPA view section and hide the rest. H.2: focus management on route change. */
function showView(id) {
  const viewIds = ["home-view", "results-view", "advance-view", "error-view", "profile-view", "help-view", "chat-view", "cache-view"];
  for (const vid of viewIds) {
    const el = document.getElementById(vid);
    if (!el) continue;
    if (vid === id) {
      el.removeAttribute("hidden");
      // H.2: move keyboard focus to the first heading or the section root so
      // screen readers announce the new view after a client-side navigation.
      const heading = el.querySelector("h1, h2, h3");
      const target = heading || el;
      if (!target.hasAttribute("tabindex")) target.setAttribute("tabindex", "-1");
      // Defer focus so the element is visible before receiving focus.
      Promise.resolve().then(() => target.focus({ preventScroll: false }));
    } else {
      el.setAttribute("hidden", "");
    }
  }
}

/** Toggle the header search form visibility. */
function setSearchFormVisible(visible) {
  const form = document.getElementById("search-form");
  if (!form) return;
  if (visible) {
    form.removeAttribute("hidden");
  } else {
    form.setAttribute("hidden", "");
  }
}

/**
 * Render EOL / development-mode warning indicators in #warning-indicators.
 * Called once after api.init() so getConfig() is populated.
 * D.5: EOL uses fa-times-circle text-danger with Bootstrap Tooltip and optional link.
 * D.6: Dev-mode uses fa-exclamation-triangle text-warning with Bootstrap Tooltip and optional link.
 */
function renderWarnings() {
  const host = document.getElementById("warning-indicators");
  if (!host) return;
  while (host.firstChild) host.removeChild(host.firstChild);
  const features = api.getConfig()?.features || {};

  // D.5: EOL warning
  if (features.eoled) {
    const tooltipText = t("labels.eol_error");
    const icon = document.createElement("i");
    icon.className = "fas fa-times-circle text-danger";
    icon.setAttribute("aria-hidden", "true");

    let el;
    if (features.eol_link) {
      el = document.createElement("a");
      el.href = features.eol_link;
      el.target = "_blank";
      el.rel = "noopener noreferrer";
      el.className = "ms-2";
      el.setAttribute("aria-label", tooltipText);
    } else {
      el = document.createElement("span");
      el.className = "ms-2";
      el.setAttribute("aria-label", tooltipText);
    }
    el.setAttribute("data-bs-toggle", "tooltip");
    el.setAttribute("data-bs-title", tooltipText);
    el.appendChild(icon);
    host.appendChild(el);
    if (window.bootstrap?.Tooltip) new window.bootstrap.Tooltip(el);
  }

  // D.6: Development mode warning
  if (features.development_mode) {
    const tooltipText = t("labels.development_mode_warning");
    const icon = document.createElement("i");
    icon.className = "fas fa-exclamation-triangle text-warning";
    icon.setAttribute("aria-hidden", "true");

    let el;
    if (features.installation_link) {
      el = document.createElement("a");
      el.href = features.installation_link;
      el.target = "_blank";
      el.rel = "noopener noreferrer";
      el.className = "ms-2";
      el.setAttribute("aria-label", tooltipText);
    } else {
      el = document.createElement("span");
      el.className = "ms-2";
      el.setAttribute("aria-label", tooltipText);
    }
    el.setAttribute("data-bs-toggle", "tooltip");
    el.setAttribute("data-bs-title", tooltipText);
    el.appendChild(icon);
    host.appendChild(el);
    if (window.bootstrap?.Tooltip) new window.bootstrap.Tooltip(el);
  }
}

/**
 * Render notification banners in the #home-notification, #results-notification,
 * and #advance-notification slots from api config notifications.
 *
 * Notification HTML is sanitized through the whitelist sanitizer from format.js
 * before being appended — no raw innerHTML on live DOM nodes.
 * Slots with empty or absent notification values are hidden with d-none.
 *
 * Called once after api.init() so getConfig() is populated.
 */
function renderNotifications() {
  const cfg = api.getConfig() || {};
  const notifications = cfg.notifications || {};

  /** @param {string} elId - banner element id */
  /** @param {string} html - notification HTML from config */
  function applyNotification(elId, html) {
    const el = document.getElementById(elId);
    if (!el) return;
    while (el.firstChild) el.removeChild(el.firstChild);
    if (!html || typeof html !== "string" || html.trim() === "") {
      el.classList.add("d-none");
      return;
    }
    el.classList.remove("d-none");
    el.appendChild(sanitizeHtml(html));
  }

  applyNotification("home-notification", notifications.search_top || "");
  applyNotification("results-notification", notifications.search_top || "");
  applyNotification("advance-notification", notifications.advance_search || "");
}

/**
 * Render a message into the #home-flash aria-live region (parity with index.jsp:123-130
 * <la:info> / <la:errors> blocks). Accepts a plain text or sanitized HTML string.
 * Call with null/empty to hide the region.
 *
 * @param {string|null} message - text to show (null/empty = hide)
 * @param {"info"|"danger"} [level] - Bootstrap alert variant (default "info")
 */
export function renderHomeFlash(message, level = "info") {
  const el = document.getElementById("home-flash");
  if (!el) return;
  while (el.firstChild) el.removeChild(el.firstChild);
  if (!message || (typeof message === "string" && !message.trim())) {
    el.classList.add("d-none");
    return;
  }
  el.className = "alert alert-" + level + " mb-2";
  el.classList.remove("d-none");
  el.appendChild(document.createTextNode(message));
}

/**
 * Fetch popular words from /api/v2/popular-words and render them in #home-popular-words.
 * Delegates to the shared renderPopularWords renderer (parity-r3 A7): first 3 always
 * visible, index ≥ 3 carry d-sm-inline-block; no hard slice-to-5.
 */
async function renderHomePopularWords() {
  const host = document.getElementById("home-popular-words");
  if (!host) return;
  try {
    const data = await api.get("/popular-words");
    const words = data?.popular_words || data?.words || [];
    search.renderPopularWords(words, host);
  } catch { /* popular words are optional — fail silently */ }
}

/** Attach the home view search form submit handler and render popular words. */
function attachHomeView() {
  const form = document.getElementById("home-search-form");
  if (form && !form.dataset.attached) {
    form.dataset.attached = "1";
    form.addEventListener("submit", ev => {
      ev.preventDefault();
      const input = document.getElementById("home-search-input");
      const q = input ? input.value.trim() : "";
      if (q) {
        // Carry the up-front home option selections (sort / num / lang) into the
        // search URL so the executed search honours them. runFromUrl() parses them.
        const params = new URLSearchParams();
        params.set("q", q);
        search.applyHomeOptions(params);
        router.navigate("/search?" + params.toString());
        // JSP parity: disable the submit button for 3s after navigation has been
        // triggered, to prevent rapid double-submits.
        search.disableSubmitBriefly(form.querySelector("[data-i18n='search.button']"));
      }
    });
  }
  // Apply i18n placeholder manually (data-i18n-placeholder is applied by i18n.js
  // on load, but the element may not have existed at that point).
  const input = document.getElementById("home-search-input");
  if (input && !input.placeholder) {
    input.placeholder = t("search.placeholder");
  }
  // Apply button text
  const btn = document.querySelector("#home-search-form [data-i18n='search.button']");
  if (btn && !btn.textContent.trim()) {
    btn.textContent = t("search.button");
  }
  // Autofocus the search box when the home view is revealed (parity with index.jsp).
  // Deferred so it wins over showView()'s heading focus.
  if (input) { Promise.resolve().then(() => input.focus()); }
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
  renderHomePopularWords();
}

/**
 * D.8: Render footer copyright via safe DOM construction (no innerHTML / no sanitizer needed
 * because every node is constructed directly — no user-supplied strings are rendered as HTML).
 * The release year and org name come from i18n keys so they can be updated without code changes.
 */
function renderFooterCopyright() {
  const el = document.getElementById("footer-copyright");
  if (!el) return;
  while (el.firstChild) el.removeChild(el.firstChild);

  // Use a NUL sentinel as a placeholder for the org name so we can split the
  // translated template into text segments and insert the <a> link in the middle.
  const year = t("footer.copyright_year");
  const org = t("footer.copyright_org");
  const SENTINEL = "\x00";
  const rendered = t("footer.copyright", [year, SENTINEL]);
  const parts = rendered.split(SENTINEL);

  el.appendChild(document.createTextNode(parts[0] || ""));

  const a = document.createElement("a");
  a.href = "https://github.com/codelibs";
  a.rel = "noopener noreferrer";
  a.target = "_blank";
  a.textContent = org;
  el.appendChild(a);

  el.appendChild(document.createTextNode(parts[1] || ""));
}

/**
 * D.4: Conditionally insert a Chat nav link when rag_chat_enabled is true.
 * The Phase E agent will add the <a> element to the markup; until then we
 * create it dynamically next to the Help link so chat navigation works.
 */
function renderChatNavLink() {
  const features = api.getConfig()?.features || {};
  if (!features.rag_chat_enabled) return;
  // If markup already has the chat link (added by Phase E agent), just show it.
  const existing = document.getElementById("chat-nav-link");
  if (existing) {
    existing.classList.remove("d-none");
    return;
  }
  // Otherwise create it and insert before the Help link.
  const helpLink = document.querySelector("nav a[href='/help']");
  if (!helpLink) return;
  const chatLink = document.createElement("a");
  chatLink.id = "chat-nav-link";
  chatLink.className = "nav-link px-2";
  chatLink.href = "/chat";
  chatLink.setAttribute("data-spa", "");
  chatLink.setAttribute("data-i18n", "nav.chat_ai_mode");
  chatLink.textContent = t("nav.chat_ai_mode");
  helpLink.parentNode.insertBefore(chatLink, helpLink);
}

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

/** Attach back-to-top button behaviour. */
function attachBackToTop() {
  const btn = document.getElementById("back-to-top");
  if (!btn) return;
  window.addEventListener("scroll", () => {
    btn.classList.toggle("is-hidden", window.scrollY <= 200);
  });
  btn.addEventListener("click", () => {
    window.scrollTo({ top: 0, behavior: "smooth" });
  });
}

/** Returns true when the current URL contains a non-empty q= parameter. */
function hasSearchQuery() {
  return new URLSearchParams(location.search).get("q")?.trim().length > 0;
}

/**
 * Forward the current query AND paging/state params to the "Advanced" links so
 * navigating to /advance carries ?q= plus the relevant state (num/sort/lang/labels).
 * JSP parity: header.jsp:119 uses fe:pagingQuery(null) which forwards q + paging
 * state; advance.js can consume these to pre-seed the form (parity-r3 A8).
 *
 * The query is taken from whichever search box is populated (header or home),
 * falling back to the current URL's q= param.
 */
function updateAdvanceLinks() {
  const headerVal = (document.getElementById("search-input") || {}).value;
  const homeVal = (document.getElementById("home-search-input") || {}).value;
  const urlParams = new URLSearchParams(location.search);
  const urlQ = urlParams.get("q") || "";
  const q = (headerVal || homeVal || urlQ || "").trim();
  if (!q) {
    document.querySelectorAll('a[href^="/advance"]').forEach(a => {
      a.setAttribute("href", "/advance");
    });
    return;
  }

  // Build paging/state params from URL (populated by runFromUrl on results view).
  const advParams = new URLSearchParams();
  advParams.set("q", q);
  // num / sort — forward if present and non-empty in URL
  const num = urlParams.get("num");
  if (num) advParams.set("num", num);
  const sort = urlParams.get("sort");
  if (sort) advParams.set("sort", sort);
  // lang — multi-valued; forward all
  urlParams.getAll("lang").filter(v => v !== "").forEach(v => advParams.append("lang", v));
  // fields.label — multi-valued; forward all
  urlParams.getAll("fields.label").filter(v => v !== "").forEach(v => advParams.append("fields.label", v));

  const href = "/advance?" + advParams.toString();
  document.querySelectorAll('a[href^="/advance"]').forEach(a => {
    a.setAttribute("href", href);
  });
}

/** Wire input/route hooks so the Advanced links stay in sync with the query. */
function attachAdvanceLinkSync() {
  for (const id of ["search-input", "home-search-input"]) {
    const el = document.getElementById(id);
    if (el && !el.dataset.advLinkSync) {
      el.dataset.advLinkSync = "1";
      el.addEventListener("input", updateAdvanceLinks);
    }
  }
  // Refresh on every route change so a freshly shown view reflects the query.
  document.addEventListener("fess:route:change", updateAdvanceLinks);
  updateAdvanceLinks();
}

function registerRoutes() {
  // Home route — "/" with no q= parameter shows the centered home view.
  router.register(
    path => (path === "/" || path === "/index" || path === "/index.html") && !hasSearchQuery(),
    () => {
      setChatNavSearchMode(false);
      setSearchFormVisible(false);
      showView("home-view");
      attachHomeView();
    }
  );

  // Search / results routes — show results view and run search.
  router.register(
    path => path === "/" || path === "/search" || path === "/index" || path === "/index.html",
    () => {
      setChatNavSearchMode(false);
      setSearchFormVisible(true);
      showView("results-view");
      // search.attach() is idempotent; wires DOM listeners once.
      search.attach();
      // A.8: re-run search from URL on every dispatch (handles popstate / back-forward).
      search.runFromUrl();
    }
  );

  // Profile view — password change.
  router.register(
    path => path === "/profile",
    () => {
      setChatNavSearchMode(false);
      setSearchFormVisible(false);
      showView("profile-view");
      profile.attach();
    }
  );

  // Advanced search view.
  router.register(
    path => path === "/advance",
    () => {
      setChatNavSearchMode(false);
      setSearchFormVisible(false);
      showView("advance-view");
      advance.attach();
    }
  );

  // Help view.
  router.register(
    path => path === "/help",
    () => {
      setChatNavSearchMode(false);
      setSearchFormVisible(false);
      showView("help-view");
      help.attach();
    }
  );

  // Chat view — full-width standalone chat page.
  router.register(
    path => path === "/chat",
    () => {
      setSearchFormVisible(false);
      setChatNavSearchMode(true);
      showView("chat-view");
      chat.attachStandalone();
    }
  );

  // Cache viewer — /cache and /cache/* (e.g. /cache/?docId=...).
  // Renders cached content inside a sandboxed iframe. The legacy CacheAction
  // is bypassed; content is fetched from /api/v2/cache/{docId} instead.
  router.register(
    path => path === "/cache" || path.startsWith("/cache/"),
    () => {
      setChatNavSearchMode(false);
      setSearchFormVisible(false);
      showView("cache-view");
      cache.attach();
    }
  );

  // Error routes — /error, /error/*, /error/400, /error/404, etc.
  router.register(
    path => path === "/error" || path.startsWith("/error/"),
    () => {
      setChatNavSearchMode(false);
      setSearchFormVisible(false);
      showView("error-view");
      errorView.attach();
    }
  );

  // Fallback: unknown paths show error view with code 404.
  router.register(
    () => true,
    () => {
      setChatNavSearchMode(false);
      setSearchFormVisible(false);
      showView("error-view");
      errorView.attach();
    }
  );
}

async function main() {
  try {
    await api.init();
  } catch (e) {
    console.error("Fess /ui/config failed:", e);
  }
  await i18n.init();
  // Render warning indicators after config is loaded.
  renderWarnings();
  // Render notification banners from config.notifications.
  renderNotifications();
  // D.8: Render footer copyright with CodeLibs link.
  renderFooterCopyright();
  // D.4: Conditionally insert Chat nav link.
  renderChatNavLink();
  await auth.attach();
  search.attach();
  chat.attach(); // no-op when rag_chat_enabled is false
  // After login, refresh results without re-attaching event listeners.
  // search.attach() is idempotent but search.refresh() is semantically cleaner.
  document.addEventListener("fess:auth:login", () => search.refresh());

  // Wire back-to-top button.
  attachBackToTop();

  // Keep the "Advanced" links carrying the current query (JSP parity).
  attachAdvanceLinkSync();

  // Client-side routing: register routes then attach listeners and dispatch.
  registerRoutes();
  router.attach();
  router.dispatch();
}

if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", main);
} else {
  main();
}
