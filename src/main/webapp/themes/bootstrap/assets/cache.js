// SPDX-License-Identifier: Apache-2.0
// SPA cache viewer for the Fess bootstrap theme.
// Fetches cached content from /api/v2/cache/{docId} and renders it inside a
// sandboxed iframe (no allow-same-origin, no allow-scripts) so that arbitrary
// crawled HTML is fully isolated from the SPA shell.

import * as api from "./api.js";
import { t } from "./i18n.js";
import { formatDate } from "./format.js";

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
 * Attributes whose value is a single URL. `data` is only a URL on <object>.
 */
const URL_ATTRS = ["href", "src", "poster", "action", "formaction", "background"];

/**
 * Resolve a URL reference against {@code base}. Returns the value unchanged when
 * there is nothing to resolve: empty, a fragment-only reference (an anchor inside
 * the cached copy), an absolute URL (any scheme) or a value that cannot be
 * resolved against {@code base}.
 *
 * @param {string} value
 * @param {string|null} base - absolute URL of the original page
 * @returns {string}
 */
function resolveUrl(value, base) {
  const v = value.trim();
  if (v === "" || v.startsWith("#") || /^[a-z][a-z0-9+.-]*:/i.test(v)) return value;
  try {
    return new URL(v, base).href;
  } catch (e) {
    return value;
  }
}

/** Resolve the url(...) references of a CSS text. */
function resolveCssUrls(css, base) {
  return css.replace(/url\(\s*(["']?)([^"')]*?)\1\s*\)/gi,
    (m, q, u) => 'url("' + resolveUrl(u, base) + '")');
}

/**
 * Resolve the URL of each candidate of a srcset value, keeping the descriptors.
 * A URL that ends with a comma is a candidate without descriptors.
 */
function resolveSrcset(srcset, base) {
  return srcset.replace(/([^\s,]\S*)([^,]*)/g, (m, u, descriptor) => {
    const commas = u.match(/,*$/)[0];
    const url = resolveUrl(u.slice(0, u.length - commas.length), base);
    return url + commas + (commas ? resolveSrcset(descriptor, base) : descriptor);
  });
}

/** A <base> start tag, attribute values included. */
const BASE_TAG = /<base\b(?:"[^"]*"|'[^']*'|[^>"'])*>/gi;

/**
 * Rewrite the relative URLs of a cached HTML document so that they point at the
 * original site.
 *
 * The cached document starts with <base href="original address"> (cache.hbs),
 * but it is shown in a blob: frame that inherits the page's Content-Security-
 * Policy, whose base-uri 'self' makes the browser ignore that element, so the
 * relative links, images and styles of the cached page resolve to nothing. They
 * are resolved here instead, against the address the server put in that element
 * (the first <base> wins in a browser, as it did when the cache was a page of
 * its own). <base> elements are dropped because they can no longer take effect
 * and the browser reports each of them as a policy violation. The policy is not
 * relaxed, and the frame stays sandboxed without scripts.
 *
 * The document is parsed with DOMParser, which neither runs scripts nor loads
 * resources, and serialised again; nothing is inserted into the viewer's DOM.
 * A <base> must not reach that parser either: it is subject to the same policy.
 *
 * @param {string} html - cached document
 * @param {string|null} pageUrl - address of the original page
 * @returns {string} html with absolute URLs; unchanged without a page address
 */
function resolveRelativeUrls(html, pageUrl) {
  if (!pageUrl) return html;
  const doc = new DOMParser().parseFromString(html.replace(BASE_TAG, ""), "text/html");
  doc.querySelectorAll("*").forEach(el => {
    const attrs = el.localName === "object" ? URL_ATTRS.concat("data") : URL_ATTRS;
    attrs.forEach(name => {
      const value = el.getAttribute(name);
      if (value !== null) el.setAttribute(name, resolveUrl(value, pageUrl));
    });
    const srcset = el.getAttribute("srcset");
    if (srcset !== null) el.setAttribute("srcset", resolveSrcset(srcset, pageUrl));
    const style = el.getAttribute("style");
    if (style !== null && /url\(/i.test(style)) el.setAttribute("style", resolveCssUrls(style, pageUrl));
    if (el.localName === "style" && /url\(/i.test(el.textContent)) el.textContent = resolveCssUrls(el.textContent, pageUrl);
  });
  return (doc.doctype ? "<!DOCTYPE " + doc.doctype.name + ">" : "") + doc.documentElement.outerHTML;
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
    : "search";

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

  // Parse hq terms from the current route URL so they can be forwarded to the
  // cache API.  The search link encodes them as repeated &hq= params
  // (state.highlightParams from the search response, or a &hq=<q> fallback).
  // CacheHandler reads req.getParameterValues("hq") and passes the array to
  // ViewHelper.createCacheContent for in-page term highlighting.  [marker: cache-hq-forward]
  const hqValues = params.getAll("hq").filter(v => v !== "");
  const cacheApiParams = hqValues.length > 0 ? { hq: hqValues } : undefined;

  api.get("/cache/" + encodeURIComponent(docId), cacheApiParams)
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
      // Format the raw stored date string for display. formatDate returns ""
      // for unparseable values, in which case we fall back to the raw string.
      // [marker: cache-created-formatdate]
      const rawCreated = env.created || null;
      const cacheCreated = rawCreated ? (formatDate(rawCreated) || rawCreated) : null;

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

      // NOTE: the SPA banner ("This is a cached copy of…") is intentionally
      // suppressed here.  The API response `content` already contains a banner
      // rendered by cache.hbs ({{cache_msg}} / {{{hl_cache}}}).  Rendering a
      // second banner in the SPA shell would show the notice twice.  The metadata
      // block below (URL / mimetype / doc-id) and the back-to-results link are
      // SPA-only additions that do not duplicate anything in the iframe content.
      // The i18n key "labels.search_cache_msg" is intentionally left in the
      // bundle unused (removing it would break LabelMessageThemeParityTest).
      // [marker: cache-banner-suppressed]

      // --- Metadata block ---
      const dl = document.createElement("dl");
      dl.className = "row cache-meta mb-3";

      // URL (from the document metadata returned by the API).
      if (cacheUrl) {
        appendMeta(dl, "labels.cache_url", cacheUrl);
      }
      appendMeta(dl, "labels.cache_mimetype", mimetype);
      appendMeta(dl, "labels.cache_doc_id", docIdVal);
      if (cacheCreated) {
        appendMeta(dl, "labels.cache_indexed_at", cacheCreated);
      }

      if (dl.children.length > 0) {
        host.appendChild(dl);
      }

      // --- Relative URLs ---
      // The blob: frame below ignores <base href> (see resolveRelativeUrls), so the
      // cached document's relative links, images and styles are resolved up front.
      const docHtml = resolveRelativeUrls(content, cacheUrl);

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
      const isNotFound = err && (err.code === "not_found" || err.code === "NOT_FOUND" || err.httpStatus === 404);
      const msg = isNotFound
        ? t("labels.cache_not_found")
        : t("error.server");
      renderError(host, msg, backHref);
    });
}
