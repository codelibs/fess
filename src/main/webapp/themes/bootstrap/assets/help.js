// SPDX-License-Identifier: Apache-2.0
// Help page renderer for the Fess bootstrap SPA.
// Section titles use textContent; section HTML is sanitized through a
// whitelist sanitizer before being appended — no raw innerHTML on live DOM nodes.

import { t, getLocale } from "./i18n.js";

// ---------------------------------------------------------------------------
// Whitelist sanitizer
// ---------------------------------------------------------------------------

/** Tags whose content is kept (children are recursively sanitized). */
const ALLOWED_TAGS = new Set([
  "P", "BR", "UL", "OL", "LI",
  "TABLE", "THEAD", "TBODY", "TR", "TH", "TD",
  "CODE", "PRE",
  "STRONG", "EM",
  "H2", "H3", "H4",
  "A",
  "DIV", "SPAN", "BLOCKQUOTE"
]);

/**
 * Allowed attributes per tag (lowercase tag name → Set of lowercase attr names).
 * Attributes not listed here are stripped.
 */
const ALLOWED_ATTRS = {
  a:     new Set(["href"]),
  table: new Set(["class"]),
  th:    new Set(["scope"]),
  td:    new Set(["colspan", "rowspan"])
};

/**
 * Schemes allowed in <a href>.
 * javascript: and data: are rejected; relative URLs and http(s) are permitted.
 */
function isSafeHref(value) {
  const v = value.trim().toLowerCase();
  // Reject javascript: and data: regardless of case or leading whitespace.
  if (/^javascript\s*:/i.test(v)) return false;
  if (/^data\s*:/i.test(v)) return false;
  return true;
}

/**
 * Recursively sanitize a single DOM node in-place.
 * Returns the node to keep, or null if the node should be removed entirely.
 *
 * @param {Node} node
 * @returns {Node|null}
 */
function sanitizeNode(node) {
  if (node.nodeType === Node.TEXT_NODE) return node;

  if (node.nodeType === Node.ELEMENT_NODE) {
    const tag = node.tagName.toUpperCase();

    if (!ALLOWED_TAGS.has(tag)) {
      // Disallowed tag: unwrap — keep its sanitized children in a fragment.
      const frag = document.createDocumentFragment();
      // Snapshot childNodes because we mutate during iteration.
      for (const child of Array.from(node.childNodes)) {
        const kept = sanitizeNode(child);
        if (kept) frag.appendChild(kept);
      }
      return frag.childNodes.length ? frag : null;
    }

    // Allowed tag: strip disallowed attributes.
    const allowed = ALLOWED_ATTRS[tag.toLowerCase()] || new Set();
    for (const attr of Array.from(node.attributes)) {
      const name = attr.name.toLowerCase();
      if (!allowed.has(name)) {
        node.removeAttribute(attr.name);
        continue;
      }
      // Special handling for <a href>.
      if (tag === "A" && name === "href") {
        if (!isSafeHref(attr.value)) {
          node.removeAttribute("href");
        } else if (/^https?:\/\//i.test(attr.value.trim())) {
          // External link: open in new tab safely.
          node.setAttribute("target", "_blank");
          node.setAttribute("rel", "noopener noreferrer");
        }
      }
    }

    // Recursively sanitize children.
    for (const child of Array.from(node.childNodes)) {
      const kept = sanitizeNode(child);
      if (kept !== child) {
        if (kept) {
          node.replaceChild(kept, child);
        } else {
          node.removeChild(child);
        }
      }
    }

    return node;
  }

  // Comment, ProcessingInstruction, etc. — remove.
  return null;
}

/**
 * Parse an HTML string and return a sanitized DocumentFragment safe to
 * appendChild into the live DOM.
 *
 * Parsing uses a <template> element so scripts in the source string are
 * never executed (the template content is an inert DocumentFragment per
 * the HTML spec). This is the OWASP-recommended parse-then-sanitize pattern.
 *
 * @param {string} html - Untrusted HTML string.
 * @returns {DocumentFragment}
 */
function sanitizeHtml(html) {
  const tpl = document.createElement("template");
  // This is the single intentional innerHTML assignment in the module.
  // It is safe because <template> content is inert: scripts are not executed
  // and the fragment is not yet part of the live document.
  tpl.innerHTML = html; // eslint-disable-line no-unsanitized/property

  const frag = tpl.content;
  for (const child of Array.from(frag.childNodes)) {
    const kept = sanitizeNode(child);
    if (kept !== child) {
      if (kept) {
        frag.replaceChild(kept, child);
      } else {
        frag.removeChild(child);
      }
    }
  }
  return frag;
}

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

  // Page heading.
  const h1 = document.createElement("h1");
  h1.textContent = t("help.title");
  container.appendChild(h1);

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
}
