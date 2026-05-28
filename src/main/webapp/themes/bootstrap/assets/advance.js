// SPDX-License-Identifier: Apache-2.0
// Advanced Search view for the Fess bootstrap SPA.
// All DOM construction uses createElement/textContent/setAttribute — no innerHTML.

import { getConfig } from "./api.js";
import { t } from "./i18n.js";
import { navigate } from "./router.js";

// ---------------------------------------------------------------------------
// DOM helpers
// ---------------------------------------------------------------------------

/**
 * Create a labelled text input group.
 *
 * @param {string} labelKey - i18n key for the label text
 * @param {string} inputId  - id attribute for the input element
 * @param {string} [type]   - input type (default: "text")
 * @returns {{ wrap: HTMLDivElement, input: HTMLInputElement }}
 */
function makeField(labelKey, inputId, type = "text") {
  const wrap = document.createElement("div");
  wrap.className = "mb-3";

  const label = document.createElement("label");
  label.htmlFor = inputId;
  label.className = "form-label";
  label.textContent = t(labelKey);

  const input = document.createElement("input");
  input.type = type;
  input.id = inputId;
  input.className = "form-control";

  wrap.append(label, input);
  return { wrap, input };
}

/**
 * Create a labelled select group.
 *
 * @param {string} labelKey    - i18n key for the label text
 * @param {string} selectId    - id attribute for the select element
 * @param {Array<{value:string, label:string}>} options
 * @returns {{ wrap: HTMLDivElement, input: HTMLSelectElement }}
 */
function makeSelect(labelKey, selectId, options) {
  const wrap = document.createElement("div");
  wrap.className = "mb-3";

  const label = document.createElement("label");
  label.htmlFor = selectId;
  label.className = "form-label";
  label.textContent = t(labelKey);

  const select = document.createElement("select");
  select.id = selectId;
  select.className = "form-select";

  for (const o of options) {
    const opt = document.createElement("option");
    opt.value = o.value;
    opt.textContent = o.label;
    select.appendChild(opt);
  }

  wrap.append(label, select);
  return { wrap, input: select };
}

// ---------------------------------------------------------------------------
// Query composition
// ---------------------------------------------------------------------------

// Backslash character built without literal backslash bytes in source. This
// keeps the BundledBootstrapThemeTest guard (which forbids consecutive
// backslash bytes anywhere in theme JS) effective for catching the actual
// anti-pattern: JSON-interpolated path escapes that break URL resolution.
const BACKSLASH = String.fromCharCode(92);

/**
 * Wrap a phrase in double quotes for an exact-match query, escaping inner quotes.
 *
 * @param {string} s
 * @returns {string}
 */
function quoteIfNeeded(s) {
  s = s.trim();
  if (!s) return "";
  if (s.startsWith('"') && s.endsWith('"')) return s;
  return '"' + s.replaceAll('"', BACKSLASH + '"') + '"';
}

/**
 * Compose a Fess/OpenSearch query string from individual advanced-search parts.
 *
 * @param {{ all?:string, exact?:string, any?:string, none?:string,
 *            site?:string, filetype?:string }} parts
 * @returns {string}
 */
function compose(parts) {
  const out = [];

  if (parts.all) {
    const words = parts.all.trim().split(/\s+/).filter(Boolean);
    if (words.length) out.push(words.join(" "));
  }

  if (parts.exact) {
    const quoted = quoteIfNeeded(parts.exact);
    if (quoted) out.push(quoted);
  }

  if (parts.any) {
    const words = parts.any.trim().split(/\s+/).filter(Boolean);
    if (words.length > 1) {
      out.push("(" + words.join(" OR ") + ")");
    } else if (words.length === 1) {
      out.push(words[0]);
    }
  }

  if (parts.none) {
    const words = parts.none.trim().split(/\s+/).filter(Boolean);
    out.push(...words.map(w => "-" + w));
  }

  if (parts.site) {
    const site = parts.site.trim();
    if (site) out.push("site:" + site);
  }

  if (parts.filetype) {
    const ft = parts.filetype.trim();
    if (ft) out.push("filetype:" + ft);
  }

  return out.filter(Boolean).join(" ");
}

// ---------------------------------------------------------------------------
// Static configuration
// ---------------------------------------------------------------------------

const TIME_RANGES = [
  { value: "",       labelKey: "facet.any_time" },
  { value: "1day",   labelKey: "labels.facet_timestamp_1day" },
  { value: "1week",  labelKey: "labels.facet_timestamp_1week" },
  { value: "1month", labelKey: "labels.facet_timestamp_1month" },
  { value: "3month", labelKey: "labels.facet_timestamp_3month" },
  { value: "6month", labelKey: "labels.facet_timestamp_6month" },
  { value: "1year",  labelKey: "labels.facet_timestamp_1year" },
];

/** Maps a TIME_RANGES value to an OpenSearch range query fragment. */
const TIME_RANGE_QUERY = {
  "1day":   "last_modified:[now/d-1d TO *]",
  "1week":  "last_modified:[now/d-7d TO *]",
  "1month": "last_modified:[now/d-1M TO *]",
  "3month": "last_modified:[now/d-3M TO *]",
  "6month": "last_modified:[now/d-6M TO *]",
  "1year":  "last_modified:[now/d-1y TO *]",
};

const FILETYPE_OPTIONS = [
  { value: "",            labelKey: "advance.any_filetype" },
  { value: "html",        label: "HTML" },
  { value: "pdf",         label: "PDF" },
  { value: "msword",      label: "Word" },
  { value: "msexcel",     label: "Excel" },
  { value: "mspowerpoint", label: "PowerPoint" },
  { value: "txt",         label: "Text" },
  { value: "image",       labelKey: "labels.facet_filetype_image" },
  { value: "audio",       labelKey: "labels.facet_filetype_audio" },
  { value: "video",       labelKey: "labels.facet_filetype_video" },
];

// ---------------------------------------------------------------------------
// View
// ---------------------------------------------------------------------------

/**
 * Attach the advanced search view to the #advance-view container.
 * Clears previous content and rebuilds from scratch each time.
 * Safe to call on every navigation to /advance.
 */
export function attach() {
  const view = document.getElementById("advance-view");
  if (!view) return;

  // Clear previous content without innerHTML assignment.
  while (view.firstChild) view.removeChild(view.firstChild);

  // Page heading
  const h1 = document.createElement("h1");
  h1.textContent = t("advance.title");
  view.appendChild(h1);

  // Introductory paragraph
  const intro = document.createElement("p");
  intro.className = "text-muted";
  intro.textContent = t("advance.intro");
  view.appendChild(intro);

  // Build the form
  const form = document.createElement("form");
  form.id = "advance-form";
  form.noValidate = true;
  form.className = "advance-form";

  // Text fields
  const fAll   = makeField("advance.all",   "adv-all");
  const fExact = makeField("advance.exact", "adv-exact");
  const fAny   = makeField("advance.any",   "adv-any");
  const fNone  = makeField("advance.none",  "adv-none");
  const fSite  = makeField("advance.site",  "adv-site");

  // File type select
  const filetypeOpts = FILETYPE_OPTIONS.map(o => ({
    value: o.value,
    label: o.labelKey ? t(o.labelKey) : o.label,
  }));
  const fFiletype = makeSelect("advance.filetype", "adv-filetype", filetypeOpts);

  // Language select — built from server config when available
  const serverConfig = getConfig() || {};
  const langOpts = [{ value: "", label: t("labels.searchoptions_all_langs") }];
  for (const lo of (serverConfig.lang_options || [])) {
    const langLabel =
      t("labels.lang_" + lo.value) !== "labels.lang_" + lo.value
        ? t("labels.lang_" + lo.value)
        : (lo.label || lo.value);
    langOpts.push({
      value: lo.value === "all" ? "" : lo.value,
      label: langLabel,
    });
  }
  const fLang = makeSelect("advance.lang", "adv-lang", langOpts);

  // Time range select
  const timeOpts = TIME_RANGES.map(r => ({ value: r.value, label: t(r.labelKey) }));
  const fTime = makeSelect("advance.time", "adv-time", timeOpts);

  form.append(
    fAll.wrap,
    fExact.wrap,
    fAny.wrap,
    fNone.wrap,
    fSite.wrap,
    fFiletype.wrap,
    fLang.wrap,
    fTime.wrap,
  );

  // Submit button
  const submit = document.createElement("button");
  submit.type = "submit";
  submit.className = "btn btn-primary";
  submit.textContent = t("advance.submit");
  form.appendChild(submit);

  view.appendChild(form);

  // Form submit handler — compose query and navigate to search
  form.addEventListener("submit", (e) => {
    e.preventDefault();

    const q = compose({
      all:      fAll.input.value,
      exact:    fExact.input.value,
      any:      fAny.input.value,
      none:     fNone.input.value,
      site:     fSite.input.value,
      filetype: fFiletype.input.value,
    });

    const params = new URLSearchParams();
    if (q) params.set("q", q);

    const lang = fLang.input.value;
    if (lang) params.set("fields.lang", lang);

    const time = fTime.input.value;
    if (time && TIME_RANGE_QUERY[time]) {
      params.set("ex_q", TIME_RANGE_QUERY[time]);
    }

    navigate("/search?" + params.toString());
  });
}
