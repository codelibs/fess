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
      Promise.resolve().then(() => target.focus({ preventScroll: true }));
    } else {
      el.setAttribute("hidden", "");
    }
  }
  // JSP parity: home (index.jsp) shows an empty navbar-brand; other views show the logo.
  setBrandVisible(id !== "home-view");
  // JSP parity (REFERENCE §HEADER): the shared header (header.jsp) is identical on
  // every view including home — brand logo + search box are always shown. The only
  // exception is the chat page, where setSearchFormVisible(false) hides the search
  // box. So no per-view brand/advanced toggling is done here.
}

/** Toggle the header search form visibility (hides the whole input-group wrapper). */
function setSearchFormVisible(visible) {
  const wrap = document.getElementById("search-form-wrap") || document.getElementById("search-form");
  if (!wrap) return;
  if (visible) {
    wrap.removeAttribute("hidden");
    wrap.classList.remove("d-none");
    wrap.classList.add("d-flex");
  } else {
    wrap.setAttribute("hidden", "");
    wrap.classList.remove("d-flex");
    wrap.classList.add("d-none");
  }
}

/** Toggle the header brand logo. JSP parity: home (index.jsp) has an empty
 *  navbar-brand; all other views show the logo. d-inline-flex is !important so
 *  swap it with d-none rather than relying on the hidden attribute. */
function setBrandVisible(visible) {
  const brand = document.getElementById("brand-link");
  if (!brand) return;
  if (visible) {
    brand.classList.remove("d-none");
    brand.classList.add("d-inline-flex");
  } else {
    brand.classList.remove("d-inline-flex");
    brand.classList.add("d-none");
  }
}

/**
 * Render EOL / development-mode warning indicators in #warning-indicators.
 * Called once after api.init() so getConfig() is populated.
 * D.5: EOL uses fa-times-circle text-danger with Bootstrap Tooltip and optional link.
 * D.6: Dev-mode uses fa-exclamation-triangle text-warning with Bootstrap Tooltip and optional link.
 */
function renderWarnings() {
  const nav = document.getElementById("header-nav");
  if (!nav) return;
  // Remove any previously-rendered warning li entries.
  nav.querySelectorAll("li.nav-item.warning-indicator").forEach(el => el.remove());
  const features = api.getConfig()?.features || {};

  // header.jsp parity: warnings are li.nav-item > a.nav-link.active > i at the
  // start of ul.nav.navbar-nav, with a tooltip on the li.
  function buildWarning(iconClass, tooltipText, link) {
    const li = document.createElement("li");
    li.className = "nav-item warning-indicator";
    li.setAttribute("data-bs-toggle", "tooltip");
    li.setAttribute("data-bs-placement", "left");
    li.setAttribute("title", tooltipText);
    const a = document.createElement("a");
    a.className = "nav-link active";
    if (link) { a.href = link; a.target = "_blank"; a.rel = "noopener noreferrer"; }
    else { a.href = "#"; }
    a.setAttribute("aria-label", tooltipText);
    const icon = document.createElement("i");
    icon.className = iconClass;
    icon.setAttribute("aria-hidden", "true");
    a.appendChild(icon);
    li.appendChild(a);
    if (window.bootstrap?.Tooltip) new window.bootstrap.Tooltip(li);
    return li;
  }

  const first = nav.firstChild;
  // D.5: EOL warning
  if (features.eoled) {
    nav.insertBefore(buildWarning("fas fa-times-circle text-danger", t("labels.eol_error"), features.eol_link), first);
  }
  // D.6: Development mode warning
  if (features.development_mode) {
    nav.insertBefore(buildWarning("fa fa-exclamation-triangle text-warning", t("labels.development_mode_warning"), features.installation_link), first);
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
      const input = document.getElementById("contentQuery");
      const q = input ? input.value.trim() : "";
      if (q) {
        const params = new URLSearchParams();
        params.set("q", q);
        // JSP parity: carry the shared drawer's sort / num / lang selections into
        // the first search so options set on the home view before searching are applied
        // (these selects no longer auto-run a search on change).
        const sortSel = document.getElementById("sortSearchOption");
        if (sortSel && sortSel.value) params.set("sort", sortSel.value);
        const numSel = document.getElementById("numSearchOption");
        if (numSel && numSel.value) params.set("num", numSel.value);
        const langSel = document.getElementById("langSearchOption");
        if (langSel) Array.from(langSel.selectedOptions).map(o => o.value).filter(Boolean).forEach(v => params.append("lang", v));
        router.navigate("/search?" + params.toString());
        // JSP parity: disable the submit button for 3s after navigation has been
        // triggered, to prevent rapid double-submits.
        search.disableSubmitBriefly(document.querySelector("#home-search-form button[type=submit]"));
      }
    });
  }
  // Apply i18n placeholder manually (data-i18n-placeholder is applied by i18n.js
  // on load, but the element may not have existed at that point).
  const input = document.getElementById("contentQuery");
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
      // submitOnSelect: true — clicking a suggestion on the home page submits the search
      // form immediately, matching default-JSP suggestor.js and results-page header behavior.
      submitOnSelect: true,
      get lang() {
        // Home and results share the single #searchOptions drawer (JSP parity),
        // so the suggest lang filter reads the drawer's #langSearchOption select.
        const sel = document.getElementById("langSearchOption");
        return sel ? Array.from(sel.selectedOptions).map(o => o.value).filter(v => v !== "") : [];
      }
    });
  }
  // The home options Clear button is the shared drawer #searchOptionsClearButton,
  // wired in search.js attach(); no separate home clear handler is needed.
  // #A (parity index.jsp:123-130): surface a query-param-driven flash message on the
  // home view (e.g. an auth redirect that appends ?error=login_required). Only keys on
  // the explicit allowlist below are rendered, so an attacker cannot reflect arbitrary
  // text into the alert via the query string (phishing primitive); the text itself is
  // resolved through the flash.* i18n bundle, never the raw query value.
  // home-flash-query-message
  const ALLOWED_FLASH_KEYS = new Set(["login_required", "session_expired"]);
  const flashParams = new URLSearchParams(location.search);
  const errKey = flashParams.get("error");
  const msgKey = flashParams.get("msg");
  if (errKey && ALLOWED_FLASH_KEYS.has(errKey)) {
    renderHomeFlash(t("flash." + errKey), "danger");
  } else if (msgKey && ALLOWED_FLASH_KEYS.has(msgKey)) {
    renderHomeFlash(t("flash." + msgKey), "info");
  } else {
    renderHomeFlash(null);
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
  // The chat nav entry is the li.nav-item#chat-nav-item in markup (header.jsp
  // parity). Reveal it by removing d-none from the li wrapper.
  const item = document.getElementById("chat-nav-item");
  if (item) item.classList.remove("d-none");
}

/**
 * #F (parity header.jsp:83-89): on the chat route, turn the chat nav link into a
 * "Search" link (href "/", fa-search). On any other route restore the chat label.
 * header-search-link
 */
function setChatNavSearchMode(onChat) {
  const link = document.getElementById("chat-nav-link");
  if (!link) return;
  // data-i18n lives on an inner <span> (not the <a>) so i18n.js's textContent
  // reset keeps the leading icon. Mirror header.jsp: fa-robot for the chat link,
  // fa-search for the "Search" link shown while on the chat route.
  link.removeAttribute("data-i18n");
  while (link.firstChild) link.removeChild(link.firstChild);
  const icon = document.createElement("i");
  icon.setAttribute("aria-hidden", "true");
  const span = document.createElement("span");
  if (onChat) {
    link.href = "/";
    icon.className = "fa fa-fw fa-search";
    span.setAttribute("data-i18n", "nav.search");
    span.textContent = t("nav.search");
  } else {
    link.href = "/chat";
    icon.className = "fa fa-fw fa-robot";
    span.setAttribute("data-i18n", "nav.chat_ai_mode");
    span.textContent = t("nav.chat_ai_mode");
  }
  link.appendChild(icon);
  link.appendChild(document.createTextNode(" "));
  link.appendChild(span);
}

// GAP A: keep body top-padding equal to the fixed-top header height so content is never hidden beneath it.
function syncHeaderOffset() {
  const header = document.querySelector(".navbar.fixed-top");
  if (!header) return;
  // JSP parity (css/style.css: body { padding: 1em 0; margin: 56px 0 4em }): content
  // clears the fixed-top navbar plus a 1em gap, so add that gap on top of the live
  // navbar height instead of butting content right against the bar.
  const gap = parseFloat(getComputedStyle(document.documentElement).fontSize) || 16;
  document.body.style.paddingTop = (header.offsetHeight + gap) + "px";
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
  const headerVal = (document.getElementById("query") || {}).value;
  const homeVal = (document.getElementById("contentQuery") || {}).value;
  const urlParams = new URLSearchParams(location.search);
  const urlQ = urlParams.get("q") || "";
  const q = (headerVal || homeVal || urlQ || "").trim();
  if (!q) {
    document.querySelectorAll('a[href^="/search/advance"]').forEach(a => {
      a.setAttribute("href", "/search/advance");
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

  const href = "/search/advance?" + advParams.toString();
  document.querySelectorAll('a[href^="/search/advance"]').forEach(a => {
    a.setAttribute("href", href);
  });
}

/** Wire input/route hooks so the Advanced links stay in sync with the query. */
function attachAdvanceLinkSync() {
  for (const id of ["query", "contentQuery"]) {
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
      // JSP parity (index.jsp): home view is re-rendered on every request in the
      // default theme, so all search state is gone.  Replicate that by doing a full
      // silent reset — clears module state, option selects, and both query inputs.
      search.clearSearchState();
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

  // Advanced search view. Accept both the SPA path (/advance) and the
  // canonical Fess URL (/search/advance) so direct links / bookmarks work.
  router.register(
    path => path === "/advance" || path === "/search/advance",
    () => {
      setChatNavSearchMode(false);
      setSearchFormVisible(false);
      showView("advance-view");
      advance.attach();
    }
  );

  // Help view. JSP parity: help.jsp includes header.jsp, which shows the
  // header search box — so keep it visible here (unlike home/advance/profile).
  router.register(
    path => path === "/help",
    () => {
      setChatNavSearchMode(false);
      setSearchFormVisible(true);
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
  // JSP parity: error/*.jsp include header.jsp, which shows the header search box.
  router.register(
    path => path === "/error" || path.startsWith("/error/"),
    () => {
      setChatNavSearchMode(false);
      setSearchFormVisible(true);
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
  // JSP parity: searchResults.jsp has NO inline chat sidebar — chat lives only on the
  // standalone /chat page (chat.attachStandalone, wired in the /chat route). Mounting
  // the inline panel here wrongly showed a chat column on the results page, so it is
  // intentionally not called. (#chat-column stays d-none as defined in index.html.)
  // After login, refresh results without re-attaching event listeners.
  // search.attach() is idempotent but search.refresh() is semantically cleaner.
  document.addEventListener("fess:auth:login", () => search.refresh());

  // Close the search-options drawer on client-side navigation. The JSP dismisses it
  // via a full page reload; in the SPA the Bootstrap collapse would otherwise stay
  // open across view changes (e.g. after submitting a search or opening /search/advance).
  document.addEventListener("fess:route:change", () => {
    const so = document.getElementById("searchOptions");
    if (so && window.bootstrap && window.bootstrap.Collapse) {
      // {toggle:false} is REQUIRED: Bootstrap Collapse defaults to toggle:true, so
      // getOrCreateInstance() would OPEN a freshly-seen (closed) drawer on creation.
      // With toggle:false, hide() just closes it if open (no-op when already closed).
      window.bootstrap.Collapse.getOrCreateInstance(so, { toggle: false }).hide();
    }
  });

  // Wire back-to-top button.
  attachBackToTop();

  // GAP A: sync body offset to live header height; keep in sync on resize.
  syncHeaderOffset();
  window.addEventListener("resize", syncHeaderOffset);
  if (window.ResizeObserver) {
    const header = document.querySelector(".navbar.fixed-top");
    if (header) new ResizeObserver(syncHeaderOffset).observe(header);
  }

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
