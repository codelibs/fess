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
  "404": "404",
  "not_found": "404",
  "500": "500",
  "internal_server_error": "500",
  "503": "503",
  "service_unavailable": "503"
};

/**
 * Derive an error code from the current pathname.
 * Checks each segment (in reverse order) against PATH_TO_CODE.
 * Returns "404" as the default when no segment matches.
 *
 * @param {string} pathname - e.g. "/error/404" or "/error/not_found"
 * @returns {string} - one of "400", "404", "500", "503"
 */
function codeFromPath(pathname) {
  const segments = pathname.split("/").filter(Boolean);
  for (let i = segments.length - 1; i >= 0; i--) {
    const seg = segments[i].toLowerCase();
    if (PATH_TO_CODE[seg]) return PATH_TO_CODE[seg];
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
 * Render the error page into the given container element.
 * Clears any previous content first.
 *
 * @param {HTMLElement} container
 * @param {string} code - "400" | "404" | "500" | "503"
 * @param {string|null} requestedUrl - optional URL that was requested
 */
function render(container, code, requestedUrl) {
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

  render(container, code, requestedUrl);
}
