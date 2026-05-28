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

/** Show one SPA view section and hide the rest. */
function showView(id) {
  const viewIds = ["home-view", "results-view", "advance-view", "error-view", "profile-view", "help-view"];
  for (const vid of viewIds) {
    const el = document.getElementById(vid);
    if (!el) continue;
    if (vid === id) {
      el.removeAttribute("hidden");
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
 */
function renderWarnings() {
  const host = document.getElementById("warning-indicators");
  if (!host) return;
  while (host.firstChild) host.removeChild(host.firstChild);
  const features = api.getConfig()?.features || {};

  if (features.eoled) {
    const span = document.createElement("span");
    span.className = "text-warning ms-2";
    span.title = t("nav.eol_warning");
    span.setAttribute("aria-label", t("nav.eol_warning"));
    const icon = document.createElement("i");
    icon.className = "fa fa-exclamation-triangle";
    icon.setAttribute("aria-hidden", "true");
    span.appendChild(icon);
    host.appendChild(span);
  }

  if (features.development_mode) {
    const span = document.createElement("span");
    span.className = "text-info ms-2";
    span.title = t("nav.dev_mode_warning");
    span.setAttribute("aria-label", t("nav.dev_mode_warning"));
    const icon = document.createElement("i");
    icon.className = "fa fa-flask";
    icon.setAttribute("aria-hidden", "true");
    span.appendChild(icon);
    host.appendChild(span);
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
        router.navigate("/search?q=" + encodeURIComponent(q));
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
  renderHomePopularWords();
}

/** Attach back-to-top button behaviour. */
function attachBackToTop() {
  const btn = document.getElementById("back-to-top");
  if (!btn) return;
  window.addEventListener("scroll", () => {
    btn.style.display = window.scrollY > 200 ? "block" : "none";
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
      // search.attach() is idempotent; safe to call on each navigation.
      search.attach();
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
