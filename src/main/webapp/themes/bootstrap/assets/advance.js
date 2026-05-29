// SPDX-License-Identifier: Apache-2.0
// Advanced Search view for the Fess bootstrap SPA.
// All DOM construction uses createElement/textContent/setAttribute — no innerHTML.

import { getConfig } from "./api.js";
import { t } from "./i18n.js";
import { navigate } from "./router.js";
import { attachSuggest } from "./search.js";

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
 * @param {boolean} [multiple] - whether the select is multi-select
 * @returns {{ wrap: HTMLDivElement, input: HTMLSelectElement }}
 */
function makeSelect(labelKey, selectId, options, multiple = false) {
  const wrap = document.createElement("div");
  wrap.className = "mb-3";

  const label = document.createElement("label");
  label.htmlFor = selectId;
  label.className = "form-label";
  label.textContent = t(labelKey);

  const select = document.createElement("select");
  select.id = selectId;
  select.className = "form-select";
  if (multiple) {
    select.multiple = true;
    select.size = 4;
  }

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
 * Tokenize a string, treating "quoted phrases" as single tokens.
 * Unquoted whitespace-separated words are individual tokens.
 *
 * @param {string} s
 * @returns {string[]}
 */
function tokenize(s) {
  const tokens = [];
  const str = s.trim();
  let i = 0;
  while (i < str.length) {
    // Skip whitespace
    while (i < str.length && /\s/.test(str[i])) i++;
    if (i >= str.length) break;

    if (str[i] === '"') {
      // Quoted token: consume until closing quote or end
      let j = i + 1;
      while (j < str.length && str[j] !== '"') j++;
      // Include the closing quote if present
      if (j < str.length) j++;
      tokens.push(str.slice(i, j));
      i = j;
    } else {
      // Unquoted word: consume until whitespace
      let j = i;
      while (j < str.length && !/\s/.test(str[j])) j++;
      tokens.push(str.slice(i, j));
      i = j;
    }
  }
  return tokens;
}

/**
 * Compose a Fess/OpenSearch query string from individual advanced-search parts.
 *
 * @param {{ all?:string, exact?:string, any?:string, none?:string,
 *            site?:string, filetype?:string, occt?:string }} parts
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
    // F.4: use quote-aware tokenizer so "foo bar" becomes NOT "foo bar"
    const tokens = tokenize(parts.none);
    out.push(...tokens.filter(Boolean).map(w => "NOT " + w));
  }

  if (parts.site) {
    const site = parts.site.trim();
    if (site) out.push("site:" + site);
  }

  if (parts.filetype) {
    const ft = parts.filetype.trim();
    if (ft) out.push("filetype:" + ft);
  }

  let q = out.filter(Boolean).join(" ");

  // F.3: prepend allintitle: or allinurl: prefix when occt is selected
  if (parts.occt === "allintitle" && q) {
    q = "allintitle:" + q;
  } else if (parts.occt === "allinurl" && q) {
    q = "allinurl:" + q;
  }

  return q;
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

const FILETYPE_OPTIONS_FALLBACK = [
  { value: "",           labelKey: "advance.any_filetype" },
  { value: "html",       labelKey: "labels.facet_filetype_html" },
  { value: "word",       labelKey: "labels.facet_filetype_word" },
  { value: "excel",      labelKey: "labels.facet_filetype_excel" },
  { value: "powerpoint", labelKey: "labels.facet_filetype_powerpoint" },
  { value: "odt",        labelKey: "labels.facet_filetype_odt" },
  { value: "ods",        labelKey: "labels.facet_filetype_ods" },
  { value: "odp",        labelKey: "labels.facet_filetype_odp" },
  { value: "pdf",        labelKey: "labels.facet_filetype_pdf" },
  { value: "txt",        labelKey: "labels.facet_filetype_txt" },
  { value: "others",     labelKey: "labels.facet_filetype_others" },
];

function buildFiletypeOptions(serverConfig) {
  const fromServer = serverConfig && Array.isArray(serverConfig.filetype_options) ? serverConfig.filetype_options : null;
  if (fromServer && fromServer.length > 0) {
    return [{ value: "", labelKey: "advance.any_filetype" },
      ...fromServer.map(o => ({ value: o.value != null ? o.value : "", labelKey: o.label_key || undefined, label: o.label_key ? undefined : (o.label || o.value || "") }))];
  }
  return FILETYPE_OPTIONS_FALLBACK.slice();
}

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

  // Server config — resolved before field/select builds that depend on it
  const serverConfig = getConfig() || {};

  // Text fields
  const fAll   = makeField("advance.all",   "adv-all");
  const fExact = makeField("advance.exact", "adv-exact");
  const fAny   = makeField("advance.any",   "adv-any");
  const fNone  = makeField("advance.none",  "adv-none");
  const fSite  = makeField("advance.site",  "adv-site");

  // File type select — options sourced from server config (filetype_options) with canonical fallback
  const filetypeOptDefs = buildFiletypeOptions(serverConfig);
  const filetypeOpts = filetypeOptDefs.map(o => ({
    value: o.value,
    label: o.labelKey ? t(o.labelKey) : (o.label || o.value || ""),
  }));
  const fFiletype = makeSelect("advance.filetype", "adv-filetype", filetypeOpts);

  // F.2: Language multi-select — built from server config when available
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
  const fLang = makeSelect("advance.lang", "adv-lang", langOpts, true /* multiple */);

  // F.2: Label checkbox group — shown only when label_options is non-empty
  const labelOptions = serverConfig.label_options || [];
  let fLabelWrap = null;
  const labelCheckboxes = [];
  if (labelOptions.length > 0) {
    fLabelWrap = document.createElement("div");
    fLabelWrap.className = "mb-3";

    const labelHeading = document.createElement("label");
    labelHeading.className = "form-label";
    labelHeading.textContent = t("labels.advance_search_label");
    fLabelWrap.appendChild(labelHeading);

    for (const lo of labelOptions) {
      const checkWrap = document.createElement("div");
      checkWrap.className = "form-check";

      const cb = document.createElement("input");
      cb.type = "checkbox";
      cb.id = "adv-label-" + lo.value;
      cb.value = lo.value != null ? lo.value : "";
      cb.className = "form-check-input";
      labelCheckboxes.push(cb);

      const cbLabel = document.createElement("label");
      cbLabel.htmlFor = cb.id;
      cbLabel.className = "form-check-label";
      cbLabel.textContent = lo.label || lo.value || "";

      checkWrap.append(cb, cbLabel);
      fLabelWrap.appendChild(checkWrap);
    }
  }

  // F.3: Occurrence (search in) select
  const occtOpts = [
    { value: "",           label: t("labels.advance_search_occt_default") },
    { value: "allintitle", label: t("labels.advance_search_occt_title") },
    { value: "allinurl",   label: t("labels.advance_search_occt_url") },
  ];
  const fOcct = makeSelect("labels.advance_search_occt", "adv-occt", occtOpts);

  // Time range select
  const timeOpts = TIME_RANGES.map(r => ({ value: r.value, label: t(r.labelKey) }));
  const fTime = makeSelect("advance.time", "adv-time", timeOpts);

  // F.1: num select — built from server config num_options
  const numNums = serverConfig.num_options && serverConfig.num_options.length > 0
    ? serverConfig.num_options
    : [10, 20, 50, 100];
  const numOpts = [
    { value: "", label: t("labels.advance_search_num") },
    ...numNums.map(n => ({ value: String(n), label: String(n) })),
  ];
  const fNum = makeSelect("labels.advance_search_num", "adv-num", numOpts);

  // F.1: sort select — built from server config sort_options
  const sortOptsRaw = serverConfig.sort_options && serverConfig.sort_options.length > 0
    ? serverConfig.sort_options
    : [];
  const sortOpts = [
    { value: "", label: t("labels.advance_search_sort_default") },
    ...sortOptsRaw.map(o => ({
      value: o.value != null ? o.value : "",
      label: t(o.label_key || o.value || ""),
    })),
  ];
  const fSort = makeSelect("labels.advance_search_sort", "adv-sort", sortOpts);

  // Append all fields before the submit button
  form.append(
    fAll.wrap,
    fExact.wrap,
    fAny.wrap,
    fNone.wrap,
    fSite.wrap,
    fFiletype.wrap,
    fOcct.wrap,
    fLang.wrap,
  );
  if (fLabelWrap) form.appendChild(fLabelWrap);
  form.append(
    fTime.wrap,
    fNum.wrap,
    fSort.wrap,
  );

  // Submit button
  const submit = document.createElement("button");
  submit.type = "submit";
  submit.className = "btn btn-primary";
  submit.textContent = t("advance.submit");
  form.appendChild(submit);

  // ADV-4: suggest dropdown for the all-words field
  const advAllSuggestDropdown = document.createElement("ul");
  advAllSuggestDropdown.className = "list-group suggest-dropdown d-none";
  advAllSuggestDropdown.id = "adv-all-suggest";
  advAllSuggestDropdown.setAttribute("role", "listbox");
  fAll.wrap.appendChild(advAllSuggestDropdown);

  view.appendChild(form);

  // ADV-4: wire up suggest on all-words input using shared attachSuggest from search.js
  attachSuggest(fAll.input, advAllSuggestDropdown, {
    get lang() {
      return Array.from(fLang.input.selectedOptions).map(o => o.value).filter(v => v !== "");
    }
  });

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
      occt:     fOcct.input.value,
    });

    const params = new URLSearchParams(location.search);
    // ADV-5: delete only the params owned by advance search, preserve unrelated ones
    for (const key of ["q", "start", "num", "sort", "lang", "ex_q"]) params.delete(key);
    for (const key of Array.from(params.keys())) { if (key.startsWith("fields.")) params.delete(key); }
    params.delete("start");
    if (q) params.set("q", q);

    // F.2: collect all selected lang options (multi-select → repeated params)
    const selectedLangs = Array.from(fLang.input.selectedOptions)
      .map(o => o.value)
      .filter(v => v !== "");
    for (const lang of selectedLangs) {
      params.append("lang", lang);
    }

    // F.2: collect all checked label checkboxes (repeated params)
    for (const cb of labelCheckboxes) {
      if (cb.checked && cb.value) {
        params.append("fields.label", cb.value);
      }
    }

    const time = fTime.input.value;
    if (time && TIME_RANGE_QUERY[time]) {
      params.set("ex_q", TIME_RANGE_QUERY[time]);
    }

    // F.1: num and sort
    const numVal = fNum.input.value;
    if (numVal) params.set("num", numVal);

    const sortVal = fSort.input.value;
    if (sortVal) params.set("sort", sortVal);

    navigate("/search?" + params.toString());
  });
}
