// Bootstrap theme i18n loader. Resolves the runtime locale once at boot
// using navigator.language with primary-subtag and English fallbacks per spec §4.6.

const SUPPORTED = ["en", "ja", "de", "es", "fr", "ko", "pt-BR", "zh-CN"];
let messages = {};
let locale = "en";

function pickLocale() {
  const raw = (navigator.language || "en");
  // Case-insensitive exact match first (e.g. "pt-BR", "zh-CN").
  const lower = raw.toLowerCase();
  for (const s of SUPPORTED) {
    if (s.toLowerCase() === lower) return s;
  }
  // Primary-subtag match (e.g. "ja-JP" → "ja", "de-AT" → "de").
  const primary = lower.split("-")[0];
  for (const s of SUPPORTED) {
    if (s.toLowerCase() === primary) return s;
  }
  return "en";
}

export async function init() {
  locale = pickLocale();
  try {
    const r = await fetch(`/themes/bootstrap/i18n/messages.${locale}.json`, { credentials: "same-origin" });
    if (!r.ok) throw new Error("i18n bundle " + locale + " HTTP " + r.status);
    messages = await r.json();
  } catch (e) {
    // Fall back to English on any failure (spec §4.6).
    if (locale !== "en") {
      try {
        const r2 = await fetch("/themes/bootstrap/i18n/messages.en.json", { credentials: "same-origin" });
        messages = await r2.json();
        locale = "en";
      } catch { messages = {}; }
    }
  }
  document.documentElement.lang = locale;
  // Update document.title from the page.title i18n key when available.
  const pageTitle = messages["page.title"];
  if (pageTitle) document.title = pageTitle;
  applyDom(document);
}

/**
 * Translate a message key with optional parameter substitution.
 *
 * Substitution uses replaceAll so every occurrence of {key} is replaced.
 *
 * WARNING: substituted values are treated as plain text and must not contain
 * curly-brace placeholder patterns themselves (e.g. "{n}"). If a value might
 * contain braces, escape it before passing: val.replace(/\{/g, "&#123;").
 *
 * @param {string} key
 * @param {object} [params] - key→value substitution map, e.g. { n: 42 }
 * @returns {string}
 */
export function t(key, params) {
  let s = messages[key] || key;
  if (params) for (const [k, v] of Object.entries(params)) s = s.replaceAll("{" + k + "}", String(v));
  return s;
}

export function getLocale() { return locale; }

/**
 * Apply i18n translations to all data-i18n* elements within root.
 * Call this on any dynamically-inserted subtree to localise new content.
 *
 * @param {Document|Element} root
 */
export function applyDom(root) {
  root.querySelectorAll("[data-i18n]").forEach(el => { el.textContent = t(el.dataset.i18n); });
  root.querySelectorAll("[data-i18n-placeholder]").forEach(el => { el.setAttribute("placeholder", t(el.dataset.i18nPlaceholder)); });
  root.querySelectorAll("[data-i18n-aria-label]").forEach(el => { el.setAttribute("aria-label", t(el.dataset.i18nAriaLabel)); });
}
