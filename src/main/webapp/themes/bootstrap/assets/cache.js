// SPDX-License-Identifier: Apache-2.0
// SPA cache viewer for the Fess bootstrap theme.
// Fetches cached content from /api/v2/cache/{docId} and renders it inside a
// sandboxed iframe (no allow-same-origin, no allow-scripts) so that arbitrary
// crawled HTML is fully isolated from the SPA shell.

import * as api from "./api.js";
import { t } from "./i18n.js";

/** Currently active blob URL — revoked on route change and after iframe load. */
let _currentBlobUrl = null;

/**
 * Revoke the current blob URL if one is pending.
 * Called on fess:route:change and after a 60-second grace period post-load.
 */
function revokeCurrent() {
  if (_currentBlobUrl) {
    URL.revokeObjectURL(_currentBlobUrl);
    _currentBlobUrl = null;
  }
}

/** Listen once for route changes and revoke any lingering blob URL. */
let _routeListenerAttached = false;
function attachRouteListener() {
  if (_routeListenerAttached) return;
  _routeListenerAttached = true;
  document.addEventListener("fess:route:change", () => revokeCurrent());
}

/**
 * Render an error state inside the cache-view section.
 *
 * @param {HTMLElement} host - the #cache-view section
 * @param {string} message - user-facing message (plain text)
 * @param {string|null} backHref - optional href for back-to-results link
 */
function renderError(host, message, backHref) {
  while (host.firstChild) host.removeChild(host.firstChild);

  if (backHref) {
    const back = document.createElement("a");
    back.href = backHref;
    back.setAttribute("data-spa", "");
    back.className = "btn btn-outline-secondary btn-sm mb-3";
    back.textContent = t("labels.cache_back_to_results");
    host.appendChild(back);
  }

  const alert = document.createElement("div");
  alert.className = "alert alert-warning";
  alert.setAttribute("role", "status");
  alert.textContent = message;
  host.appendChild(alert);
}

/**
 * Build a metadata <dl> row.
 *
 * @param {HTMLElement} dl
 * @param {string} labelKey - i18n key
 * @param {string} value - plain text value
 */
function appendMeta(dl, labelKey, value) {
  if (!value) return;
  const dt = document.createElement("dt");
  dt.className = "col-sm-3";
  dt.textContent = t(labelKey);
  const dd = document.createElement("dd");
  dd.className = "col-sm-9";
  dd.textContent = value;
  dl.appendChild(dt);
  dl.appendChild(dd);
}

/**
 * Render the cache viewer for the current URL parameters.
 * Reads docId (required) and queryId (optional) from location.search.
 *
 * Called by app.js route handler each time the /cache/* path is dispatched.
 */
export function attach() {
  attachRouteListener();

  const host = document.getElementById("cache-view");
  if (!host) return;

  // Revoke any blob URL left from a previous visit to this view.
  revokeCurrent();

  const params = new URLSearchParams(location.search);
  const docId = params.get("docId");

  // Build the back-to-results href from history state or referrer heuristic.
  // Use a simple fallback to /search if nothing is available.
  const backHref = document.referrer
    ? new URL(document.referrer).pathname + new URL(document.referrer).search
    : "/search";

  if (!docId) {
    renderError(host, t("labels.cache_not_found"), backHref);
    return;
  }

  // Show loading indicator while fetching.
  while (host.firstChild) host.removeChild(host.firstChild);

  const backLink = document.createElement("a");
  backLink.href = backHref;
  backLink.setAttribute("data-spa", "");
  backLink.className = "btn btn-outline-secondary btn-sm mb-3";
  backLink.textContent = t("labels.cache_back_to_results");
  host.appendChild(backLink);

  const loading = document.createElement("p");
  loading.className = "text-muted";
  loading.setAttribute("aria-live", "polite");
  loading.textContent = t("labels.cache_loading");
  host.appendChild(loading);

  api.get("/cache/" + encodeURIComponent(docId))
    .then(env => {
      // CacheHandler returns { doc_id, mimetype, content, url, created, charset }
      // inside the v2 envelope (url/created/charset added in Phase A).
      const docIdVal = env.doc_id || docId;
      const baseMime = env.mimetype || "text/html";
      const charset = env.charset || "utf-8";
      // Build a charset-aware MIME type for the Blob so the browser decodes the
      // cached document correctly even when the server omits a charset parameter.
      const mimetype = /charset=/i.test(baseMime) ? baseMime : baseMime + ";charset=" + charset;
      const content = env.content || "";
      const cacheUrl = env.url || null;
      const cacheCreated = env.created || env.last_modified || null;

      // Remove loading indicator.
      while (host.firstChild) host.removeChild(host.firstChild);

      // --- Back link ---
      const back = document.createElement("a");
      back.href = backHref;
      back.setAttribute("data-spa", "");
      back.className = "btn btn-outline-secondary btn-sm mb-3";
      back.textContent = t("labels.cache_back_to_results");
      host.appendChild(back);

      // --- Heading ---
      const h2 = document.createElement("h2");
      h2.textContent = t("labels.cache_title");
      host.appendChild(h2);

      // --- Cache banner (out-of-iframe, safe textContent — no innerHTML) ---
      // Shows "This is a cache of <url>. It is a snapshot of the page as it appeared on <created>."
      const banner = document.createElement("div");
      banner.className = "alert alert-secondary cache-banner";
      banner.setAttribute("role", "status");
      const unknown = t("labels.search_unknown");
      banner.textContent = t("labels.search_cache_msg", [cacheUrl || unknown, cacheCreated || unknown]);
      host.appendChild(banner);

      // --- Metadata block ---
      const dl = document.createElement("dl");
      dl.className = "row cache-meta mb-3";

      // URL (from the document metadata returned by the API).
      if (cacheUrl) {
        appendMeta(dl, "labels.cache_url", cacheUrl);
      }
      if (env.last_modified) {
        appendMeta(dl, "labels.cache_indexed_at", env.last_modified);
      }
      appendMeta(dl, "labels.cache_mimetype", mimetype);
      appendMeta(dl, "labels.cache_doc_id", docIdVal);

      if (dl.children.length > 0) {
        host.appendChild(dl);
      }

      // --- <base href> injection ---
      // Inject a <base href> into the cached HTML so that relative URLs in the
      // cached document resolve against the original page origin.  This is done
      // only when a URL is available and the cached document has no existing
      // <base> element.  The URL is HTML-escaped for attribute context (XSS safe).
      let docHtml = content;
      if (cacheUrl && !/<base\b/i.test(docHtml)) {
        const safeBase = cacheUrl
          .replace(/&/g, "&amp;")
          .replace(/"/g, "&quot;")
          .replace(/</g, "&lt;")
          .replace(/>/g, "&gt;");
        const baseTag = '<base href="' + safeBase + '">';
        docHtml = /<head\b[^>]*>/i.test(docHtml)
          ? docHtml.replace(/<head\b[^>]*>/i, m => m + baseTag)
          : baseTag + docHtml;
      }

      // --- Sandboxed iframe for cached content ---
      // SECURITY: cached HTML is untrusted, arbitrary content. We MUST use a
      // sandboxed iframe without allow-same-origin and without allow-scripts.
      // Never inject content into the SPA DOM directly.
      const blob = new Blob([docHtml], { type: mimetype });
      const blobUrl = URL.createObjectURL(blob);
      _currentBlobUrl = blobUrl;

      const iframe = document.createElement("iframe");
      iframe.src = blobUrl;
      // No allow-same-origin — prevents the iframe document from accessing the
      // parent origin. No allow-scripts — prevents any JS in cached HTML from
      // running. allow-popups lets links open in a new tab; allow-popups-to-
      // escape-sandbox ensures the opened tab is not sandboxed.
      iframe.sandbox = "allow-popups allow-popups-to-escape-sandbox";
      iframe.className = "cache-frame";
      iframe.title = t("labels.search_result_cache");
      iframe.setAttribute("aria-label", t("labels.cache_title"));

      iframe.addEventListener("load", () => {
        // Revoke after a 60-second grace period to allow the iframe to finish
        // rendering. Early revocation can cause blank frames on slow machines.
        setTimeout(() => {
          if (_currentBlobUrl === blobUrl) {
            revokeCurrent();
          } else {
            // A newer blob URL has been created; revoke this one immediately.
            URL.revokeObjectURL(blobUrl);
          }
        }, 60000);
      });

      host.appendChild(iframe);
    })
    .catch(err => {
      const isNotFound = err && (err.code === "NOT_FOUND" || err.httpStatus === 404);
      const msg = isNotFound
        ? t("labels.cache_not_found")
        : t("error.server");
      renderError(host, msg, backHref);
    });
}
