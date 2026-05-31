// SPDX-License-Identifier: Apache-2.0
// Help page renderer for the Fess bootstrap SPA.
// Section titles use textContent; section HTML is sanitized through a
// whitelist sanitizer before being appended — no raw innerHTML on live DOM nodes.

import { t, getLocale } from "./i18n.js";
import { sanitizeHtml } from "./format.js";

// ---------------------------------------------------------------------------
// Help page renderer
// ---------------------------------------------------------------------------

/**
 * Fetch the help content JSON for the given locale.
 * Falls back to English on any failure.
 *
 * @param {string} locale - e.g. "en" or "ja"
 * @returns {Promise<{sections: Array<{id:string, title:string, html:string}>}>}
 */
async function fetchHelpContent(locale) {
  const url = `/themes/bootstrap/help/${locale}.json`;
  try {
    const r = await fetch(url, { credentials: "same-origin" });
    if (!r.ok) throw new Error(`help/${locale}.json HTTP ${r.status}`);
    return await r.json();
  } catch (e) {
    if (locale !== "en") {
      // Fallback to English.
      const r2 = await fetch("/themes/bootstrap/help/en.json", { credentials: "same-origin" });
      if (!r2.ok) throw new Error(`help/en.json HTTP ${r2.status}`);
      return await r2.json();
    }
    throw e;
  }
}

/**
 * Render a single help section into the container.
 *
 * @param {HTMLElement} container
 * @param {{id:string, title:string, html:string}} section
 */
function renderSection(container, section) {
  const sec = document.createElement("section");
  sec.id = `help-${section.id}`;
  sec.className = "help-section";

  const h2 = document.createElement("h2");
  // textContent — never innerHTML — for section titles.
  h2.textContent = section.title;
  sec.appendChild(h2);

  // Sanitize the HTML content before appending to the live DOM.
  sec.appendChild(sanitizeHtml(section.html));

  container.appendChild(sec);
}

/**
 * Attach the help view to the #help-view container.
 * Clears previous content and rebuilds from scratch each time.
 * Safe to call on every navigation to /help.
 */
export async function attach() {
  const container = document.getElementById("help-view");
  if (!container) return;

  // Clear previous content without innerHTML assignment.
  while (container.firstChild) container.removeChild(container.firstChild);

  // No page-level "Help" h1: the section <h2>s carry the headings (parity request);
  // showView() falls back to the first <h2> as the focus target.

  // Loading indicator (removed once content arrives).
  const loading = document.createElement("p");
  loading.className = "text-muted";
  loading.textContent = "…"; // ellipsis
  container.appendChild(loading);

  let data;
  try {
    data = await fetchHelpContent(getLocale());
  } catch (e) {
    container.removeChild(loading);
    const err = document.createElement("p");
    err.className = "text-danger";
    err.textContent = t("error.server");
    container.appendChild(err);
    return;
  }

  container.removeChild(loading);

  if (!data || !Array.isArray(data.sections)) return;

  for (const section of data.sections) {
    renderSection(container, section);
  }
  // The inline "back to top" link was removed per request; the floating
  // #back-to-top button covers this.
}
