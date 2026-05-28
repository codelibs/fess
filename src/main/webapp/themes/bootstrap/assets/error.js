// SPDX-License-Identifier: Apache-2.0
// Error page renderer for the Fess bootstrap SPA.
// All DOM construction uses createElement/textContent — no innerHTML.

import { t } from "./i18n.js";

/**
 * Maps URL path segments to HTTP-like status codes.
 * Lowercase comparison is applied before lookup.
 */
const PATH_TO_CODE = {
  "400": "400",
  "bad_request": "400",
  "badrequest": "400",
  "404": "404",
  "not_found": "404",
  "notfound": "404",
  "500": "500",
  "internal_server_error": "500",
  "internalservererror": "500",
  "system": "500",
  "error": "500",
  "503": "503",
  "service_unavailable": "503",
  "serviceunavailable": "503",
  "busy": "429"
};

/**
 * Derive an error code from the current pathname.
 * Checks each segment (in reverse order) against PATH_TO_CODE.
 * camelCase segments (e.g. "notFound", "badRequest") are normalised to
 * all-lowercase before the lookup so both snake_case and camelCase paths match.
 * Returns "404" as the default when no segment matches.
 *
 * @param {string} pathname - e.g. "/error/404", "/error/not_found", "/error/notFound"
 * @returns {string} - one of "400", "404", "500", "503"
 */
function codeFromPath(pathname) {
  const segments = pathname.split("/").filter(Boolean);
  for (let i = segments.length - 1; i >= 0; i--) {
    // Normalise: lowercase and remove underscores so snake_case and camelCase
    // collapse to the same key (e.g. "notFound" → "notfound", "not_found" → "notfound").
    const seg = segments[i].toLowerCase().replace(/_/g, "");
    if (PATH_TO_CODE[seg]) return PATH_TO_CODE[seg];
    // Also try the raw lowercase (for plain numeric keys like "400").
    const raw = segments[i].toLowerCase();
    if (PATH_TO_CODE[raw]) return PATH_TO_CODE[raw];
  }
  return "404";
}

/**
 * Build a safe <dt>/<dd> definition-list entry using textContent only.
 *
 * @param {HTMLElement} dl
 * @param {string} label
 * @param {string} value
 */
function appendDtDd(dl, label, value) {
  const dt = document.createElement("dt");
  dt.textContent = label;
  const dd = document.createElement("dd");
  dd.textContent = value;
  dl.appendChild(dt);
  dl.appendChild(dd);
}

/**
 * Read the error detail key injected by ThemeViewAction (F.9).
 * Returns the message key string (e.g. "errors.docid_not_found") or null.
 *
 * @returns {string|null}
 */
function readErrorDetailKey() {
  const meta = document.querySelector('meta[name="x-fess-error-detail-key"]');
  return meta ? meta.getAttribute("content") : null;
}

/**
 * Render the error page into the given container element.
 * Clears any previous content first.
 *
 * @param {HTMLElement} container
 * @param {string} code - "400" | "404" | "500" | "503"
 * @param {string|null} requestedUrl - optional URL that was requested
 * @param {string|null} errorDetailKey - optional i18n key for additional detail (F.9)
 */
function render(container, code, requestedUrl, errorDetailKey) {
  // Clear previous content without innerHTML assignment.
  while (container.firstChild) container.removeChild(container.firstChild);

  // Title
  const h2 = document.createElement("h2");
  h2.className = "error-title";
  h2.textContent = t("error.title_" + code);
  container.appendChild(h2);

  // Body / description
  const p = document.createElement("p");
  p.className = "error-body";
  p.textContent = t("error.body_" + code);
  container.appendChild(p);

  // F.9: Additional error detail from server-injected meta tag.
  if (errorDetailKey) {
    // Convert the server-side key (e.g. "errors.docid_not_found") to the
    // client-side i18n key convention (e.g. "error.detail_docid_not_found").
    const clientKey = "error.detail_" + errorDetailKey.replace(/^errors\./, "");
    const detailText = t(clientKey);
    // Only render if the i18n bundle has an entry; t() returns the key itself as
    // fallback — skip generic fallback keys to avoid showing raw key strings.
    if (detailText && detailText !== clientKey) {
      const detail = document.createElement("p");
      detail.className = "error-detail-additional text-muted";
      detail.textContent = detailText;
      container.appendChild(detail);
    }
  }

  // Requested URL block (optional)
  if (requestedUrl) {
    const detail = document.createElement("div");
    detail.className = "error-detail";
    const dl = document.createElement("dl");
    dl.className = "mb-0";
    appendDtDd(dl, t("error.requested_url"), requestedUrl);
    detail.appendChild(dl);
    container.appendChild(detail);
  }

  // Actions
  const actions = document.createElement("div");
  actions.className = "error-actions";

  const homeLink = document.createElement("a");
  homeLink.href = "/";
  homeLink.setAttribute("data-spa", "");
  homeLink.className = "btn btn-primary me-2";
  homeLink.textContent = t("error.go_home");
  actions.appendChild(homeLink);

  container.appendChild(actions);

  // Admin contact message
  const contact = document.createElement("p");
  contact.className = "text-muted small";
  contact.textContent = t("error.contact_admin");
  container.appendChild(contact);
}

/**
 * Attach the error view to the #error-view container.
 * Reads the current pathname and optional ?url search parameter.
 * Also reads the x-fess-error-detail-key meta tag injected by ThemeViewAction (F.9).
 * Safe to call multiple times — re-renders from scratch each time.
 */
export function attach() {
  const container = document.getElementById("error-view");
  if (!container) return;

  const code = codeFromPath(location.pathname);

  // Read ?url parameter safely via URL API — never concatenate into markup.
  let requestedUrl = null;
  try {
    requestedUrl = new URL(location.href).searchParams.get("url");
  } catch { /* ignore — URL constructor should never throw for location.href */ }

  // F.9: Read server-injected error detail key from <meta> tag in document head.
  const errorDetailKey = readErrorDetailKey();

  render(container, code, requestedUrl, errorDetailKey);
}
