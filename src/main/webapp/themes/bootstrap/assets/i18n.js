// Bootstrap theme i18n loader. Resolves the runtime locale once at boot
// using navigator.language with primary-subtag and English fallbacks per spec §4.6.

const SUPPORTED = ["en", "ja"];
let messages = {};
let locale = "en";

function pickLocale() {
  const raw = (navigator.language || "en").toLowerCase();
  if (SUPPORTED.includes(raw)) return raw;
  const primary = raw.split("-")[0];
  if (SUPPORTED.includes(primary)) return primary;
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
  applyDom(document);
}

export function t(key, params) {
  let s = messages[key] || key;
  if (params) for (const [k, v] of Object.entries(params)) s = s.replace("{" + k + "}", String(v));
  return s;
}

export function getLocale() { return locale; }

export function applyDom(root) {
  root.querySelectorAll("[data-i18n]").forEach(el => { el.textContent = t(el.dataset.i18n); });
  root.querySelectorAll("[data-i18n-placeholder]").forEach(el => { el.setAttribute("placeholder", t(el.dataset.i18nPlaceholder)); });
  root.querySelectorAll("[data-i18n-aria-label]").forEach(el => { el.setAttribute("aria-label", t(el.dataset.i18nAriaLabel)); });
}
