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
 * Fetch popular words from /api/v2/popular-words and render them in #home-popular-words.
 */
async function renderHomePopularWords() {
  const host = document.getElementById("home-popular-words");
  if (!host) return;
  try {
    const data = await api.get("/popular-words");
    const words = data?.popular_words || data?.words || [];
    while (host.firstChild) host.removeChild(host.firstChild);
    if (words.length === 0) return;
    const label = document.createElement("span");
    label.className = "me-2";
    label.textContent = t("search.popular_searches") + ":";
    host.appendChild(label);
    words.slice(0, 5).forEach((w, i) => {
      if (i > 0) host.appendChild(document.createTextNode(" "));
      const a = document.createElement("a");
      a.href = "/search?q=" + encodeURIComponent(w);
      a.setAttribute("data-spa", "");
      a.textContent = w;
      host.appendChild(a);
    });
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
  const homeInput = document.getElementById("home-search-input");
  if (homeInput) { Promise.resolve().then(() => homeInput.focus()); }
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
  chatLink.setAttribute("data-i18n", "nav.chat");
  chatLink.textContent = t("nav.chat");
  helpLink.parentNode.insertBefore(chatLink, helpLink);
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

function registerRoutes() {
  // Home route — "/" with no q= parameter shows the centered home view.
  router.register(
    path => (path === "/" || path === "/index" || path === "/index.html") && !hasSearchQuery(),
    () => {
      setSearchFormVisible(false);
      showView("home-view");
      attachHomeView();
    }
  );

  // Search / results routes — show results view and run search.
  router.register(
    path => path === "/" || path === "/search" || path === "/index" || path === "/index.html",
    () => {
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
      setSearchFormVisible(false);
      showView("profile-view");
      profile.attach();
    }
  );

  // Advanced search view.
  router.register(
    path => path === "/advance",
    () => {
      setSearchFormVisible(false);
      showView("advance-view");
      advance.attach();
    }
  );

  // Help view.
  router.register(
    path => path === "/help",
    () => {
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
      setSearchFormVisible(false);
      showView("cache-view");
      cache.attach();
    }
  );

  // Error routes — /error, /error/*, /error/400, /error/404, etc.
  router.register(
    path => path === "/error" || path.startsWith("/error/"),
    () => {
      setSearchFormVisible(false);
      showView("error-view");
      errorView.attach();
    }
  );

  // Fallback: unknown paths show error view with code 404.
  router.register(
    () => true,
    () => {
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
