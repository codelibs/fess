// SPDX-License-Identifier: Apache-2.0
// Predicate-driven client-side router for the Fess bootstrap SPA.
// Supports pushState / popstate navigation and intercepts data-spa anchors.
//
// Fess inserts <base href="{context path}/"> into index.html, so the theme writes
// relative URLs ("search?q=x", "./") and the router matches routes against the path
// below that base ("/search"), whatever context path Fess runs under.

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
 * URL that relative URLs resolve against: the <base href> Fess inserts, or the origin
 * root when the document has none.
 */
function rootUrl() {
  return document.querySelector("base[href]") ? document.baseURI : location.origin + "/";
}

/** Path of the application root, ending in "/" ("/" or e.g. "/fess/"). */
export function basePath() {
  return new URL(".", rootUrl()).pathname;
}

/**
 * The route path of the current location: location.pathname below basePath(), with a
 * leading "/" and no trailing slash ("/fess/search/" → "/search", "/fess" → "/").
 */
export function currentPath() {
  const base = basePath();
  let path = location.pathname;
  if (path.startsWith(base)) {
    path = "/" + path.slice(base.length);
  } else if (path + "/" === base) {
    path = "/";
  }
  return path.replace(/\/+$/, "") || "/";
}

/**
 * Navigate to a new path using pushState (or replaceState when opts.replace is true).
 * Dispatches the matching handler immediately. A relative path ("search?q=x", "./")
 * resolves against the application root.
 *
 * @param {string} path
 * @param {{ replace?: boolean }} [opts]
 */
export function navigate(path, opts = {}) {
  const url = new URL(path, rootUrl());
  const target = url.pathname + url.search + url.hash;
  if (opts.replace) {
    history.replaceState(null, "", target);
  } else {
    history.pushState(null, "", target);
  }
  dispatch();
}

/**
 * Leave the SPA with a full page load, e.g. to the SSO login page or an identity
 * provider's logout URL. A relative URL resolves against the application root.
 *
 * @param {string} url
 */
export function redirect(url) {
  location.assign(new URL(url, rootUrl()).href);
}

/**
 * Run the first route whose predicate matches currentPath(). Trailing slashes are
 * normalised away so that "/search/" and "/search" both match the same route.
 */
export function dispatch() {
  const path = currentPath();
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
 *   - click: keep fragment-only links ("#page-root") on this page, and intercept
 *     <a data-spa> anchors and navigate via pushState.
 *
 * Safe to call multiple times — guards against duplicate registration.
 */
let _attached = false;
export function attach() {
  if (_attached) return;
  _attached = true;

  window.addEventListener("popstate", () => dispatch());

  document.addEventListener("click", ev => {
    // Under <base href> a fragment-only link resolves against the application root, so
    // following it would load the home page. Stay on this page and move focus to the
    // target instead. Links that handled the click themselves, and Bootstrap toggles,
    // are left alone.
    const fragment = ev.target.closest('a[href^="#"]');
    if (fragment) {
      if (ev.defaultPrevented || fragment.hasAttribute("data-bs-toggle")) return;
      ev.preventDefault();
      const id = fragment.getAttribute("href").slice(1);
      const target = id ? document.getElementById(id) : null;
      if (target) {
        if (!target.hasAttribute("tabindex")) target.setAttribute("tabindex", "-1");
        target.focus();
        if (typeof target.scrollIntoView === "function") target.scrollIntoView();
      }
      return;
    }
    const anchor = ev.target.closest("a[data-spa]");
    if (!anchor) return;
    const href = anchor.getAttribute("href");
    if (!href || href.startsWith("http://") || href.startsWith("https://") || href.startsWith("//")) return;
    ev.preventDefault();
    navigate(href);
  });
}
