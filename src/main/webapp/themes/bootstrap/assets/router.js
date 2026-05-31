// SPDX-License-Identifier: Apache-2.0
// Predicate-driven client-side router for the Fess bootstrap SPA.
// Supports pushState / popstate navigation and intercepts data-spa anchors.

/** @type {Array<{predicate: (path: string) => boolean, handler: (path: string) => void}>} */
const routes = [];

/**
 * Register a route.
 *
 * @param {(path: string) => boolean} predicate - Returns true when this handler should handle the given path.
 * @param {(path: string) => void} handler - Called when the predicate matches.
 */
export function register(predicate, handler) {
  routes.push({ predicate, handler });
}

/**
 * Navigate to a new path using pushState (or replaceState when opts.replace is true).
 * Dispatches the matching handler immediately.
 *
 * @param {string} path
 * @param {{ replace?: boolean }} [opts]
 */
export function navigate(path, opts = {}) {
  if (opts.replace) {
    history.replaceState(null, "", path);
  } else {
    history.pushState(null, "", path);
  }
  dispatch();
}

/**
 * Inspect location.pathname and run the first matching route handler.
 * If no route matches, the last registered route (fallback) is used when present.
 * Trailing slashes are normalised away before predicate evaluation so that
 * "/search/" and "/search" both match the same route.
 */
export function dispatch() {
  // A.7: strip trailing slashes; fall back to "/" when the pathname is empty.
  const path = (location.pathname.replace(/\/+$/, "") || "/");
  // A.9: notify modules of the route change before running any handler.
  document.dispatchEvent(new CustomEvent("fess:route:change", { detail: { path } }));
  for (const route of routes) {
    if (route.predicate(path)) {
      route.handler(path);
      return;
    }
  }
}

/**
 * Register global event listeners:
 *   - popstate: re-dispatch on browser back/forward.
 *   - click: intercept <a data-spa> anchors and navigate via pushState.
 *
 * Safe to call multiple times — guards against duplicate registration.
 */
let _attached = false;
export function attach() {
  if (_attached) return;
  _attached = true;

  window.addEventListener("popstate", () => dispatch());

  document.addEventListener("click", ev => {
    const anchor = ev.target.closest("a[data-spa]");
    if (!anchor) return;
    const href = anchor.getAttribute("href");
    if (!href || href.startsWith("http://") || href.startsWith("https://") || href.startsWith("//")) return;
    ev.preventDefault();
    navigate(href);
  });
}
