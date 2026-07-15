// SPDX-License-Identifier: Apache-2.0
// Common formatting utilities for the Fess bootstrap SPA.
// No DOM access — pure functions, safe to import from any module.

const UNITS = ["B", "KB", "MB", "GB", "TB", "PB"];

/**
 * Format a byte count as a human-readable file size string.
 *
 * @param {number|string|null|undefined} bytes
 * @returns {string} e.g. "1.4 MB" or "" for invalid input
 */
export function formatFileSize(bytes) {
  if (bytes === null || bytes === undefined || bytes === "" || Number.isNaN(Number(bytes))) {
    return "";
  }
  let n = Number(bytes);
  if (n < 0) return "";
  let i = 0;
  while (n >= 1024 && i < UNITS.length - 1) {
    n /= 1024;
    i++;
  }
  return `${n.toFixed(i === 0 ? 0 : 1)} ${UNITS[i]}`;
}

/**
 * Format an ISO 8601 date string as "YYYY-MM-DD HH:MM" in local time.
 *
 * @param {string|null|undefined} isoString
 * @returns {string} formatted date or "" for invalid/empty input
 */
export function formatDate(isoString) {
  if (!isoString) return "";
  const d = new Date(isoString);
  if (Number.isNaN(d.getTime())) return "";
  const pad = (x) => String(x).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ` +
         `${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

/**
 * Escape HTML special characters so a plain string is safe to assign to
 * element.textContent is always preferred, but this function is provided
 * for cases where a sanitized string must be embedded in a larger context.
 *
 * @param {string|null|undefined} s
 * @returns {string}
 */
export function escapeHtml(s) {
  if (s === null || s === undefined) return "";
  return String(s)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

/**
 * Sanitize a server-supplied search snippet for safe innerHTML assignment.
 *
 * The server wraps matched terms in <strong> or <em>. This function escapes
 * all HTML first, then selectively restores only those two tag pairs.
 * All other markup (including any injected tags) remains escaped.
 *
 * The return value is safe to assign to element.innerHTML.
 *
 * @param {string|null|undefined} raw
 * @returns {string} sanitized HTML string
 */
export function renderHighlightedSnippet(raw) {
  if (!raw) return "";
  const escaped = escapeHtml(raw);
  return escaped
    .replaceAll("&lt;strong&gt;", "<strong>")
    .replaceAll("&lt;/strong&gt;", "</strong>")
    .replaceAll("&lt;em&gt;", "<em>")
    .replaceAll("&lt;/em&gt;", "</em>");
}

// ---------------------------------------------------------------------------
// Whitelist HTML sanitizer (shared with help.js and related-content rendering)
// ---------------------------------------------------------------------------

/** Tags whose content is kept (children are recursively sanitized). */
const ALLOWED_TAGS = new Set([
  "P", "BR", "UL", "OL", "LI",
  "TABLE", "THEAD", "TBODY", "TR", "TH", "TD",
  "CODE", "PRE",
  "STRONG", "EM",
  "H2", "H3", "H4",
  "A",
  "DIV", "SPAN", "BLOCKQUOTE",
  "DL", "DT", "DD",
  "HR"
]);

/**
 * Elements dropped whole, children included, rather than unwrapped like an
 * ordinary disallowed tag. For the raw-text members — SCRIPT, STYLE,
 * TEXTAREA, TITLE, XMP, PLAINTEXT, NOEMBED, NOFRAMES — and for IFRAME,
 * OBJECT, EMBED, the child "text" is the element's own source or markup,
 * not prose: unwrapping would surface that source as visible content (a
 * <script> body becomes the literal string "alert(1)"). NOSCRIPT is
 * dropped for a different reason: sanitizeHtml() parses with a <template>
 * element, which runs with the scripting flag disabled, so a <noscript>'s
 * children parse as ordinary elements, not raw text — but that content is
 * a no-JS fallback, meaningless in this JS-required SPA, so it is dropped
 * rather than unwrapped. TEMPLATE is kept mainly for foreign content:
 * inside <svg>, a <template> has real childNodes and no `.content`
 * fragment, so it would otherwise leak its text like the raw-text members
 * above; in ordinary HTML content its children live in `.content`, a
 * fragment this function never traverses, so dropping it here is usually
 * a no-op.
 */
const DROP_WITH_CONTENT = new Set([
  "SCRIPT", "STYLE", "TEXTAREA", "TITLE", "NOSCRIPT",
  "IFRAME", "OBJECT", "EMBED", "TEMPLATE", "XMP",
  "PLAINTEXT", "NOEMBED", "NOFRAMES"
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
 * Schemes permitted in a sanitized <a href>. Anything outside this allowlist
 * (javascript:, data:, vbscript:, blob:, file:, …) is rejected. Relative,
 * protocol-relative and fragment URLs resolve to the page scheme (http/https)
 * and are therefore permitted.
 */
const SAFE_HREF_SCHEMES = new Set(["http:", "https:", "mailto:", "tel:", "ftp:", "ftps:"]);

/**
 * Return true when `value` is safe to use as an <a href>.
 *
 * Parses with the WHATWG URL parser and checks the resolved scheme against an
 * allowlist, rather than matching dangerous schemes with a denylist regex. A
 * denylist such as /^javascript:/ is bypassable with obfuscated input like
 * "java\tscript:" (browsers strip the tab before navigating) or a leading C0
 * control character; the URL parser normalises those away so the real scheme is
 * always tested. This mirrors the safeHref helpers in search.js / chat.js.
 *
 * @param {string} value
 * @returns {boolean}
 */
export function isSafeHref(value) {
  if (typeof value !== "string") return false;
  // Browsers ignore ASCII tab/newline/CR inside URLs; strip them (plus
  // surrounding whitespace / leading control chars) before parsing so the
  // scheme cannot be hidden from the check.
  const cleaned = value.replace(/[\t\n\r]/g, "").trim();
  if (cleaned === "") return false;
  try {
    return SAFE_HREF_SCHEMES.has(new URL(cleaned, window.location.href).protocol);
  } catch {
    // Malformed URL — treat as unsafe.
    return false;
  }
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

    if (DROP_WITH_CONTENT.has(tag)) {
      // See the DROP_WITH_CONTENT comment above for why each member is
      // dropped whole here instead of being unwrapped.
      return null;
    }

    if (!ALLOWED_TAGS.has(tag)) {
      // Disallowed tag: unwrap — keep its sanitized children in a fragment.
      const frag = document.createDocumentFragment();
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
      if (tag === "A" && name === "href") {
        if (!isSafeHref(attr.value)) {
          node.removeAttribute("href");
        } else if (/^https?:\/\//i.test(attr.value.trim())) {
          node.setAttribute("target", "_blank");
          // nofollow matches the legacy JSP chat link policy (js/chat.js rel hook);
          // noopener/noreferrer are the static theme's added hardening. parity-r3.
          node.setAttribute("rel", "nofollow noopener noreferrer");
        }
      }
    }

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
export function sanitizeHtml(html) {
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
