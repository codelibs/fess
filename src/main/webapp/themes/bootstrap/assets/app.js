import * as api from "./api.js";
import * as i18n from "./i18n.js";
import * as auth from "./auth.js";
import * as search from "./search.js";
import * as chat from "./chat.js";
import * as router from "./router.js";
import * as errorView from "./error.js";

/** Show one SPA view section and hide the rest. */
function showView(id) {
  const viewIds = ["results-view", "error-view", "profile-view", "help-view"];
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

function registerRoutes() {
  // Search / home routes — show results view and run search.
  router.register(
    path => path === "/" || path === "/search" || path === "/index" || path === "/index.html",
    () => {
      setSearchFormVisible(true);
      showView("results-view");
      // search.attach() is idempotent; safe to call on each navigation.
      search.attach();
    }
  );

  // Profile view placeholder.
  router.register(
    path => path === "/profile",
    () => {
      setSearchFormVisible(false);
      showView("profile-view");
    }
  );

  // Help view placeholder.
  router.register(
    path => path === "/help",
    () => {
      setSearchFormVisible(false);
      showView("help-view");
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
  await auth.attach();
  search.attach();
  chat.attach(); // no-op when rag_chat_enabled is false
  // After login, refresh results without re-attaching event listeners.
  // search.attach() is idempotent but search.refresh() is semantically cleaner.
  document.addEventListener("fess:auth:login", () => search.refresh());

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
