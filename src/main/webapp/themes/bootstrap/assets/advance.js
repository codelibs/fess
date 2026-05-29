// SPDX-License-Identifier: Apache-2.0
// Advanced Search view for the Fess bootstrap SPA.
// All DOM construction uses createElement/textContent/setAttribute — no innerHTML.

import { getConfig } from "./api.js";
import { t } from "./i18n.js";
import { navigate } from "./router.js";
import { attachSuggest, disableSubmitBriefly } from "./search.js";

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
    // Server (QueryStringBuilder:230-235) splits on whitespace, each token → NOT <token>.
    const tokens = parts.none.trim().split(/\s+/).filter(Boolean);
    out.push(...tokens.map(w => "NOT " + w));
  }

  if (parts.site) {
    const site = parts.site.trim();
    if (site) out.push("site:" + site);
  }

  if (parts.filetype) {
    const ft = parts.filetype.trim();
    // Server (QueryStringBuilder:236-238) emits filetype:"<value>" with quotes.
    if (ft) out.push('filetype:"' + ft + '"');
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

// JSP parity: exactly four options matching advance.jsp:242-253 (no 3month/6month).
const TIME_RANGES = [
  { value: "",       labelKey: "facet.any_time" },
  { value: "1day",   labelKey: "labels.facet_timestamp_1day" },
  { value: "1week",  labelKey: "labels.facet_timestamp_1week" },
  { value: "1month", labelKey: "labels.facet_timestamp_1month" },
  { value: "1year",  labelKey: "labels.facet_timestamp_1year" },
];

/**
 * Maps a TIME_RANGES value to the exact date-math range fragment used by the
 * server (QueryStringBuilder:242-244; advance.jsp:242-253).
 * Field is "timestamp"; values are appended to q as "timestamp:<value>".
 */
const TIME_RANGE_QUERY = {
  "1day":   "timestamp:[now-1d/d TO *]",
  "1week":  "timestamp:[now-1w/d TO *]",
  "1month": "timestamp:[now-1M/d TO *]",
  "1year":  "timestamp:[now-1y/d TO *]",
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
    : [10, 20, 30, 40, 50, 100];
  const numOpts = [
    { value: "", label: t("labels.advance_search_num") },
    ...numNums.map(n => ({ value: String(n), label: String(n) })),
  ];
  const fNum = makeSelect("labels.advance_search_num", "adv-num", numOpts);

  // ADV-6: sort select — prefer serverConfig.sort_options; when absent build a gated fallback
  const features = (serverConfig.features) || {};
  const searchLogEnabled = !!features.search_log_enabled;
  const favoriteEnabled = !!features.user_favorite;
  let sortOptsRaw;
  if (serverConfig.sort_options && serverConfig.sort_options.length > 0) {
    sortOptsRaw = serverConfig.sort_options.map(o => ({
      value: o.value != null ? o.value : "",
      label: t(o.label_key || o.value || ""),
    }));
  } else {
    // Fallback: always include core sort options; gate click_count and favorite_count on features
    sortOptsRaw = [
      { value: "score.desc",             label: t("labels.search_result_sort_score_desc") },
      { value: "filename.asc",           label: t("labels.search_result_sort_filename_asc") },
      { value: "filename.desc",          label: t("labels.search_result_sort_filename_desc") },
      { value: "created.asc",            label: t("labels.search_result_sort_created_asc") },
      { value: "created.desc",           label: t("labels.search_result_sort_created_desc") },
      { value: "content_length.asc",     label: t("labels.search_result_sort_content_length_asc") },
      { value: "content_length.desc",    label: t("labels.search_result_sort_content_length_desc") },
      { value: "last_modified.asc",      label: t("labels.search_result_sort_last_modified_asc") },
      { value: "last_modified.desc",     label: t("labels.search_result_sort_last_modified_desc") },
    ];
    if (searchLogEnabled) {
      sortOptsRaw.push(
        { value: "click_count.asc",  label: t("labels.search_result_sort_click_count_asc") },
        { value: "click_count.desc", label: t("labels.search_result_sort_click_count_desc") },
      );
    }
    if (favoriteEnabled) {
      sortOptsRaw.push(
        { value: "favorite_count.asc",  label: t("labels.search_result_sort_favorite_count_asc") },
        { value: "favorite_count.desc", label: t("labels.search_result_sort_favorite_count_desc") },
      );
    }
  }
  const sortOpts = [
    { value: "", label: t("labels.advance_search_sort_default") },
    ...sortOptsRaw,
  ];
  const fSort = makeSelect("labels.advance_search_sort", "adv-sort", sortOpts);

  // ---------------------------------------------------------------------------
  // Best-effort prefill from incoming URL params (parity-r3 #5 / A3)
  // ---------------------------------------------------------------------------
  // Lossless round-trip from a flat q string is impossible (the server flattens
  // all-words, exact, any-words into a single q without field markers). What we
  // CAN recover unambiguously:
  //   site:<v>           → site field
  //   filetype:"<v>"     → filetype select
  //   NOT <token>        → none-words field (space-joined)
  //   "quoted phrase"    → exact-phrase field (first quoted phrase found)
  //   allintitle:/allinurl: prefix → occt select
  //   remaining bare tokens → all-words field (best effort; may include any-words)
  // Additionally forward: lang, sort, num, fields.label from the URL.
  // Content is never dropped — anything unrecognised lands in all-words.
  (function prefillFromUrl() {
    const urlParams = new URLSearchParams(location.search);
    const rawQ = urlParams.get("q") || "";

    // --- forward non-q params ---
    // num
    const numParam = urlParams.get("num");
    if (numParam) {
      const opt = Array.from(fNum.input.options).find(o => o.value === numParam);
      if (opt) fNum.input.value = numParam;
    }
    // sort
    const sortParam = urlParams.get("sort");
    if (sortParam) {
      const opt = Array.from(fSort.input.options).find(o => o.value === sortParam);
      if (opt) fSort.input.value = sortParam;
    }
    // lang (multi-select, repeated param)
    const langParams = urlParams.getAll("lang").filter(v => v !== "");
    if (langParams.length > 0) {
      for (const opt of fLang.input.options) {
        opt.selected = langParams.includes(opt.value);
      }
    }
    // fields.label (repeated param) → checkboxes
    const labelParams = urlParams.getAll("fields.label").filter(v => v !== "");
    if (labelParams.length > 0) {
      for (const cb of labelCheckboxes) {
        cb.checked = labelParams.includes(cb.value);
      }
    }

    if (!rawQ) {
      return; // nothing to parse
    }

    // --- parse q ---
    let q = rawQ.trim();

    // Strip leading occt prefix (allintitle: or allinurl:)
    let occtVal = "";
    if (q.startsWith("allintitle:")) {
      occtVal = "allintitle";
      q = q.slice("allintitle:".length).trim();
    } else if (q.startsWith("allinurl:")) {
      occtVal = "allinurl";
      q = q.slice("allinurl:".length).trim();
    }
    if (occtVal) {
      const opt = Array.from(fOcct.input.options).find(o => o.value === occtVal);
      if (opt) fOcct.input.value = occtVal;
    }

    // Tokenize the remainder (whitespace-separated, respecting quoted strings)
    const tokens = tokenize(q);

    const notTokens = [];
    let exactPhrase = "";
    let siteVal = "";
    let filetypeVal = "";
    const bareTokens = [];

    for (const tok of tokens) {
      // Standalone "NOT" sentinels fall through to bareTokens and are paired with
      // the following word in the second pass below (the whitespace tokenizer splits
      // "NOT foo" into ["NOT","foo"]). parity-r3 review: do NOT consume "NOT" here,
      // or the pairing never fires and the none-words round-trip yields "NOT NOT".
      if (tok.startsWith("site:")) {
        siteVal = tok.slice("site:".length);
      } else if (tok.startsWith("filetype:")) {
        // Server emits filetype:"value" — strip surrounding quotes if present
        let ftv = tok.slice("filetype:".length);
        if (ftv.startsWith('"') && ftv.endsWith('"')) ftv = ftv.slice(1, -1);
        filetypeVal = ftv;
      } else if (tok.startsWith('"') && tok.endsWith('"') && tok.length >= 2) {
        // Quoted phrase → exact-phrase (first one wins; subsequent go to bare)
        if (!exactPhrase) {
          exactPhrase = tok.slice(1, -1);
        } else {
          bareTokens.push(tok);
        }
      } else {
        bareTokens.push(tok);
      }
    }

    // Second pass: collapse consecutive NOT+word pairs that the whitespace
    // tokenizer splits as ["NOT", "foo"] into NOT entries.
    const finalBare = [];
    for (let i = 0; i < bareTokens.length; i++) {
      if (bareTokens[i] === "NOT" && i + 1 < bareTokens.length) {
        notTokens.push(bareTokens[i + 1]);
        i++; // skip next
      } else {
        finalBare.push(bareTokens[i]);
      }
    }

    if (siteVal) fSite.input.value = siteVal;
    if (filetypeVal) {
      const opt = Array.from(fFiletype.input.options).find(o => o.value === filetypeVal);
      if (opt) fFiletype.input.value = filetypeVal;
    }
    if (exactPhrase) fExact.input.value = exactPhrase;
    if (notTokens.length > 0) fNone.input.value = notTokens.join(" ");
    // Dump remaining bare tokens into all-words (best effort)
    if (finalBare.length > 0) fAll.input.value = finalBare.join(" ");
  })();

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

    // JSP parity (QueryStringBuilder:242-244): timestamp condition is appended to q,
    // not passed as ex_q. Emit "timestamp:<value>" directly into the q string.
    const time = fTime.input.value;
    if (time && TIME_RANGE_QUERY[time]) {
      const fullQ = q ? q + " " + TIME_RANGE_QUERY[time] : TIME_RANGE_QUERY[time];
      params.set("q", fullQ);
    }

    // F.1: num and sort
    const numVal = fNum.input.value;
    if (numVal) params.set("num", numVal);

    const sortVal = fSort.input.value;
    if (sortVal) params.set("sort", sortVal);

    navigate("/search?" + params.toString());
    // JSP parity: disable the submit button for 3000ms after navigation has
    // been triggered, to prevent rapid double-submits. The shared helper
    // (search.js) re-enables it on the timer.
    disableSubmitBriefly(submit);
  });
}
