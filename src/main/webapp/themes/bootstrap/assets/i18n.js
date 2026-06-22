// Bootstrap theme i18n loader. Resolves the runtime locale once at boot
// using navigator.language with primary-subtag and English fallbacks per spec §4.6.

const SUPPORTED = ["de", "en", "es", "fr", "hi", "id", "it", "ja", "ko", "nl", "pl", "pt-BR", "ru", "tr", "zh-CN", "zh-TW"];
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
 * Substitution supports two modes:
 *  - Named placeholders (Object): {name} is replaced by params.name.
 *    e.g. t("result.click_count", { n: 42 }) → "Clicks: 42"
 *  - Positional placeholders (Array): {0}, {1}, … are replaced by params[0], params[1], …
 *    e.g. t("labels.search_result_status", [total, start, end, query])
 *    → "Results 1 - 10 of 100 for foo"
 *
 * Both modes use the same {key} syntax (matching JSP MessageFormat style for
 * positional indices). Numeric string keys in an Object are also treated as
 * positional by the pattern, so { "0": "foo" } replaces {0}.
 *
 * WARNING: substituted values are treated as plain text and must not contain
 * curly-brace placeholder patterns themselves (e.g. "{n}"). If a value might
 * contain braces, escape it before passing: val.replace(/\{/g, "&#123;").
 *
 * @param {string} key
 * @param {Array|object} [params] - positional array or named key→value map
 * @returns {string}
 */
export function t(key, params) {
  let s = messages[key] || key;
  if (params != null) {
    s = s.replace(/\{([^{}]+)\}/g, (_, k) => {
      const v = Array.isArray(params) ? params[Number(k)] : params[k];
      return v == null ? "" : String(v);
    });
  }
  return s;
}

export function getLocale() { return locale; }

/**
 * Return a human-readable language name for a BCP-47 or underscore-variant code.
 *
 * Resolution order:
 *  1. Theme i18n key "labels.lang_<value>" — used when it resolves to something
 *     other than the raw key string.
 *  2. Intl.DisplayNames in the current UI locale — robust for the ~53 server
 *     language codes that the theme i18n files do not cover.
 *  3. fallbackLabel or value as last resort.
 *
 * @param {string} value         - language code from the server (e.g. "ar", "pt_BR", "zh_CN")
 * @param {string} [fallbackLabel] - label supplied by server (used when i18n + Intl both fail)
 * @returns {string}
 */
export function languageLabel(value, fallbackLabel) {
  if (!value) return fallbackLabel || value || "";

  // Step 1: try theme i18n key
  const key = "labels.lang_" + value;
  const translated = t(key);
  if (translated !== key) return translated;

  // Step 2: normalize underscore variants (e.g. pt_BR → pt-BR) for Intl
  const normalized = value.replace(/_/g, "-");
  try {
    const dn = new Intl.DisplayNames([locale, "en"], { type: "language" });
    const name = dn.of(normalized);
    if (name && name !== normalized && name !== value) return name;
  } catch (e) {
    // Intl.DisplayNames not supported or invalid tag — fall through
  }

  // Step 3: fallback
  return fallbackLabel || value;
}

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
  root.querySelectorAll("[data-i18n-alt]").forEach(el => { el.setAttribute("alt", t(el.dataset.i18nAlt)); });
}
