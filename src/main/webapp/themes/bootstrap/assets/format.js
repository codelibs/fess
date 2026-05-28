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
